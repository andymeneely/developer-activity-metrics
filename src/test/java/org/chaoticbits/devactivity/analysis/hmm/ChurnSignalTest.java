package org.chaoticbits.devactivity.analysis.hmm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.chaoticbits.devactivity.analysis.hmm.vulnintro.ChurnSignal;
import org.junit.Test;

public class ChurnSignalTest {
	@Test
	public void hasAllLetters() throws Exception {
		ChurnSignal[] values = ChurnSignal.values();
		assertEquals("size is the same", values.length, ChurnSignal.letters().size());
		for (ChurnSignal churnSignal : values) {
			assertTrue("has " + churnSignal, ChurnSignal.letters().contains(churnSignal));
		}
	}
}
