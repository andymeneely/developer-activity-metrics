package edu.ncsu.csc.realsearch.apmeneel.devactivity.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.graph.Graph;

public class CutVertices<V, E> {

	private final Graph<V, E> graph;
	private Set<V> cutVertices;

	public CutVertices(Graph<V, E> graph) {
		this.graph = graph;
	}

	public Set<V> getCutVertices() {
		cutVertices = new HashSet<V>();
		Collection<V> vertices = new ArrayList<V>();
		vertices.addAll(graph.getVertices());
		for (V v : vertices) {
			// Backup the vertex's edges
			List<Neighbor> backupNeighbors = asList(graph, v);
			// Remove the vertex, check for increase in weak components
			int numWeak = new WeakComponentClusterer<V, E>().transform(graph).size();
			graph.removeVertex(v);
			if (new WeakComponentClusterer<V, E>().transform(graph).size() > numWeak)
				// weak components increased!
				cutVertices.add(v);
			// Restore the graph to it's previous state
			restore(graph, v, backupNeighbors);
		}
		return cutVertices;
	}

	/**
	 * Take a given vertex and return a list of Neighbors (edge and vertex pairs)
	 * 
	 * @param g
	 * @param v
	 * @return
	 */
	private List<Neighbor> asList(Graph<V, E> g, V v) {
		List<Neighbor> list = new ArrayList<CutVertices<V, E>.Neighbor>();
		Collection<V> neighbors = g.getNeighbors(v);
		for (V neighVertex : neighbors)
			list.add(new Neighbor(neighVertex, g.findEdge(v, neighVertex)));
		return list;
	}

	/**
	 * Add a vertex and its neighbors back to the graph
	 * 
	 * @param graph2
	 * @param v
	 * @param backupNeighbors
	 */
	private void restore(Graph<V, E> graph2, V v, List<Neighbor> backupNeighbors) {
		for (Neighbor neighbor : backupNeighbors) {
			graph2.addEdge(neighbor.edge, v, neighbor.vertex);
		}
	}

	/**
	 * A basic class for storing vertex/edge pairs
	 * 
	 * @author ameneely
	 * 
	 */
	private class Neighbor {
		public E edge;
		public V vertex;

		public Neighbor(V v, E e) {
			vertex = v;
			edge = e;

		}
	}
}
