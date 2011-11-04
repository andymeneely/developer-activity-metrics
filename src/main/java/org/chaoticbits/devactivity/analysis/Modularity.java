package org.chaoticbits.devactivity.analysis;

import java.util.Set;

import edu.uci.ics.jung.graph.Graph;

public class Modularity<V, E> {

	private final Graph<V, E> graph;

	public Modularity(Graph<V, E> graph) {
		this.graph = graph;
	}

	/**
	 * Calculates modularity by counting up all of the edges within the given partitions, then
	 * subtracts the expected number of edges in a random graph according to the degrees of the
	 * vertices.<br>
	 * <br>
	 * The entire formula is normalized according to the total number of edges. In this case, 1/2m
	 * where m is the total number of edges in the graph.<br>
	 * <br>
	 * The result <b>should</b> be a number between -1 and 1. Positive means that the number of
	 * edges within the partitions exceeds the expected random amount. <br>
	 * <br>
	 * This also does not count self-loops.
	 * 
	 * @param partitions
	 * @return
	 */
	public double calculate(Set<Set<V>> partitions) {
		double score = 0.0;
		double randomEdgesDenominator = 2.0 * graph.getEdgeCount();
		for (Set<V> partition : partitions) {
			for (V v1 : partition) {
				for (V v2 : partition) {
					if (!graph.containsVertex(v1) || !graph.containsVertex(v2))
						throw new IllegalArgumentException(
								"Partitions contain vertices not found in the graph, either " + v1.toString()
										+ " or " + v2.toString());
					if (v1 != v2) {
						double degree1 = graph.degree(v1);
						double degree2 = graph.degree(v2);
						double hasEdge = graph.findEdge(v1, v2) == null ? 0.0 : 1.0;
						score += hasEdge - (degree1 * degree2) / randomEdgesDenominator;
					}
				}
			}
		}
		return score / randomEdgesDenominator; // yes, do it again at the end
	}
}
