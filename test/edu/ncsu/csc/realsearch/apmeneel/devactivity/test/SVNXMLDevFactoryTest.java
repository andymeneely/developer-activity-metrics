package edu.ncsu.csc.realsearch.apmeneel.devactivity.test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import edu.ncsu.csc.realsearch.apmeneel.devactivity.DBUtil;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.Developer;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.FileSet;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.factory.DBDevAdjacencyFactory;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.factory.SVNXMLDeveloperFactory;
import edu.uci.ics.jung.graph.Graph;

public class SVNXMLDevFactoryTest {

	private DBUtil dbUtil;

	@Before
	public void init() throws Exception {
		Properties props = new Properties();
		props.load(new FileReader("devactivitytests.properties"));
		dbUtil = new DBUtil(props);
		dbUtil.executeSQLFile("sql/createSVNRepoLog.sql");
	}

	@Test
	public void exampleTwoNode() throws Exception {
		File input = new File("testdata/exampleTwoNodeSVN.xml");
		Graph<Developer, FileSet> graph = new SVNXMLDeveloperFactory(input, new DBDevAdjacencyFactory(dbUtil))
				.build().getGraph();
		assertEquals("Two developers", 2, graph.getVertexCount());
		assertTrue("andy exists", graph.getVertices().contains(new Developer("andy")));
		assertTrue("bob exists", graph.getVertices().contains(new Developer("bob")));
		Developer[] d = graph.getVertices().toArray(new Developer[2]);
		FileSet edge = graph.findEdge(d[0], d[1]);
		assertNotNull("edge exists", edge);
		assertEquals("1 file", 1, edge.getFiles().size());
		assertEquals("Contains file1.txt", "/file1.txt", edge.getFiles().get(0));
	}
}
