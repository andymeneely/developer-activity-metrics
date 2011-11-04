package org.chaoticbits.devactivity.analysis.hmm.builtin;

import java.util.HashMap;
import java.util.Map;

import org.chaoticbits.devactivity.analysis.hmm.IEmissionPDF;
import org.chaoticbits.devactivity.analysis.hmm.IHMMAlphabet;
import org.chaoticbits.devactivity.analysis.hmm.vulnintro.InvalidPDFException;

public class LaplaceHashEmissionPDF<T extends IHMMAlphabet<T>> implements IEmissionPDF<T> {
	private final Map<T, Integer> countMap;
	private double total = 1;

	public LaplaceHashEmissionPDF(int alphabetSize) {
		countMap = new HashMap<T, Integer>(alphabetSize);
		total = alphabetSize;
	}

	public Double getProbability(T signal) {
		Integer count = getWithInit(signal);
		return count / total;
	}

	public void validate() throws InvalidPDFException {
		throw new IllegalStateException("unimplemented!");
	}

	public void incrementOccurrence(T signal, int by) {
		total += by;
		Integer count = getWithInit(signal);
		countMap.put(signal, count + by);
	}

	public void setProbability(T signal, double by) throws UnsupportedOperationException {
		throw new UnsupportedOperationException(
				"Method not supported - use incrementOccurrence instead for this implementation.");
	}

	private Integer getWithInit(T signal) {
		Integer count = countMap.get(signal);
		if (count == null) {
			count = 1; // Laplace!
			countMap.put(signal, count);
		}
		return count;
	}
}
