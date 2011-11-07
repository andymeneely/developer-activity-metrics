package org.chaoticbits.devactivity.analysis.hmm.vulnintro;

import java.util.Collection;

import org.chaoticbits.devactivity.analysis.hmm.Fraction;
import org.chaoticbits.devactivity.analysis.hmm.IHMMFactory;
import org.chaoticbits.devactivity.analysis.hmm.IHMMState;
import org.chaoticbits.devactivity.analysis.hmm.IHMMTransition;
import org.chaoticbits.devactivity.analysis.hmm.IHiddenMarkovModel;

import edu.uci.ics.jung.graph.DirectedGraph;

public class VulnIntroHMM implements IHiddenMarkovModel<ChurnSignal> {

	private DirectedGraph<IHMMState<ChurnSignal>, IHMMTransition<ChurnSignal>> graph;

	public VulnIntroHMM(IHMMFactory<ChurnSignal> factory) {
		graph = factory.getStateGraph();
	}

	public DirectedGraph<IHMMState<ChurnSignal>, IHMMTransition<ChurnSignal>> getStateGraph() {
		return this.graph;
	}

	public void incrementTransition(NumDevsState s1, NumDevsState s2, int by) {
		IHMMTransition<ChurnSignal> incrEdge = graph.findEdge(s1, s2);
		if (incrEdge == null)
			throw new IllegalArgumentException("Transition " + s1 + " to " + s2 + " does not exist");
		Collection<IHMMTransition<ChurnSignal>> edges = graph.getOutEdges(s1);
		for (IHMMTransition<ChurnSignal> edge : edges) {
			int denominator = edge.getProbability().getDenom();
			if (incrEdge == edge) {
				edge.setProbability(new Fraction(edge.getProbability().getNum() + by, denominator + by));
			} else {
				edge.setProbability(new Fraction(edge.getProbability().getNum(), denominator + by));
			}
		}
	}
}
