package org.chaoticbits.devactivity.analysis.hmm.builtin;

import org.chaoticbits.devactivity.analysis.hmm.IHMMAlphabet;
import org.chaoticbits.devactivity.analysis.hmm.IHMMState;

/**
 * A basic start state that does not emit a symbol, only exists once, and only has out edges. Running
 * emissionProbability and incrementOccurrence will result in exceptions.
 * 
 * @author andy
 * 
 * @param <T>
 */
public class SimpleStartState<T extends IHMMAlphabet<T>> implements IHMMState<T> {

	/**
	 * Makes no sense for this class. Throws an IllegalAccessError
	 */
	public Double emissionProbability(T signalLetter) {
		throw new IllegalAccessError("Start state does not emit a symbol");
	}

	/**
	 * Returns "start".
	 */
	public String name() {
		return "start";
	}

	/**
	 * Returns true always.
	 */
	public boolean isStarting() {
		return true;
	}

	/**
	 * Makes no sense for this class. Throws an IllegalAccessError
	 */
	public void incrementOccurrence(T signalLetter) {
		throw new IllegalAccessError("Start state does not emit a symbol");
	}

	@Override
	public String toString() {
		return name();
	}

}
