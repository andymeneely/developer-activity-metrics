package org.chaoticbits.devactivity.analysis.hmm.builtin;

import java.util.HashMap;
import java.util.Map;

import org.chaoticbits.devactivity.analysis.hmm.IEmissionPDF;
import org.chaoticbits.devactivity.analysis.hmm.IHMMAlphabet;
import org.chaoticbits.devactivity.analysis.hmm.vulnintro.InvalidPDFException;

/**
 * The Laplace Hash-based Emission Probability Distribution Function. For a current state, what is the
 * probability of emitting a given signal letter?
 * 
 * The Laplace part of this is that we assume every single signal letter is possible, thus getting a 1/n
 * probability (where n is the number of observed occurrences). As we increment more known occurrences of a
 * letter being emitted from a state, that n increases and that probability decreases.
 * 
 * @author andy
 * 
 * @param <T>
 */
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

	/**
	 * Record an occurrence of a symbol being emitted from the state. We keep track of the total number of
	 * occurrences we've seen, and use that denominator everywhere.
	 */
	public void incrementOccurrence(T signal, int by) {
		if (by <= 0)
			throw new IllegalArgumentException("Positive numbers only for incrementing occurrences");
		total += by;
		Integer count = getWithInit(signal);
		countMap.put(signal, count + by);
	}

	// We only support incrementing occurrences here
	public void setProbability(T signal, double by) throws UnsupportedOperationException {
		throw new UnsupportedOperationException(
				"Method not supported - use incrementOccurrence instead for this implementation.");
	}

	// If there is no recorded occurrence, then we assume it can happen once. And initialize the table, too.
	private Integer getWithInit(T signal) {
		Integer count = countMap.get(signal);
		if (count == null) {
			count = 1; // Laplace!
			countMap.put(signal, count);
		}
		return count;
	}
}
