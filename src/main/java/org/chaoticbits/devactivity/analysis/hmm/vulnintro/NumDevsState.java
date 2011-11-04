package org.chaoticbits.devactivity.analysis.hmm.vulnintro;

import org.chaoticbits.devactivity.analysis.hmm.IEmissionPDF;
import org.chaoticbits.devactivity.analysis.hmm.IHMMState;
import org.chaoticbits.devactivity.analysis.hmm.builtin.LaplaceHashEmissionPDF;

public class NumDevsState implements IHMMState<ChurnSignal> {

	private final int numDevs;
	private final VulnerabilityState state;
	private final IEmissionPDF<ChurnSignal> emissionPDF;

	public NumDevsState(VulnerabilityState state, int numDevs) {
		this(state, numDevs, new LaplaceHashEmissionPDF<ChurnSignal>(ChurnSignal.values().length));
	}

	public NumDevsState(VulnerabilityState state, int numDevs, IEmissionPDF<ChurnSignal> pdf) {
		this.state = state;
		this.numDevs = numDevs;
		this.emissionPDF = pdf;
	}

	public Double emissionProbability(ChurnSignal signalLetter) {
		return emissionPDF.getProbability(signalLetter);
	}

	public String name() {
		return state + ", n=" + numDevs;
	}

	public int getNumDevs() {
		return numDevs;
	}

	public VulnerabilityState getState() {
		return state;
	}

	@Override
	public String toString() {
		return name();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + numDevs;
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NumDevsState other = (NumDevsState) obj;
		if (numDevs != other.numDevs)
			return false;
		if (state != other.state)
			return false;
		return true;
	}

}
