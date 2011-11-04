package org.chaoticbits.devactivity.analysis.hmm.vulnintro;

import org.chaoticbits.devactivity.analysis.hmm.IHMMState;
import org.chaoticbits.devactivity.analysis.hmm.IHMMTransition;
import org.chaoticbits.devactivity.analysis.hmm.IHiddenMarkovModel;

import edu.uci.ics.jung.graph.DirectedGraph;

public class VulnIntroHMM implements IHiddenMarkovModel<ChurnSignal> {

	private DirectedGraph<IHMMState<ChurnSignal>, IHMMTransition<ChurnSignal>> stateGraph;

	public VulnIntroHMM(SimpleVulnIntroHMMFactory factory) {
		stateGraph = factory.getStateGraph();
	}

	public DirectedGraph<IHMMState<ChurnSignal>, IHMMTransition<ChurnSignal>> getStateGraph() {
		return this.stateGraph;
	}

}
