package org.chaoticbits.devactivity.analysis;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.chaoticbits.devactivity.DBUtil;
import org.chaoticbits.devactivity.contribution.ContributionCommit;
import org.chaoticbits.devactivity.contribution.ContributionFile;
import org.chaoticbits.devactivity.contribution.ContributionNetwork;
import org.chaoticbits.devactivity.contribution.ContributionNode;
import org.chaoticbits.devactivity.contribution.factory.DBContributionFactory;

import com.mysql.jdbc.Connection;

import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.graph.Graph;

public class ContributionNetworkAnalysis {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger
			.getLogger(ContributionNetworkAnalysis.class);
	private final DBUtil dbUtil;

	public ContributionNetworkAnalysis(DBUtil dbUtil) {
		this.dbUtil = dbUtil;
	}

	public void run() throws Exception {
		log.debug("Building contribution network...");
		ContributionNetwork cn = new DBContributionFactory(dbUtil).build();
		log.debug("Running centrality calculations...");
		loadBetweenness(cn);
		log.debug("Contribution network analysis stats...");
		runNetworkAnalysis(cn);
	}

	private void loadBetweenness(ContributionNetwork cn) throws SQLException {
		BetweennessCentrality<ContributionNode, ContributionCommit> bc = new BetweennessCentrality<ContributionNode, ContributionCommit>(
				cn.getGraph(), true, false);
		bc.setRemoveRankScoresOnFinalize(false);
		log.debug("\tComputing betweenness...");
		bc.evaluate();
		log.debug("\tLoading file betweenness scores...");
		List<ContributionFile> files = cn.getFiles();
		Connection conn = dbUtil.getConnection();
		PreparedStatement ps = conn
				.prepareStatement("UPDATE sourcecode SET CNBetweenness=? WHERE filepath=?");
		for (ContributionFile file : files) {
			ps.setDouble(1, bc.getVertexRankScore(file));
			ps.setString(2, file.getName());
			ps.addBatch();
		}
		log.debug("\tExecuting batch insert ...");
		ps.executeBatch();
		DBUtil.closeConnection(conn, ps);
	}

	private void runNetworkAnalysis(ContributionNetwork cn) {
		Graph<ContributionNode, ContributionCommit> graph = cn.getGraph();
		log.info("\tNumber of developers:\t" + cn.getDevs().size());
		log.info("\tNumber of files:\t" + cn.getFiles().size());
		int n = graph.getVertexCount();
		log.info("\tNumber of nodes:\t" + n);
		int e = graph.getEdgeCount();
		log.info("\tNumber of edges:\t" + e);
		double density = (n - 1) * (n - 2) / 2;
		density = ((double) e) / density;
		log.info("\tDensity:      \t" + density);
	}
}
