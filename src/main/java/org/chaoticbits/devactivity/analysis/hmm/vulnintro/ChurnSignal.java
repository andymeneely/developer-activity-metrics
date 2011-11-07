package org.chaoticbits.devactivity.analysis.hmm.vulnintro;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.chaoticbits.devactivity.analysis.hmm.IHMMAlphabet;

public enum ChurnSignal implements IHMMAlphabet<ChurnSignal> {
	BIG_NEW_CODE("BN"), SMALL_NEW_CODE("SN"), BIG_CHANGE("BC"), SMALL_CHANGE("SC");

	private String parseCode;

	private ChurnSignal(String parseCode) {
		this.parseCode = parseCode;
	}

	public String parseCode() {
		return parseCode;
	}

	private static Set<ChurnSignal> letters = null;

	public static Set<ChurnSignal> letters() {
		if (letters == null)
			letters = new HashSet<ChurnSignal>();
		letters.addAll(Arrays.asList(ChurnSignal.values()));
		return letters;
	}

	public static ChurnSignal parse(String token) {
		for (ChurnSignal churnSignal : values()) {
			if (churnSignal.parseCode.equalsIgnoreCase(token))
				return churnSignal;
		}
		throw new IllegalArgumentException("No such signal for token " + token);
	}
}
