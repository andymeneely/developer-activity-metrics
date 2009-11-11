package edu.ncsu.csc.realsearch.apmeneel.devactivity.contribution;

import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.Developer;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.FileSet;
import edu.uci.ics.jung.graph.Graph;

public class ContributionNetwork {
	private Graph<ContributionNode, ContributionCommit> graph;

	public ContributionNetwork(Graph<ContributionNode, ContributionCommit> graph) {
		this.graph = graph;
	}
}
