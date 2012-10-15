package org.chaoticbits.devactivity.analysis.hmm.builtin;

import java.util.List;

import org.chaoticbits.devactivity.analysis.hmm.IHMMAlphabet;
import org.chaoticbits.devactivity.analysis.hmm.IHMMState;
import org.chaoticbits.devactivity.analysis.hmm.IHMMTrainer;
import org.chaoticbits.devactivity.analysis.hmm.IHMMTrainingSymbol;
import org.chaoticbits.devactivity.analysis.hmm.IHiddenMarkovModel;

/**
 * Train a HMM by incrementing the number of occurrences a given transition and emission appeared
 * 
 * Requires a list of symbols linked to states (via {@link IHMMTrainingSymbol}).
 * 
 * This is the trivial type of training - where we know the state for each symbol. Ordinarily, we don't have
 * that information
 * @author andy
 * 
 * @param <T>
 */
public class IncrementOccurrencesHMMTrainer<T extends IHMMAlphabet<T>>
		implements IHMMTrainer<T> {

	/**
	 * 
	 * @param hmm
	 *            - the Hidden Markov Model to train
	 * @param trainingSequence
	 *            - the list of symbol-states with which to train.
	 * 
	 */
	public IHiddenMarkovModel<T> train(IHiddenMarkovModel<T> hmm,
			List<IHMMTrainingSymbol<T>> trainingSequence) {
		IHMMState<T> current = hmm.starting();
		for (IHMMTrainingSymbol<T> next : trainingSequence) {
			hmm.incrementTransition(current, next.state());
			hmm.find(next.state()).incrementOccurrence(next.symbol());
			current = next.state();
		}
		return hmm;
	}

}
