package edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork;

import edu.uci.ics.jung.graph.Graph;

public class DeveloperNetwork {
	private Graph<Developer, FileSet> graph;

	public DeveloperNetwork(Graph<Developer, FileSet> graph) {
		this.graph = graph;
	}

	public Graph<Developer, FileSet> getGraph() {
		return graph;
	}

}
