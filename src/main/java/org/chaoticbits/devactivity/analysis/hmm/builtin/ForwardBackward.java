package org.chaoticbits.devactivity.analysis.hmm.builtin;

import static java.lang.Math.log10;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chaoticbits.devactivity.analysis.hmm.IHMMAlphabet;
import org.chaoticbits.devactivity.analysis.hmm.IHMMState;
import org.chaoticbits.devactivity.analysis.hmm.IHMMStateInferencer;
import org.chaoticbits.devactivity.analysis.hmm.IHMMTransition;
import org.chaoticbits.devactivity.analysis.hmm.IHiddenMarkovModel;

import edu.uci.ics.jung.graph.DirectedGraph;

public class ForwardBackward<T extends IHMMAlphabet<T>> implements IHMMStateInferencer<T> {

	// p(state | sequence) - the dynamic programming table
	private Map<StateSeqKey<T>, Double> p_state_seq;

	public Map<IHMMState<T>, Double> logProbInState(IHiddenMarkovModel<T> hmm, List<T> signal) {
		p_state_seq = new HashMap<StateSeqKey<T>, Double>(signal.size());
		DirectedGraph<IHMMState<T>, IHMMTransition<T>> g = hmm.getStateGraph();

		startingProbs(hmm, signal, g);
		dynamicProgrammingStep(hmm, signal, g);

		return lastColumn(signal, hmm);
	}

	private void startingProbs(IHiddenMarkovModel<T> hmm, List<T> signal,
			DirectedGraph<IHMMState<T>, IHMMTransition<T>> g) {
		Collection<IHMMTransition<T>> outEdges = g.getOutEdges(hmm.starting());
		for (IHMMTransition<T> edge : outEdges) {
			IHMMState<T> state = g.getDest(edge);
			Double emitProb = state.emissionProbability(signal.get(0));
			Double edgeProb = edge.getProbability().toDouble();
			p_state_seq.put(new StateSeqKey<T>(state, 0), log10(edgeProb) + log10(emitProb));
		}
	}

	private void dynamicProgrammingStep(IHiddenMarkovModel<T> hmm, List<T> signal,
			DirectedGraph<IHMMState<T>, IHMMTransition<T>> g) {
		for (int i = 1; i < signal.size(); i++) {
			for (IHMMState<T> state : hmm.emittingStates()) {
				double prob = 0.0;
				for (IHMMTransition<T> inEdge : g.getInEdges(state)) {
					IHMMState<T> source = g.getSource(inEdge);

					Double previousProb = p_state_seq.get(new StateSeqKey<T>(source, i - 1));
					if (previousProb == null)
						continue; // then this state is impossible to get to at this time

					// p(state | signal_0..i) = sigma_incomingstatesS(
					// p(S|signal_0..i-1)*inEdge*emissionProb )
					double logP = previousProb + inEdge.getProbability().toDouble()
							+ state.emissionProbability(signal.get(i));
					prob += Math.pow(10d, logP);
				}
				p_state_seq.put(new StateSeqKey<T>(state, i), log10(prob));
			}
		}
	}

	/**
	 * Go down the last column of our "array" to get the final probabilities for the last symbol in
	 * the signal for our answer
	 * 
	 * @param signal
	 * @param hmm
	 * @return
	 */
	private Map<IHMMState<T>, Double> lastColumn(List<T> signal, IHiddenMarkovModel<T> hmm) {
		int lastColumnIndex = signal.size() - 1;
		Map<IHMMState<T>, Double> map = new HashMap<IHMMState<T>, Double>(signal.size());
		for (IHMMState<T> state : hmm.emittingStates()) {
			map.put(state, p_state_seq.get(new StateSeqKey<T>(state, lastColumnIndex)));
		}
		return map;
	}

	/**
	 * Normally you'd use a 2D array here - but we're using hashtables to do our dyamic programming.
	 * Normally one dimension is for each non-starting state, and the other dimension is the signal.
	 * 
	 * As we compute our backward probabilities, we'll just look up the i-1th entries in the
	 * hashtable instead of iterating over the previous column. It's pretty much the same thing
	 * (hopefully...)
	 * 
	 * @author andy
	 * 
	 * @param <T>
	 */
	private class StateSeqKey<T extends IHMMAlphabet<T>> {
		IHMMState<T> state;
		int seqIndex;

		public StateSeqKey(IHMMState<T> state, int seqIndex) {
			this.state = state;
			this.seqIndex = seqIndex;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + seqIndex;
			result = prime * result + ((state == null) ? 0 : state.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			StateSeqKey other = (StateSeqKey) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (seqIndex != other.seqIndex)
				return false;
			if (state == null) {
				if (other.state != null)
					return false;
			} else if (!state.equals(other.state))
				return false;
			return true;
		}

		private ForwardBackward getOuterType() {
			return ForwardBackward.this;
		}

		@Override
		public String toString() {
			return state.name() + ";" + seqIndex;
		}
	}

}
