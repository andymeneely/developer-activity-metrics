package edu.ncsu.csc.realsearch.apmeneel.devactivity.contribution;

import edu.uci.ics.jung.graph.Graph;

public class ContributionNetwork {
	private Graph<ContributionNode, ContributionCommit> graph;

	public ContributionNetwork(Graph<ContributionNode, ContributionCommit> graph) {
		this.graph = graph;
	}
}
