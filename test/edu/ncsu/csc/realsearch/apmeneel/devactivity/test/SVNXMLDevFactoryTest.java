package edu.ncsu.csc.realsearch.apmeneel.devactivity.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;

import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.Developer;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.FileSet;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.factory.SVNXMLDeveloperFactory;
import edu.uci.ics.jung.graph.Graph;

public class SVNXMLDevFactoryTest {

	@Test
	public void exampleTwoNode() throws Exception {
		File input = new File("testdata/exampleTwoNodeSVN.xml");
		Graph<Developer, FileSet> graph = new SVNXMLDeveloperFactory(input).build().getGraph();
		assertEquals("Two developers", graph.getVertexCount());
		assertEquals("andy exists", graph.getVertices().contains("andy"));
		assertEquals("bob exists", graph.getVertices().contains("bob"));
		Developer[] d = graph.getVertices().toArray(new Developer[2]);
		FileSet edge = graph.findEdge(d[0], d[1]);
		assertNotNull("edge exists", edge);
		assertEquals("1 file", edge.getFiles().size());
		assertEquals("Contains file1.txt", "/file1.txt", edge.getFiles().get(0));
	}
}
