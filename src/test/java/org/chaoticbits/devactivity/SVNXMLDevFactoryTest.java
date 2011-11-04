package org.chaoticbits.devactivity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import org.chaoticbits.devactivity.devnetwork.Developer;
import org.chaoticbits.devactivity.devnetwork.FileSet;
import org.chaoticbits.devactivity.devnetwork.factory.DBDevAdjacencyFactory;
import org.chaoticbits.devactivity.devnetwork.factory.SVNXMLDeveloperFactory;
import org.junit.Before;
import org.junit.Test;

import edu.uci.ics.jung.graph.Graph;

public class SVNXMLDevFactoryTest {

	private final Developer andy = new Developer("andy");
	private final Developer bob = new Developer("bob");
	private final Developer cathy = new Developer("cathy");

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
		assertTrue("andy exists", graph.getVertices().contains(andy));
		assertTrue("bob exists", graph.getVertices().contains(bob));

		FileSet edge = graph.findEdge(bob, andy);
		assertNotNull("edge exists", edge);
		assertEquals("1 file", 1, edge.getFiles().size());
		assertEquals("Contains file1.txt", "/file1.txt", edge.getFiles().get(0));
	}

	@Test
	public void exampleThreeNode() throws Exception {
		File input = new File("testdata/exampleThreeNodeSVN.xml");
		Graph<Developer, FileSet> graph = new SVNXMLDeveloperFactory(input, new DBDevAdjacencyFactory(dbUtil))
				.build().getGraph();
		assertEquals("Three developers", 3, graph.getVertexCount());
		assertTrue("andy exists", graph.getVertices().contains(andy));
		assertTrue("bob exists", graph.getVertices().contains(bob));
		assertTrue("cathy exists", graph.getVertices().contains(cathy));

		FileSet edge = graph.findEdge(andy, bob);
		assertNotNull("edge exists", edge);
		assertEquals("1 file", 1, edge.getFiles().size());
		assertEquals("Contains file1.txt", "/file1.txt", edge.getFiles().get(0));

		assertEquals("andy has one neighbor", 1, graph.degree(andy));
		assertEquals("bob has one neighbor", 1, graph.degree(bob));
		assertEquals("cathy has no neighbors", 0, graph.degree(cathy));
	}
}
