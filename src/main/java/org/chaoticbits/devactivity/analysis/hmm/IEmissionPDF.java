package org.chaoticbits.devactivity.analysis.hmm;


public interface IEmissionPDF<T extends IHMMAlphabet<T>> {

	public Double getProbability(T signalLetter);

	public void incrementOccurrence(T signal, int by) throws UnsupportedOperationException;

	public void setProbability(T signal, double by) throws UnsupportedOperationException;

}
