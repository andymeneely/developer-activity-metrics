package edu.ncsu.csc.realsearch.apmeneel.devactivity.analysis;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.commons.collections15.Transformer;

import com.mysql.jdbc.Connection;

import edu.ncsu.csc.realsearch.apmeneel.devactivity.AnalysisAggregator;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.DBUtil;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.Developer;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.DeveloperNetwork;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.DeveloperNetworkCache;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.FileSet;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.factory.DBDevAdjacencyFactory;
import edu.uci.ics.jung.algorithms.scoring.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.algorithms.shortestpath.DistanceStatistics;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.Graph;

public class DeveloperNetworkAnalysis {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(DeveloperNetworkAnalysis.class);
	private final DBUtil dbUtil;
	private DeveloperNetwork dn;
	private BetweennessCentrality<Developer, FileSet> bc;
	private String experiment;

	public DeveloperNetworkAnalysis(DBUtil dbUtil, Properties props) {
		this.dbUtil = dbUtil;
		this.experiment = props.getProperty("history.experiment");
	}

	public void run() throws Exception {
		log.debug("Building developer network...");
		dn = new DBDevAdjacencyFactory(dbUtil).build();
		log.debug("Running centrality calculations...");
		loadBetweenness(dn);
		// log.debug("Running all pairs distance...");
		// loadDistances(dn, new ComplementFileSetSizeDistance(1039));
		// loadDistances(dn, new InverseFileSetSizeDistance());
		log.debug("Calculating network results...");
		runNetworkAnalysis(dn);
		log.debug("Calculating network turnover...");
		runNetworkTurnover(dn);
		log.debug("Robustness simulations...");
		runRobustnessSimulations(dn);
	}

	private void loadBetweenness(DeveloperNetwork dn) throws SQLException {
		log.debug("\tComputing betweenness...");
		bc = new BetweennessCentrality<Developer, FileSet>(dn.getGraph());
		Collection<Developer> devs = dn.getGraph().getVertices();
		log.debug("\tLoading dev betweenness & degree scores...");
		Connection conn = dbUtil.getConnection();
		PreparedStatement ps = conn
				.prepareStatement("INSERT INTO DevNetworkAnalysis(author, degree, betweenness) VALUES (?,?,?)");
		int e = dn.getGraph().getVertexCount();
		double normalizeBy = (e - 1) * (e - 2) / 2.0;

		for (Developer dev : devs) {
			ps.setString(1, dev.getName());
			ps.setDouble(2, dn.getGraph().degree(dev));
			ps.setDouble(3, bc.getVertexScore(dev) / normalizeBy);
			ps.addBatch();
		}
		log.debug("\tExecuting batch insert ...");
		ps.executeBatch();

		log.debug("\tLoading edge betweenness scores...");
		PreparedStatement ps2 = conn
				.prepareStatement("INSERT INTO EdgeBetweenness(filepath, edgebetweenness) VALUES (?,?)");
		Collection<FileSet> edges = dn.getGraph().getEdges();
		for (FileSet fileSet : edges) {
			List<String> files = fileSet.getFiles();
			for (String filepath : files) {
				ps2.setString(1, filepath);
				ps2.setDouble(2, bc.getEdgeScore(fileSet));
				ps2.addBatch();
			}
		}
		log.debug("\tExecuting batch insert ...");
		ps2.executeBatch();
		DBUtil.closeConnection(conn, ps);
	}

	private void loadDistances(DeveloperNetwork dn, Transformer<FileSet, Number> fDistance) throws Exception {
		Graph<Developer, FileSet> graph = dn.getGraph();
		log.debug("\tComputing distances...");
		DijkstraShortestPath<Developer, FileSet> sssp = new DijkstraShortestPath<Developer, FileSet>(graph, fDistance,
				true);
		DijkstraShortestPath<Developer, FileSet> ssspUnweighted = new DijkstraShortestPath<Developer, FileSet>(graph,
				true);
		Connection conn = dbUtil.getConnection();
		PreparedStatement ps = conn
				.prepareStatement("INSERT INTO DevNetworkDistance(dev1, dev2, distance,unweighteddistance) VALUES (?,?,?,?)");
		Collection<Developer> devs1 = graph.getVertices();
		Collection<Developer> devs2 = graph.getVertices();
		for (Developer dev1 : devs1) {
			for (Developer dev2 : devs2) {
				if (dev1.getName().compareTo(dev2.getName()) < 0) {
					ps.setString(1, dev1.getName());
					ps.setString(2, dev2.getName());
					Number distance = sssp.getDistance(dev1, dev2);
					Number unweightedDistance = ssspUnweighted.getDistance(dev1, dev2);
					if (distance != null) {
						ps.setDouble(3, distance.doubleValue());
						ps.setDouble(4, unweightedDistance.doubleValue());
						ps.addBatch();
					}
				}
			}
		}

		log.debug("\tExecuting batch insert ...");
		ps.executeBatch();
	}

	private void runNetworkAnalysis(DeveloperNetwork dn) throws SQLException {
		Connection conn = dbUtil.getConnection();
		Graph<Developer, FileSet> graph = dn.getGraph();
		int n = graph.getVertexCount();
		AnalysisAggregator.logResult(experiment, "Number of developers", n, conn);
		int e = graph.getEdgeCount();
		AnalysisAggregator.logResult(experiment, "Number of edges", e, conn);
		double density = (n - 1) * (n - 2) / 2;
		density = ((double) e) / density;
		AnalysisAggregator.logResult(experiment, "Density:      \t", density, conn);
		double diameter = DistanceStatistics.diameter(graph, new UnweightedShortestPath<Developer, FileSet>(graph),
				true);
		AnalysisAggregator.logResult(experiment, "Diameter (non-inf)", diameter, conn);
		DBUtil.closeConnection(conn, (PreparedStatement) null);
	}

	private void runNetworkTurnover(DeveloperNetwork dn) throws SQLException {
		Connection conn = dbUtil.getConnection();
		DeveloperNetwork previous = DeveloperNetworkCache.getInstance().get("previous");
		if (previous != null) {
			GraphDiff<Developer, FileSet> graphDiff = new GraphDiff<Developer, FileSet>(previous.getGraph(),
					dn.getGraph());
			AnalysisAggregator.logResult(experiment, "New Developers", graphDiff.getNewVertices().size(), conn);
			AnalysisAggregator.logResult(experiment, "Dropped Developers", graphDiff.getDroppedVertices().size(), conn);
			AnalysisAggregator.logResult(experiment, "Unchanged Developers", graphDiff.getUnchangedVertices().size(),
					conn);
		} else
			log.debug("No previous developer network - no turnover analysis for experiment " + experiment);
		DBUtil.closeConnection(conn, (PreparedStatement) null);
		DeveloperNetworkCache.getInstance().put("previous", dn);
	}

	private void runRobustnessSimulations(DeveloperNetwork dn) throws SQLException {
		Connection conn = dbUtil.getConnection();
		AnalysisAggregator.logResult(experiment, "Cut Developers", new CutVertices<Developer, FileSet>(dn.getGraph())
				.getCutVertices().size(), conn);
		DBUtil.closeConnection(conn, (PreparedStatement) null);
	}

	public DeveloperNetwork getDeveloperNetwork() {
		return dn;
	}

	public BetweennessCentrality<Developer, FileSet> getBetweennessCentrality() {
		return bc;
	}
}
