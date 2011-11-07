package org.chaoticbits.devactivity.analysis.hmm;

import java.util.List;
import java.util.Map;

public interface IHMMStateInferencer<T extends IHMMAlphabet<T>> {

	public Map<IHMMState<T>, Double> logProbInState(IHiddenMarkovModel<T> hmm, List<T> signal);
	
}
