package org.chaoticbits.devactivity;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.chaoticbits.devactivity.analysis.Modularity;
import org.junit.Test;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

public class ModularityTest {

	private static int edge = 1;
	private static int vertex = 1;

	@Test
	public void two_k3_two_loosely_connected_partitions() throws Exception {
		Graph<String, Integer> k3_1 = makeK(3);
		Graph<String, Integer> k3_2 = makeK(3);
		Graph<String, Integer> graph = new UndirectedSparseGraph<String, Integer>();
		for (Integer i : k3_1.getEdges())
			graph.addEdge(i, k3_1.getEndpoints(i).getFirst(), k3_1.getEndpoints(i).getSecond());
		for (Integer i : k3_2.getEdges())
			graph.addEdge(i, k3_2.getEndpoints(i).getFirst(), k3_2.getEndpoints(i).getSecond());
		for (String v1 : k3_1.getVertices()) {
			for (String v2 : k3_2.getVertices()) {
				graph.addEdge(edge++, v1, v2);
				break; // only do this once
			}
			break; // only do this once
		}

		Set<Set<String>> partitions = new HashSet<Set<String>>();
		Set<String> partition1 = new HashSet<String>();
		Set<String> partition2 = new HashSet<String>();
		partition1.addAll(k3_1.getVertices());
		partition2.addAll(k3_2.getVertices());
		partitions.add(partition1);
		partitions.add(partition2);
		Modularity<String, Integer> mod = new Modularity<String, Integer>(graph);
		assertEquals(0.53, mod.calculate(partitions), 0.001);
	}	
	@Test
	public void two_k3_two_disconnected_partitions() throws Exception {
		Graph<String, Integer> k3_1 = makeK(3);
		Graph<String, Integer> k3_2 = makeK(3);
		Graph<String, Integer> graph = new UndirectedSparseGraph<String, Integer>();
		for (Integer i : k3_1.getEdges())
			graph.addEdge(i, k3_1.getEndpoints(i).getFirst(), k3_1.getEndpoints(i).getSecond());
		for (Integer i : k3_2.getEdges())
			graph.addEdge(i, k3_2.getEndpoints(i).getFirst(), k3_2.getEndpoints(i).getSecond());
		
		Set<Set<String>> partitions = new HashSet<Set<String>>();
		Set<String> partition1 = new HashSet<String>();
		Set<String> partition2 = new HashSet<String>();
		partition1.addAll(k3_1.getVertices());
		partition2.addAll(k3_2.getVertices());
		partitions.add(partition1);
		partitions.add(partition2);
		Modularity<String, Integer> mod = new Modularity<String, Integer>(graph);
		assertEquals(0.666, mod.calculate(partitions), 0.001);
	}	

	private Graph<String, Integer> makeK(int k) {
		UndirectedSparseGraph<String, Integer> graph = new UndirectedSparseGraph<String, Integer>();
		for (int i = 0; i < k; i++)
			graph.addVertex("v" + vertex++);
		for (String v1 : graph.getVertices()) {
			for (String v2 : graph.getVertices()) {
				if (v1 != v2)
					graph.addEdge(edge++, v1, v2);
			}
		}
		return graph;
	}
}
