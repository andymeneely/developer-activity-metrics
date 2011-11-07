package org.chaoticbits.devactivity.analysis.hmm.builtin;

import org.chaoticbits.devactivity.analysis.hmm.IHMMAlphabet;
import org.chaoticbits.devactivity.analysis.hmm.IHMMState;
import org.chaoticbits.devactivity.analysis.hmm.IHMMTrainingSymbol;

public class SimpleHMMTrainingSymbol<T extends IHMMAlphabet<T>> implements IHMMTrainingSymbol<T> {

	private final IHMMState<T> state;
	private final T symbol;

	public SimpleHMMTrainingSymbol(IHMMState<T> state, T symbol) {
		this.state = state;
		this.symbol = symbol;
	}

	public IHMMState<T> state() {
		return state;
	}

	public T symbol() {
		return symbol;
	}

}
