package org.chaoticbits.devactivity.analysis.hmm;

import static org.chaoticbits.devactivity.analysis.hmm.vulnintro.VulnerabilityState.NEUTRAL;
import static org.chaoticbits.devactivity.analysis.hmm.vulnintro.VulnerabilityState.VULNERABLE;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.chaoticbits.devactivity.analysis.hmm.vulnintro.NumDevsState;
import org.chaoticbits.devactivity.analysis.hmm.vulnintro.NumDevsStateParser;
import org.junit.Test;

public class NumDevsStateParserTest {

	@Test
	public void testBasicString() throws Exception {
		String test = "n1 v1 n2 v2 n3";
		List<NumDevsState> list = new NumDevsStateParser().readLine(test);
		assertEquals("Size of the list", 5, list.size());
		assertEquals("Token", new NumDevsState(NEUTRAL, 1), list.get(0));
		assertEquals("Token", new NumDevsState(VULNERABLE, 1), list.get(1));
		assertEquals("Token", new NumDevsState(NEUTRAL, 2), list.get(2));
		assertEquals("Token", new NumDevsState(VULNERABLE, 2), list.get(3));
		assertEquals("Token", new NumDevsState(NEUTRAL, 3), list.get(4));
	}
}
