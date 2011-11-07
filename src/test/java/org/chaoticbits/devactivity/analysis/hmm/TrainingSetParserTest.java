package org.chaoticbits.devactivity.analysis.hmm;

import static org.chaoticbits.devactivity.analysis.hmm.vulnintro.VulnerabilityState.NEUTRAL;
import static org.chaoticbits.devactivity.analysis.hmm.vulnintro.VulnerabilityState.VULNERABLE;
import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.chaoticbits.devactivity.analysis.hmm.vulnintro.ChurnSignal;
import org.chaoticbits.devactivity.analysis.hmm.vulnintro.NumDevsState;
import org.chaoticbits.devactivity.analysis.hmm.vulnintro.TrainingSetParser;
import org.junit.Test;

public class TrainingSetParserTest {

	@Test
	public void parseTrainingSet() throws Exception {
		List<IHMMTrainingSymbol<ChurnSignal>> list = new TrainingSetParser()
				.parse(new File("testdata/trainingtest.txt"));

		assertEquals("Token", ChurnSignal.BIG_NEW_CODE, list.get(0).symbol());
		assertEquals("Token", new NumDevsState(NEUTRAL, 1), list.get(0).state());

		assertEquals("Token", ChurnSignal.BIG_CHANGE, list.get(1).symbol());
		assertEquals("Token", new NumDevsState(VULNERABLE, 1), list.get(1).state());

		assertEquals("Token", ChurnSignal.BIG_NEW_CODE, list.get(2).symbol());
		assertEquals("Token", new NumDevsState(NEUTRAL, 2), list.get(2).state());

		assertEquals("Token", ChurnSignal.SMALL_CHANGE, list.get(3).symbol());
		assertEquals("Token", new NumDevsState(VULNERABLE, 2), list.get(3).state());

		assertEquals("Token", ChurnSignal.SMALL_NEW_CODE, list.get(4).symbol());
		assertEquals("Token", new NumDevsState(NEUTRAL, 3), list.get(4).state());
	}
}
