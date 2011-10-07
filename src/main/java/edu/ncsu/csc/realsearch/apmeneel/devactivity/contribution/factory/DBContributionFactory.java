package edu.ncsu.csc.realsearch.apmeneel.devactivity.contribution.factory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.Connection;

import edu.ncsu.csc.realsearch.apmeneel.devactivity.DBUtil;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.contribution.ContributionCommit;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.contribution.ContributionDeveloper;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.contribution.ContributionFile;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.contribution.ContributionNetwork;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.contribution.ContributionNode;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

public class DBContributionFactory implements IContributionNetworkFactory {

	private final DBUtil dbUtil;

	public DBContributionFactory(DBUtil dbUtil) {
		this.dbUtil = dbUtil;
	}


	public ContributionNetwork build() throws Exception {
		dbUtil.executeSQLFile("sql/contribution.sql");
		Graph<ContributionNode, ContributionCommit> graph = new UndirectedSparseGraph<ContributionNode, ContributionCommit>();
		Connection conn = dbUtil.getConnection();
		List<ContributionDeveloper> devs = addDevelopers(graph, conn);
		List<ContributionFile> files = addFiles(graph, conn);
		addEdges(graph, devs, files, conn);
		conn.close();
		return new ContributionNetwork(graph, devs, files);
	}

	private List<ContributionDeveloper> addDevelopers(Graph<ContributionNode, ContributionCommit> graph,
			Connection conn) throws SQLException {
		List<ContributionDeveloper> devs = new ArrayList<ContributionDeveloper>();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT DISTINCT author FROM contribution");
		while (rs.next()) {
			ContributionDeveloper dev = new ContributionDeveloper(rs.getString("author").toLowerCase());
			devs.add(dev);
			graph.addVertex(dev);
		}
		rs.close();
		stmt.close();
		return devs;
	}

	private List<ContributionFile> addFiles(Graph<ContributionNode, ContributionCommit> graph, Connection conn)
			throws SQLException {
		List<ContributionFile> files = new ArrayList<ContributionFile>();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT DISTINCT filepath FROM contribution");
		while (rs.next()) {
			ContributionFile file = new ContributionFile(rs.getString("filepath").toLowerCase());
			files.add(file);
			graph.addVertex(file);
		}
		rs.close();
		stmt.close();
		return files;
	}

	private void addEdges(Graph<ContributionNode, ContributionCommit> graph,
			List<ContributionDeveloper> devs, List<ContributionFile> files2, Connection conn)
			throws SQLException {
		ResultSet rs = conn.createStatement().executeQuery(
				"SELECT author, filepath, count(*) as num FROM contribution c GROUP BY author,filepath");
		while (rs.next()) {
			ContributionDeveloper dev = new ContributionDeveloper(rs.getString("author"));
			ContributionFile file = new ContributionFile(rs.getString("filepath"));
			ContributionCommit commit = new ContributionCommit(rs.getInt("num"));
			graph.addEdge(commit, dev, file);
		}
	}

	public DBUtil getDbUtil() {
		return dbUtil;
	}

}
