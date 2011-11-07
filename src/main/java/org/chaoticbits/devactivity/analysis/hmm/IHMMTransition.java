package org.chaoticbits.devactivity.analysis.hmm;

public interface IHMMTransition<T extends IHMMAlphabet<T>> {

	public Fraction getProbability();

	public void setProbability(Fraction prob);

	public String name();
}
