package org.chaoticbits.devactivity.analysis.hmm.builtin;

import org.chaoticbits.devactivity.analysis.hmm.IHMMAlphabet;
import org.chaoticbits.devactivity.analysis.hmm.IHMMState;

public class SimpleStartState<T extends IHMMAlphabet<T>> implements IHMMState<T> {

	public Double emissionProbability(T signalLetter) {
		throw new IllegalAccessError("Start state does not emit a symbol");
	}

	public String name() {
		return "start";
	}

	public void incrementOccurrence(T signalLetter) {
		throw new IllegalAccessError("Start state does not emit a symbol");
	}

}
