package org.chaoticbits.devactivity.analysis.hmm;

import static org.chaoticbits.devactivity.analysis.hmm.vulnintro.VulnerabilityState.NEUTRAL;
import static org.chaoticbits.devactivity.analysis.hmm.vulnintro.VulnerabilityState.VULNERABLE;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;

import org.chaoticbits.devactivity.analysis.hmm.builtin.IncrementOccurrencesHMMTrainer;
import org.chaoticbits.devactivity.analysis.hmm.vulnintro.ChurnSignal;
import org.chaoticbits.devactivity.analysis.hmm.vulnintro.NumDevsState;
import org.chaoticbits.devactivity.analysis.hmm.vulnintro.NumDevsVulnIntroHMMFactory;
import org.chaoticbits.devactivity.analysis.hmm.vulnintro.TrainingSetParser;
import org.chaoticbits.devactivity.analysis.hmm.vulnintro.VulnIntroHMM;
import org.junit.Before;
import org.junit.Test;

public class IncrementOccurrencesHMMTrainerTest {

	private IHiddenMarkovModel<ChurnSignal> hmm;

	@Before
	public void setUp() throws FileNotFoundException {
		hmm = new VulnIntroHMM(new NumDevsVulnIntroHMMFactory(3));
	}

	@Test
	public void basicIncrementTrainer() throws Exception {
		// Training set is:
		// n1 v1 n2 v2 n3
		// BN BC BN SC SN
		IHiddenMarkovModel<ChurnSignal> trainedHMM = new IncrementOccurrencesHMMTrainer<ChurnSignal>().train(hmm,
				new TrainingSetParser().parse(new File("testdata/trainingtest.txt")));
		NumDevsState n1 = new NumDevsState(NEUTRAL, 1);
		NumDevsState n2 = new NumDevsState(NEUTRAL, 2);
		NumDevsState n3 = new NumDevsState(NEUTRAL, 3);
		NumDevsState v1 = new NumDevsState(VULNERABLE, 1);
		NumDevsState v2 = new NumDevsState(VULNERABLE, 2);
		NumDevsState v3 = new NumDevsState(VULNERABLE, 3);

		// for n1: bump up for n1-->v1, 2/5 vs. 1/5
		assertEquals("Transition probability", 0.4, prob(trainedHMM, n1, v1), 0.001);
		assertEquals("Transition probability", 0.2, prob(trainedHMM, n1, v2), 0.001);
		assertEquals("Transition probability", 0.2, prob(trainedHMM, n1, n1), 0.001);
		assertEquals("Transition probability", 0.2, prob(trainedHMM, n1, n2), 0.001);

		// for n2: bump up for n2-->v2, 2/5 vs. 1/5
		assertEquals("Transition probability", 0.4, prob(trainedHMM, n2, v2), 0.001);
		assertEquals("Transition probability", 0.2, prob(trainedHMM, n2, v3), 0.001);
		assertEquals("Transition probability", 0.2, prob(trainedHMM, n2, n2), 0.001);
		assertEquals("Transition probability", 0.2, prob(trainedHMM, n2, n3), 0.001);
	}

	@Test
	public void emissionProbs() throws Exception {
		// Training set is:
		// n1 v1 n2 v2 n3
		// BN BC BN SC SN
		IHiddenMarkovModel<ChurnSignal> trainedHMM = new IncrementOccurrencesHMMTrainer<ChurnSignal>().train(hmm,
				new TrainingSetParser().parse(new File("testdata/trainingtest.txt")));

		IHMMState<ChurnSignal> n1 = trainedHMM.find(new NumDevsState(NEUTRAL, 1));
		IHMMState<ChurnSignal> v1 = trainedHMM.find(new NumDevsState(VULNERABLE, 1));
		IHMMState<ChurnSignal> v2 = trainedHMM.find(new NumDevsState(VULNERABLE, 2));

		assertEquals("emission probability", 1d / 5d, n1.emissionProbability(ChurnSignal.BIG_CHANGE), 0.001);
		assertEquals("emission probability", 2d / 5d, n1.emissionProbability(ChurnSignal.BIG_NEW_CODE), 0.001);
		assertEquals("emission probability", 1d / 5d, n1.emissionProbability(ChurnSignal.SMALL_CHANGE), 0.001);
		assertEquals("emission probability", 1d / 5d, n1.emissionProbability(ChurnSignal.SMALL_NEW_CODE), 0.001);

		assertEquals("emission probability", 2d / 5d, v1.emissionProbability(ChurnSignal.BIG_CHANGE), 0.001);
		assertEquals("emission probability", 1d / 5d, v1.emissionProbability(ChurnSignal.BIG_NEW_CODE), 0.001);
		assertEquals("emission probability", 1d / 5d, v1.emissionProbability(ChurnSignal.SMALL_CHANGE), 0.001);
		assertEquals("emission probability", 1d / 5d, v1.emissionProbability(ChurnSignal.SMALL_NEW_CODE), 0.001);

		assertEquals("emission probability", 1d / 5d, v2.emissionProbability(ChurnSignal.BIG_CHANGE), 0.001);
		assertEquals("emission probability", 1d / 5d, v2.emissionProbability(ChurnSignal.BIG_NEW_CODE), 0.001);
		assertEquals("emission probability", 2d / 5d, v2.emissionProbability(ChurnSignal.SMALL_CHANGE), 0.001);
		assertEquals("emission probability", 1d / 5d, v2.emissionProbability(ChurnSignal.SMALL_NEW_CODE), 0.001);
	}

	@Test
	public void thrashingIncrements() throws Exception {
		// Training set is:
		// n1 v1 n1 v1 v1
		// SN BC BN BN SC
		IHiddenMarkovModel<ChurnSignal> trainedHMM = new IncrementOccurrencesHMMTrainer<ChurnSignal>().train(hmm,
				new TrainingSetParser().parse(new File("testdata/thrashingHMMTrainingTest.txt")));
		NumDevsState n1 = new NumDevsState(NEUTRAL, 1);
		NumDevsState n2 = new NumDevsState(NEUTRAL, 2);
		NumDevsState v1 = new NumDevsState(VULNERABLE, 1);
		NumDevsState v2 = new NumDevsState(VULNERABLE, 2);

		// for n1: denominator should be 6, bumped up twice on n1-->v1
		assertEquals("Default probability", 3d / 6d, prob(trainedHMM, n1, v1), 0.001);
		assertEquals("Default probability", 1d / 6d, prob(trainedHMM, n1, v2), 0.001);
		assertEquals("Default probability", 1d / 6d, prob(trainedHMM, n1, n1), 0.001);
		assertEquals("Default probability", 1d / 6d, prob(trainedHMM, n1, n2), 0.001);

		// for v1: bump up for v1-->n1 and v1-->v1, denominator 6
		assertEquals("Default probability", 2d / 6d, prob(trainedHMM, v1, n1), 0.001);
		assertEquals("Default probability", 1d / 6d, prob(trainedHMM, v1, n2), 0.001);
		assertEquals("Default probability", 2d / 6d, prob(trainedHMM, v1, v1), 0.001);
		assertEquals("Default probability", 1d / 6d, prob(trainedHMM, v1, v2), 0.001);
	}

	private Double prob(IHiddenMarkovModel<ChurnSignal> hmm, NumDevsState s1, NumDevsState s2) {
		return hmm.getStateGraph().findEdge(s1, s2).getProb().toDouble();
	}

}
