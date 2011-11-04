package org.chaoticbits.devactivity.devnetwork.factory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import org.chaoticbits.devactivity.DBUtil;
import org.chaoticbits.devactivity.devnetwork.Developer;
import org.chaoticbits.devactivity.devnetwork.DeveloperNetwork;
import org.chaoticbits.devactivity.devnetwork.FileSet;

import com.mysql.jdbc.Connection;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

public class DBDevAdjacencyFactory implements IDeveloperNetworkFactory {

	private DBUtil dbUtil;

	public DBDevAdjacencyFactory(DBUtil dbUtil) {
		this.dbUtil = dbUtil;
	}

	public DeveloperNetwork build() throws Exception {
		dbUtil.executeSQLFile("sql/devAdjacency.sql");
		Graph<Developer, FileSet> graph = new UndirectedSparseGraph<Developer, FileSet>();
		Connection conn = dbUtil.getConnection();
		addVertices(graph, conn);
		addEdges(graph, conn);
		conn.close();
		return new DeveloperNetwork(graph);
	}

	private void addVertices(Graph<Developer, FileSet> graph, Connection conn) throws SQLException {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT DISTINCT authorname FROM repolog");
		while (rs.next()) {
			Developer dev = new Developer(rs.getString("authorname").toLowerCase());
			graph.addVertex(dev);
		}
		rs.close();
		stmt.close();
	}

	private void addEdges(Graph<Developer, FileSet> graph, Connection conn) throws SQLException {
		ResultSet rs = conn.createStatement().executeQuery("SELECT dev1,dev2, num, files FROM devadjacency");
		while (rs.next()) {
			Developer dev1 = new Developer(rs.getString("dev1").toLowerCase());
			Developer dev2 = new Developer(rs.getString("dev2").toLowerCase());
			FileSet fileSet = new FileSet();
			String files = rs.getString("files");			
			fileSet.getFiles().addAll(Arrays.asList(files.split("\n")));
			graph.addEdge(fileSet, dev1, dev2);
		}
	}

	public DBUtil getDbUtil() {
		return dbUtil;
	}
}
