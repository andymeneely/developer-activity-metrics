package edu.ncsu.csc.realsearch.apmeneel.devactivity.analysis;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections15.Transformer;

import com.mysql.jdbc.Connection;

import edu.ncsu.csc.realsearch.apmeneel.devactivity.DBUtil;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.ComplementFileSetSizeDistance;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.Developer;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.DeveloperNetwork;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.FileSet;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.factory.DBDevAdjacencyFactory;
import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.algorithms.shortestpath.DistanceStatistics;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.Graph;

public class DeveloperNetworkAnalysis {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger
			.getLogger(DeveloperNetworkAnalysis.class);
	private final DBUtil dbUtil;
	private DeveloperNetwork dn;

	public DeveloperNetworkAnalysis(DBUtil dbUtil) {
		this.dbUtil = dbUtil;
	}

	public void run() throws Exception {
		log.debug("Building developer network...");
		dn = new DBDevAdjacencyFactory(dbUtil).build();
		log.debug("Running centrality calculations...");
		loadBetweenness(dn);
//		log.debug("Running all pairs distance...");
//		loadDistances(dn, new ComplementFileSetSizeDistance(1787));
		// loadDistances(dn, new InverseFileSetSizeDistance());
		log.debug("Calculating network results...");
		runNetworkAnalysis(dn);
	}

	private void loadBetweenness(DeveloperNetwork dn) throws SQLException {
		BetweennessCentrality<Developer, FileSet> bc = new BetweennessCentrality<Developer, FileSet>(dn
				.getGraph(), true, true);
		bc.setRemoveRankScoresOnFinalize(false);
		log.debug("\tComputing betweenness...");
		bc.evaluate();
		Collection<Developer> devs = dn.getGraph().getVertices();
		log.debug("\tLoading dev betweenness & degree scores...");
		Connection conn = dbUtil.getConnection();
		PreparedStatement ps = conn
				.prepareStatement("INSERT INTO DevNetworkAnalysis(author, degree, betweenness) VALUES (?,?,?)");
		for (Developer dev : devs) {
			ps.setString(1, dev.getName());
			ps.setDouble(2, dn.getGraph().degree(dev));
			ps.setDouble(3, bc.getVertexRankScore(dev));
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
				ps2.setDouble(2, bc.getEdgeRankScore(fileSet));
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
		DijkstraShortestPath<Developer, FileSet> sssp = new DijkstraShortestPath<Developer, FileSet>(graph,
				fDistance, true);
		Connection conn = dbUtil.getConnection();
		PreparedStatement ps = conn
				.prepareStatement("INSERT INTO DevNetworkDistance(dev1, dev2, distance) VALUES (?,?,?)");
		Collection<Developer> devs1 = graph.getVertices();
		Collection<Developer> devs2 = graph.getVertices();
		for (Developer dev1 : devs1) {
			for (Developer dev2 : devs2) {
				if (dev1.getName().compareTo(dev2.getName()) < 0) {
					ps.setString(1, dev1.getName());
					ps.setString(2, dev2.getName());
					Number distance = sssp.getDistance(dev1, dev2);
					if (distance != null) {
						ps.setDouble(3, distance.doubleValue());
						ps.addBatch();
					}
				}
			}
		}

		log.debug("\tExecuting batch insert ...");
		ps.executeBatch();
	}

	private void runNetworkAnalysis(DeveloperNetwork dn) {
		Graph<Developer, FileSet> graph = dn.getGraph();
		int n = graph.getVertexCount();
		log.info("\tNumber of developers:\t" + n);
		int e = graph.getEdgeCount();
		log.info("\tNumber of edges:\t" + e);
		double density = (n - 1) * (n - 2) / 2;
		density = ((double) e) / density;
		log.info("\tDensity:      \t" + density);
		double diameter = DistanceStatistics.diameter(graph, new UnweightedShortestPath<Developer, FileSet>(
				graph), true);
		log.info("\tDiameter (non-inf):   \t" + diameter);
	}
	
	public DeveloperNetwork getDeveloperNetwork() {
		return dn;
	}
}
