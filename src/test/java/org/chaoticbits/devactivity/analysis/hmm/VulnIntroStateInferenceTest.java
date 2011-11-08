package org.chaoticbits.devactivity.analysis.hmm;

import static java.lang.Math.*;
import static org.chaoticbits.devactivity.analysis.hmm.vulnintro.ChurnSignal.*;
import static org.chaoticbits.devactivity.analysis.hmm.vulnintro.ChurnSignal.SMALL_NEW_CODE;
import static org.chaoticbits.devactivity.analysis.hmm.vulnintro.VulnerabilityState.NEUTRAL;
import static org.chaoticbits.devactivity.analysis.hmm.vulnintro.VulnerabilityState.VULNERABLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

import org.chaoticbits.devactivity.analysis.hmm.builtin.ForwardBackward;
import org.chaoticbits.devactivity.analysis.hmm.builtin.IncrementOccurrencesHMMTrainer;
import org.chaoticbits.devactivity.analysis.hmm.vulnintro.ChurnSignal;
import org.chaoticbits.devactivity.analysis.hmm.vulnintro.NumDevsState;
import org.chaoticbits.devactivity.analysis.hmm.vulnintro.SimpleVulnIntroHMMFactory;
import org.chaoticbits.devactivity.analysis.hmm.vulnintro.TrainingSetParser;
import org.chaoticbits.devactivity.analysis.hmm.vulnintro.VulnIntroHMM;
import org.junit.Test;

public class VulnIntroStateInferenceTest {

	@Test
	public void firstSymbol() throws Exception {
		// Training set is:
		// n1 v1 n1 v1 v1
		// SN BC BN BN SC
		IHiddenMarkovModel<ChurnSignal> hmm = new IncrementOccurrencesHMMTrainer<ChurnSignal>().train(new VulnIntroHMM(
				new SimpleVulnIntroHMMFactory(2)), new TrainingSetParser().parse(new File(
				"testdata/thrashingHMMTrainingTest.txt")));

		Map<IHMMState<ChurnSignal>, Double> map = new ForwardBackward<ChurnSignal>().logProbInState(hmm,
				Arrays.asList(SMALL_NEW_CODE));

		Double p_n1 = map.get(hmm.find(new NumDevsState(NEUTRAL, 1)));
		Double p_v1 = map.get(hmm.find(new NumDevsState(VULNERABLE, 1)));

		assertEquals("p(v1|SN)", log10(0.333333 * 1d / 7d), p_v1, 0.001);
		assertEquals("p(n1|SN)", log10(0.666666 * 1d / 3d), p_n1, 0.001);
	}

	@Test
	public void happyPath() throws Exception {
		// Train the model from one path, then test it on that same sequence and the highest
		// weighted
		// state should be the most probable state

		// Training set is:
		// n1 v1 n1 v1 v1
		// SN BC BN BN SC
		IHiddenMarkovModel<ChurnSignal> hmm = new IncrementOccurrencesHMMTrainer<ChurnSignal>().train(new VulnIntroHMM(
				new SimpleVulnIntroHMMFactory(2)), new TrainingSetParser().parse(new File(
				"testdata/thrashingHMMTrainingTest.txt")));

		print(hmm);

		Map<IHMMState<ChurnSignal>, Double> map = new ForwardBackward<ChurnSignal>().logProbInState(hmm,
				Arrays.asList(SMALL_NEW_CODE, BIG_CHANGE));

		Double p_n1 = pow(10d, map.get(hmm.find(new NumDevsState(NEUTRAL, 1))));
		Double p_v1 = pow(10d, map.get(hmm.find(new NumDevsState(VULNERABLE, 1))));

		assertTrue("p(v1) > p(n1)", p_v1 > p_n1);
		assertEquals("p(v1|SN BC)", 16d / 441d, p_v1, 0.001);
	}

	private void print(IHiddenMarkovModel<ChurnSignal> hmm) {
		for (IHMMState<ChurnSignal> state : hmm.getStateGraph().getVertices()) {
			System.out.println(state);
			if (!state.isStarting())
				for (ChurnSignal signal : ChurnSignal.values()) {
					System.out.print(signal + ":" + state.emissionProbability(signal) + " ");
				}
			System.out.println("");
			for (IHMMTransition<ChurnSignal> edge : hmm.getStateGraph().getOutEdges(state)) {
				System.out.println("\t--" + edge.getProb() + "-->" + hmm.getStateGraph().getDest(edge));
			}
		}
	}

	@Test
	public void fullHappyPath() throws Exception {
		// Train the model from one path, then test it on that same sequence and the highest
		// weighted
		// state should be the most probable state

		// Training set is:
		// n1 v1 n2 v2 n3
		// BN BC BN SC SN
		IHiddenMarkovModel<ChurnSignal> hmm = new IncrementOccurrencesHMMTrainer<ChurnSignal>().train(new VulnIntroHMM(
				new SimpleVulnIntroHMMFactory(3)), new TrainingSetParser().parse(new File(
				"testdata/thrashingHMMTrainingTest.txt")));

		Map<IHMMState<ChurnSignal>, Double> map = new ForwardBackward<ChurnSignal>().logProbInState(hmm,
				Arrays.asList(BIG_NEW_CODE, BIG_CHANGE, BIG_NEW_CODE, SMALL_CHANGE, SMALL_NEW_CODE));

		Double p_n1 = pow(10d, map.get(hmm.find(new NumDevsState(NEUTRAL, 1)))); // anti-log
		Double p_v1 = pow(10d, map.get(hmm.find(new NumDevsState(VULNERABLE, 1))));
		Double p_n2 = pow(10d, map.get(hmm.find(new NumDevsState(NEUTRAL, 2))));
		Double p_v2 = pow(10d, map.get(hmm.find(new NumDevsState(VULNERABLE, 2))));
		Double p_n3 = pow(10d, map.get(hmm.find(new NumDevsState(NEUTRAL, 3))));

		assertTrue("p(n3) > p(n1)", p_n3 > p_n1);
		assertTrue("p(n3) > p(n2)", p_n3 > p_n2);
		assertTrue("p(n3) > p(v1)", p_n3 > p_v1);
		assertTrue("p(n3) > p(v2)", p_n3 > p_v2);
	}
}
