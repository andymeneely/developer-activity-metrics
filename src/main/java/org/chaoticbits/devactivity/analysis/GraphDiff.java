package org.chaoticbits.devactivity.analysis;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;

public class GraphDiff<V, E> {

	private final Graph<V, E> first;
	private final Graph<V, E> second;
	private Set<V> droppedVertices;
	private Set<V> newVertices;
	private Set<V> sameVertices;

	public GraphDiff(Graph<V, E> first, Graph<V, E> second) {
		this.first = first;
		this.second = second;
	}

	public Set<V> getDroppedVertices() {
		if (droppedVertices == null)
			calcVertexDiff();
		return droppedVertices;
	}

	private void calcVertexDiff() {
		droppedVertices = new HashSet<V>();
		newVertices = new HashSet<V>();
		sameVertices = new HashSet<V>();
		Collection<V> firstVertices = first.getVertices();
		for (V v : firstVertices) {
			if (second.containsVertex(v)) {
				sameVertices.add(v);
			} else {
				droppedVertices.add(v);
			}
		}
		Collection<V> secondVertices = second.getVertices();
		for (V v : secondVertices) {
			if (!first.containsVertex(v)) // just check for new
				newVertices.add(v);
		}
	}

	public Set<V> getNewVertices() {
		if (newVertices == null)
			calcVertexDiff();
		return newVertices;
	}

	public Set<V> getUnchangedVertices() {
		if (sameVertices == null)
			calcVertexDiff();
		return sameVertices;
	}
}
