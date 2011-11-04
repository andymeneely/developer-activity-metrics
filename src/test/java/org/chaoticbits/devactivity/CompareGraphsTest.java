package org.chaoticbits.devactivity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.chaoticbits.devactivity.analysis.GraphDiff;
import org.junit.Test;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

public class CompareGraphsTest {

	@Test
	public void sameGraph() throws Exception {
		Graph<String, Integer> firstK3 = makeK3();
		Graph<String, Integer> secondK3 = makeK3();

		GraphDiff<String, Integer> graphDiff = new GraphDiff<String, Integer>(firstK3, secondK3);
		assertEquals(0, graphDiff.getDroppedVertices().size());
		assertEquals(0, graphDiff.getNewVertices().size());

		assertEquals(3, graphDiff.getUnchangedVertices().size());
		assertTrue(graphDiff.getUnchangedVertices().contains("a"));
		assertTrue(graphDiff.getUnchangedVertices().contains("b"));
		assertTrue(graphDiff.getUnchangedVertices().contains("c"));
	}

	@Test
	public void oneDroppedVertex() throws Exception {
		Graph<String, Integer> firstK3 = makeK3();
		firstK3.addVertex("d");
		Graph<String, Integer> secondK3 = makeK3();

		GraphDiff<String, Integer> graphDiff = new GraphDiff<String, Integer>(firstK3, secondK3);
		assertEquals(1, graphDiff.getDroppedVertices().size());
		assertEquals(0, graphDiff.getNewVertices().size());
		assertTrue(graphDiff.getDroppedVertices().contains("d"));

		assertEquals(3, graphDiff.getUnchangedVertices().size());
		assertTrue(graphDiff.getUnchangedVertices().contains("a"));
		assertTrue(graphDiff.getUnchangedVertices().contains("b"));
		assertTrue(graphDiff.getUnchangedVertices().contains("c"));
	}

	@Test
	public void oneNewVertex() throws Exception {
		Graph<String, Integer> firstK3 = makeK3();
		Graph<String, Integer> secondK3 = makeK3();
		secondK3.addVertex("d");

		GraphDiff<String, Integer> graphDiff = new GraphDiff<String, Integer>(firstK3, secondK3);
		assertEquals(1, graphDiff.getNewVertices().size());
		assertEquals(0, graphDiff.getDroppedVertices().size());
		assertTrue(graphDiff.getNewVertices().contains("d"));

		assertEquals(3, graphDiff.getUnchangedVertices().size());
		assertTrue(graphDiff.getUnchangedVertices().contains("a"));
		assertTrue(graphDiff.getUnchangedVertices().contains("b"));
		assertTrue(graphDiff.getUnchangedVertices().contains("c"));
	}

	private Graph<String, Integer> makeK3() {
		Graph<String, Integer> k3 = new UndirectedSparseGraph<String, Integer>();
		k3.addEdge(1, "a", "b");
		k3.addEdge(2, "a", "c");
		k3.addEdge(3, "b", "c");
		return k3;
	}
}
