package org.chaoticbits.devactivity.analysis.hmm;

import static org.junit.Assert.*;

import java.util.List;

import org.chaoticbits.devactivity.analysis.hmm.vulnintro.ChurnSignal;
import org.chaoticbits.devactivity.analysis.hmm.vulnintro.ChurnSignalParser;
import org.junit.Test;

public class ChurnSignalParserTest {

	@Test
	public void readsSimple() throws Exception {
		String test = "BN BC BN SC SN";
		List<ChurnSignal> list = new ChurnSignalParser().readLine(test);
		assertEquals("Size of the list", 5, list.size());
		assertEquals("Token", ChurnSignal.BIG_NEW_CODE, list.get(0));
		assertEquals("Token", ChurnSignal.BIG_CHANGE, list.get(1));
		assertEquals("Token", ChurnSignal.BIG_NEW_CODE, list.get(2));
		assertEquals("Token", ChurnSignal.SMALL_CHANGE, list.get(3));
		assertEquals("Token", ChurnSignal.SMALL_NEW_CODE, list.get(4));
	}
}
