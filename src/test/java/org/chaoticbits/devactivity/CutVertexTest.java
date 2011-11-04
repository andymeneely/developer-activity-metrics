package org.chaoticbits.devactivity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.chaoticbits.devactivity.analysis.CutVertices;
import org.junit.Test;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

public class CutVertexTest {

	@Test
	public void cycleIsZero() throws Exception {
		Graph<String, Integer> graph = makeC4();
		assertEquals(0, new CutVertices<String, Integer>(graph).getCutVertices().size());
	}

	@Test
	public void cycleWithATail() throws Exception {
		Graph<String, Integer> g = makeC4();
		g.addEdge(5, "e", "a");

		Set<String> cutVertices = new CutVertices<String, Integer>(g).getCutVertices();
		assertEquals(1, cutVertices.size());
		assertTrue(cutVertices.contains("a"));
	}

	@Test
	public void cycleWithTwoTails() throws Exception {
		Graph<String, Integer> g = makeC4();
		g.addEdge(5, "e", "a");
		g.addEdge(6, "f", "b");

		Set<String> cutVertices = new CutVertices<String, Integer>(g).getCutVertices();
		assertEquals(2, cutVertices.size());
		assertTrue(cutVertices.contains("a"));
		assertTrue(cutVertices.contains("b"));
	}

	@Test
	public void cycleWithLongTail() throws Exception {
		Graph<String, Integer> g = makeC4();
		g.addEdge(5, "e", "a");
		g.addEdge(6, "f", "e");

		Set<String> cutVertices = new CutVertices<String, Integer>(g).getCutVertices();
		assertEquals(2, cutVertices.size());
		assertTrue(cutVertices.contains("a"));
		assertTrue(cutVertices.contains("e"));
	}
	
	@Test
	public void cycleWithLongTailSameAfterwards() throws Exception {
		Graph<String, Integer> g = makeC4();
		g.addEdge(5, "e", "a");
		g.addEdge(6, "f", "e");

		Set<String> cutVertices = new CutVertices<String, Integer>(g).getCutVertices();
		assertEquals(2, cutVertices.size());
		//Same graph as before?
		assertEquals(6, g.getVertexCount());
		assertEquals(6, g.getEdgeCount());
		assertTrue(g.getNeighbors("a").contains("b"));
		assertTrue(g.getNeighbors("a").contains("d"));
		assertTrue(g.getNeighbors("a").contains("e"));
	}

	private Graph<String, Integer> makeC4() {
		Graph<String, Integer> c4 = new UndirectedSparseGraph<String, Integer>();
		c4.addEdge(1, "a", "b");
		c4.addEdge(2, "b", "c");
		c4.addEdge(3, "c", "d");
		c4.addEdge(4, "d", "a");
		return c4;
	}
}
