package org.chaoticbits.devactivity.analysis.hmm.builtin;

import org.chaoticbits.devactivity.analysis.hmm.IHMMAlphabet;
import org.chaoticbits.devactivity.analysis.hmm.IHMMTransition;

public class SimpleTransition<T extends IHMMAlphabet<T>> implements IHMMTransition<T> {

	private final double probability;
	private final String name;

	public SimpleTransition(double probability) {
		this(null, probability);
	}

	public SimpleTransition(String name, double probability) {
		this.name = name;
		this.probability = probability;
	}

	public double getProbability() {
		return probability;
	}

	public String name() {
		return name == null ? "edge" : name;
	}

	@Override
	public String toString() {
		return name();
	}

	@Override
	public int hashCode() {
		return name().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleTransition<T> other = (SimpleTransition<T>) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (Double.doubleToLongBits(probability) != Double.doubleToLongBits(other.probability))
			return false;
		return true;
	}

}
