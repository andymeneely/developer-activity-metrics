package org.chaoticbits.devactivity.analysis.hmm;

public interface IHMMTrainingSymbol<T extends IHMMAlphabet<T>> {

	public IHMMState<T> state();

	public T symbol();

}
