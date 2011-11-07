package org.chaoticbits.devactivity.analysis.hmm;

public class Fraction {
	private final Integer num;
	private final Integer denom;

	public Fraction(Integer num, Integer denom) {
		this.num = num;
		this.denom = denom;
	}

	public Integer getNum() {
		return num;
	}

	public Integer getDenom() {
		return denom;
	}

	public Double toDouble() {
		return (double) num / (double) denom;
	}

}
