package org.chaoticbits.devactivity.analysis.hmm;

public interface IHMMState<T extends IHMMAlphabet<T>> {

	public Double emissionProbability(T signalLetter);

	public void incrementOccurrence(T signalLetter);
	
	public String name();
	
	public boolean isStarting();
	
}
