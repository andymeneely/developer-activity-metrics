package org.chaoticbits.devactivity.analysis.hmm.vulnintro;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.chaoticbits.devactivity.analysis.hmm.IHMMAlphabet;

public enum ChurnSignal implements IHMMAlphabet<ChurnSignal> {
	MAJOR_NEW_CODE, MINOR_NEW_CODE, MAJOR_CHANGE, MINOR_CHANGE;

	private static Set<ChurnSignal> letters = null;

	public static Set<ChurnSignal> letters() {
		if (letters == null)
			letters = new HashSet<ChurnSignal>();
		letters.addAll(Arrays.asList(ChurnSignal.values()));
		return letters;
	}

}
