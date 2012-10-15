package org.chaoticbits.devactivity.analysis.hmm.builtin;

import static java.lang.Math.log10;
import static java.lang.Math.pow;

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
import edu.uci.ics.jung.graph.Graph;

/**
 * An implementation of the Forward-Backward algorithm for Hidden Markov Models. Given a trained HMM, we
 * compute the probability of being in a given state given a particular signal.
 * 
 * For starting probabilities, we assume that each initial state is equally likely.
 * 
 * @author andy
 * 
 * @param <T>
 */
public class ForwardBackward<T extends IHMMAlphabet<T>> implements
		IHMMStateInferencer<T> {

	// p(state | sequence) - the dynamic programming table
	private Map<StateSeqKey<T>, Double> p_state_seq;

	/**
	 * Given a model and a signal, compute the probability that the model ends up in a given state. The
	 * answer is given in the {@link Math.log10} of the probability (to prevent poor double precision).
	 * 
	 * @return map - the map of a state to a probability, given this signal
	 * 
	 */
	public Map<IHMMState<T>, Double> logProbInState(IHiddenMarkovModel<T> hmm,
			List<T> signal) {
		p_state_seq = new HashMap<StateSeqKey<T>, Double>(signal.size());

		startingProbs(hmm, signal);
		dynamicProgrammingStep(hmm, signal);

		return lastColumn(signal, hmm);
	}

	/**
	 * Computes the starting probabilities for the first letter in the signal (index=0). Base case for the
	 * dynamic programming step.
	 * @param hmm
	 * @param signal
	 * @param g
	 */
	private void startingProbs(IHiddenMarkovModel<T> hmm, List<T> signal) {
		DirectedGraph<IHMMState<T>, IHMMTransition<T>> graph = hmm
				.getStateGraph();
		Collection<IHMMTransition<T>> startingOutEdges = graph.getOutEdges(hmm
				.starting());
		for (IHMMTransition<T> edge : startingOutEdges) {
			IHMMState<T> state = graph.getDest(edge);
			Double emitProb = state.emissionProbability(signal.get(0));
			Double edgeProb = edge.getProb().toDouble();
			p_state_seq.put(new StateSeqKey<T>(state, 0), log10(edgeProb)
					+ log10(emitProb));
		}
	}

	/**
	 * Go through the rest of the signal, (index=1..end), and for each state that emits a signal look at the
	 * incoming edges. For each incoming edge that has a non-zero probability (i.e. it's reachable from this
	 * signal), compute the probability of reaching this state. The formula is:
	 * 
	 * p(state | signal_0..i) = sigma_incomingStatesS( p(S|signal_0..i-1)*inEdge*emissionProb )
	 * 
	 * That is, the probability of getting to this state is the probability of getting to any of the previous
	 * states times the transition probability, times the emission probability.
	 * 
	 * @param hmm
	 * @param signal
	 */
	private void dynamicProgrammingStep(IHiddenMarkovModel<T> hmm,
			List<T> signal) {
		Graph<IHMMState<T>, IHMMTransition<T>> graph = hmm.getStateGraph();
		for (int i = 1; i < signal.size(); i++) {
			for (IHMMState<T> state : hmm.emittingStates()) {
				double prob = 0.0;
				for (IHMMTransition<T> inEdge : graph.getInEdges(state)) {
					IHMMState<T> source = graph.getSource(inEdge);

					Double previousProb = p_state_seq.get(new StateSeqKey<T>(
							source, i - 1));
					if (previousProb == null)
						continue; // then this state is impossible to get to at this time

					// p(state | signal_0..i) = sigma_incomingstatesS(
					// p(S|signal_0..i-1)*inEdge*emissionProb )
					Double inEdgeProb = inEdge.getProb().toDouble();
					Double emitProb = state.emissionProbability(signal.get(i));
					prob += pow(10d, previousProb) * inEdgeProb * emitProb;
				}
				p_state_seq.put(new StateSeqKey<T>(state, i), log10(prob));
			}
		}
	}

	/**
	 * Go down the last column of our "array" to get the final probabilities for the last symbol in the
	 * signal for our answer
	 * 
	 * @param signal
	 * @param hmm
	 * @return
	 */
	private Map<IHMMState<T>, Double> lastColumn(List<T> signal,
			IHiddenMarkovModel<T> hmm) {
		int lastColumnIndex = signal.size() - 1;
		Map<IHMMState<T>, Double> map = new HashMap<IHMMState<T>, Double>(
				signal.size());
		for (IHMMState<T> state : hmm.emittingStates()) {
			map.put(state,
					p_state_seq.get(new StateSeqKey<T>(state, lastColumnIndex)));
		}
		return map;
	}

	/**
	 * Normally you'd use a 2D array here - but we're using hashtables to do our dyamic programming. Normally
	 * one dimension is for each non-starting state, and the other dimension is the signal. Hence, we need a
	 * custom key for a state-sequence_index pairing.
	 * 
	 * As we compute our backward probabilities, we'll just look up the i-1th entries in the hashtable
	 * instead of iterating over the previous column. It's pretty much the same thing (hopefully...)
	 * 
	 * @author andy
	 * 
	 * @param <S>
	 */
	private class StateSeqKey<S extends IHMMAlphabet<S>> {
		IHMMState<S> state;
		int seqIndex;

		public StateSeqKey(IHMMState<S> state, int seqIndex) {
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

		private ForwardBackward<T> getOuterType() {
			return ForwardBackward.this;
		}

		@Override
		public String toString() {
			return state.name() + ";" + seqIndex;
		}
	}

}
