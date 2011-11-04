package org.chaoticbits.devactivity.contribution;

import java.util.List;

import edu.uci.ics.jung.graph.Graph;

public class ContributionNetwork {
	private final Graph<ContributionNode, ContributionCommit> graph;
	private final List<ContributionDeveloper> devs;
	private final List<ContributionFile> files;

	public ContributionNetwork(Graph<ContributionNode, ContributionCommit> graph,
			List<ContributionDeveloper> devs, List<ContributionFile> files) {
		this.graph = graph;
		this.devs = devs;
		this.files = files;
	}

	public Graph<ContributionNode, ContributionCommit> getGraph() {
		return graph;
	}

	public List<ContributionDeveloper> getDevs() {
		return devs;
	}

	public List<ContributionFile> getFiles() {
		return files;
	}

}
