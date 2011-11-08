package org.chaoticbits.devactivity.analysis.hmm.vulnintro;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import org.chaoticbits.devactivity.analysis.hmm.Fraction;
import org.chaoticbits.devactivity.analysis.hmm.IHMMFactory;
import org.chaoticbits.devactivity.analysis.hmm.IHMMState;
import org.chaoticbits.devactivity.analysis.hmm.IHMMTransition;
import org.chaoticbits.devactivity.analysis.hmm.IHiddenMarkovModel;

import edu.uci.ics.jung.graph.DirectedGraph;

public class VulnIntroHMM implements IHiddenMarkovModel<ChurnSignal> {

	private final DirectedGraph<IHMMState<ChurnSignal>, IHMMTransition<ChurnSignal>> graph;
	private final IHMMState<ChurnSignal> starting;

	public VulnIntroHMM(IHMMFactory<ChurnSignal> factory) {
		graph = factory.getStateGraph();
		starting = factory.getStarting();
	}

	public DirectedGraph<IHMMState<ChurnSignal>, IHMMTransition<ChurnSignal>> getStateGraph() {
		return this.graph;
	}

	public void incrementTransition(IHMMState<ChurnSignal> s1, IHMMState<ChurnSignal> s2) {
		incrementTransition(s1, s2, 1);
	}

	public IHMMState<ChurnSignal> starting() {
		return starting;
	}

	public void incrementTransition(IHMMState<ChurnSignal> s1, IHMMState<ChurnSignal> s2, int by) {
		IHMMTransition<ChurnSignal> incrEdge = graph.findEdge(s1, s2);
		if (incrEdge == null)
			throw new IllegalArgumentException("Transition " + s1 + " to " + s2 + " does not exist");
		Collection<IHMMTransition<ChurnSignal>> edges = graph.getOutEdges(s1);
		for (IHMMTransition<ChurnSignal> edge : edges) {
			int denominator = edge.getProb().getDenom();
			if (incrEdge == edge) {
				edge.setProbability(new Fraction(edge.getProb().getNum() + by, denominator + by));
			} else {
				edge.setProbability(new Fraction(edge.getProb().getNum(), denominator + by));
			}
		}
	}

	public IHMMState<ChurnSignal> find(IHMMState<ChurnSignal> state) {
		Collection<IHMMState<ChurnSignal>> vertices = graph.getVertices();
		for (IHMMState<ChurnSignal> ihmmState : vertices) {
			if (ihmmState.equals(state))
				return ihmmState;
		}
		throw new IllegalArgumentException("State not found: " + state);
	}

	public Double probabilityVulnerable(List<ChurnSignal> churnHistory) {
		throw new IllegalStateException("unimplemented!");
	}

	public int getEmittingStateCount() {
		int count = 0;
		Collection<IHMMState<ChurnSignal>> states = graph.getVertices();
		for (IHMMState<ChurnSignal> state : states) {
			count = count + (state.isStarting() ? 0 : 1);
		}
		return count;
	}

	public Collection<IHMMState<ChurnSignal>> emittingStates() {
		Collection<IHMMState<ChurnSignal>> set = new LinkedHashSet<IHMMState<ChurnSignal>>(graph.getVertexCount());
		for (IHMMState<ChurnSignal> state : graph.getVertices()) {
			if (!state.isStarting())
				set.add(state);
		}
		return set;
	}
}
