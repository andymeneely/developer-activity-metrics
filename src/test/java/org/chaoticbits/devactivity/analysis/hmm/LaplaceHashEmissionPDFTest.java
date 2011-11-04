package org.chaoticbits.devactivity.analysis.hmm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.chaoticbits.devactivity.analysis.hmm.builtin.LaplaceHashEmissionPDF;
import org.chaoticbits.devactivity.analysis.hmm.vulnintro.ChurnSignal;
import org.junit.Before;
import org.junit.Test;

public class LaplaceHashEmissionPDFTest {

	private LaplaceHashEmissionPDF<ChurnSignal> pdf;

	@Before
	public void init() {
		pdf = new LaplaceHashEmissionPDF<ChurnSignal>(ChurnSignal.values().length);
	}

	@Test
	public void testLaplaceDefault() throws Exception {
		double expect = 1.0d / ChurnSignal.values().length;
		for (ChurnSignal signal : ChurnSignal.values()) {
			assertEquals("probability for " + signal, expect, pdf.getProbability(signal), 0.001);
		}
	}

	@Test
	public void noSetProb() throws Exception {
		try {
			pdf.setProbability(ChurnSignal.MAJOR_CHANGE, 1);
			fail("exception should have been thrown");
		} catch (UnsupportedOperationException e) {
			assertEquals("Method not supported - use incrementOccurrence instead for this implementation.",
					e.getMessage());
		}
	}

	@Test
	public void incrementBy() throws Exception {
		pdf.incrementOccurrence(ChurnSignal.MAJOR_CHANGE, 2);
		// By default, it should be 1/4 =0.25 for each letter
		// But now we increment it two more times
		// which means it's 1/6 for everything else and 3/6 for MAJOR_CHANGE
		assertEquals("everything else", 0.1666, pdf.getProbability(ChurnSignal.MAJOR_NEW_CODE), 0.01);
		assertEquals("everything else", 0.1666, pdf.getProbability(ChurnSignal.MINOR_CHANGE), 0.01);
		assertEquals("everything else", 0.1666, pdf.getProbability(ChurnSignal.MINOR_NEW_CODE), 0.01);
		assertEquals("everything else", 0.5, pdf.getProbability(ChurnSignal.MAJOR_CHANGE), 0.01);
	}

	@Test
	public void addsUpToOne() throws Exception {
		pdf.incrementOccurrence(ChurnSignal.MAJOR_CHANGE, 2);
		pdf.incrementOccurrence(ChurnSignal.MAJOR_CHANGE, 3);
		pdf.incrementOccurrence(ChurnSignal.MINOR_CHANGE, 2);
		pdf.incrementOccurrence(ChurnSignal.MINOR_NEW_CODE, 26);
		double total = 0;
		for (ChurnSignal signal : ChurnSignal.values()) {
			total += pdf.getProbability(signal);
		}
		assertEquals("everything totals to 1.0", 1.0, total, 0.0001);

	}

}
