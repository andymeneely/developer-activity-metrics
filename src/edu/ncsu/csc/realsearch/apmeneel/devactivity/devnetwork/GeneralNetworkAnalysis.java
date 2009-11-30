package edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import edu.ncsu.csc.realsearch.apmeneel.devactivity.DBUtil;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.factory.DBDevAdjacencyFactory;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.factory.SVNXMLDeveloperFactory;
import edu.uci.ics.jung.algorithms.shortestpath.DistanceStatistics;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import edu.uci.ics.jung.graph.Graph;

public class GeneralNetworkAnalysis {

	public static void main(String[] args) throws Exception {
		Graph<Developer, FileSet> graph = buildNetwork();
		analysis(graph);
	}

	private static Graph<Developer, FileSet> buildNetwork() throws IOException, FileNotFoundException,
			SQLException, Exception {
		Properties props = new Properties();
		props.load(new FileReader("devactivitytests.properties"));
		DBUtil dbUtil = new DBUtil(props);
		// dbUtil.executeSQLFile("sql/createSVNRepoLog.sql");
		// File input = new File("C:/data/openmrs/openmrs-svnlog-full-verbose.xml");
		// Graph<Developer, FileSet> graph = new SVNXMLDeveloperFactory(input, new
		// DBDevAdjacencyFactory(dbUtil))
		// .build().getGraph();

		Graph<Developer, FileSet> graph = new DBDevAdjacencyFactory(dbUtil).build().getGraph();
		return graph;
	}

	private static void analysis(Graph<Developer, FileSet> graph) {
		System.out.println("==== Analysis ====");
		System.out.println("Number of devs:\t" + graph.getVertexCount());
		System.out.println("Number of edges:\t" + graph.getEdgeCount());
		System.out.println("Diameter:\t"
				+ DistanceStatistics.diameter(graph, new UnweightedShortestPath<Developer, FileSet>(graph),
						true));
		
	}
}
