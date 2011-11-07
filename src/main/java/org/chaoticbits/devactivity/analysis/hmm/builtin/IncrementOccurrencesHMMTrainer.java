package org.chaoticbits.devactivity.analysis.hmm.builtin;

import java.util.List;

import org.chaoticbits.devactivity.analysis.hmm.IHMMAlphabet;
import org.chaoticbits.devactivity.analysis.hmm.IHMMState;
import org.chaoticbits.devactivity.analysis.hmm.IHMMTrainer;
import org.chaoticbits.devactivity.analysis.hmm.IHMMTrainingSymbol;
import org.chaoticbits.devactivity.analysis.hmm.IHiddenMarkovModel;

public class IncrementOccurrencesHMMTrainer<T extends IHMMAlphabet<T>> implements IHMMTrainer<T> {

	public IHiddenMarkovModel<T> train(IHiddenMarkovModel<T> hmm, List<IHMMTrainingSymbol<T>> trainingSequence) {
		IHMMState<T> current = hmm.starting();
		for (IHMMTrainingSymbol<T> next : trainingSequence) {
			hmm.incrementTransition(current, next.state());
			hmm.find(next.state()).incrementOccurrence(next.symbol());
			current = next.state();
		}
		return hmm;
	}

}
