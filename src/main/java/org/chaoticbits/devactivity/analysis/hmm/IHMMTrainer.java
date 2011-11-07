package org.chaoticbits.devactivity.analysis.hmm;

import java.util.List;

public interface IHMMTrainer<T extends IHMMAlphabet<T>> {

	public IHiddenMarkovModel<T> train(IHiddenMarkovModel<T> hmm, List<IHMMTrainingSymbol<T>> trainingSequence);
}
