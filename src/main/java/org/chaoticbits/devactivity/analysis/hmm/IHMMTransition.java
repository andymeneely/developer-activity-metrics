package org.chaoticbits.devactivity.analysis.hmm;

public interface IHMMTransition<T extends IHMMAlphabet<T>> {

	public double getProbability();

	public String name();
}
