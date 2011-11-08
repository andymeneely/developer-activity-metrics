package org.chaoticbits.devactivity.analysis.hmm;

import static org.chaoticbits.devactivity.analysis.hmm.vulnintro.VulnerabilityState.NEUTRAL;
import static org.chaoticbits.devactivity.analysis.hmm.vulnintro.VulnerabilityState.VULNERABLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.chaoticbits.devactivity.analysis.hmm.vulnintro.ChurnSignal;
import org.chaoticbits.devactivity.analysis.hmm.vulnintro.SimpleVulnIntroHMMFactory;
import org.chaoticbits.devactivity.analysis.hmm.vulnintro.VulnIntroHMM;
import org.chaoticbits.devactivity.analysis.hmm.vulnintro.VulnerabilityHMMState;
import org.junit.Test;

import edu.uci.ics.jung.graph.DirectedGraph;

public class SimpleVulnIntroHMMFactoryTest {

	@Test
	public void sizeIsRight() throws Exception {
		IHiddenMarkovModel<ChurnSignal> hmm = new VulnIntroHMM(new SimpleVulnIntroHMMFactory());
		assertEquals("Two states + starting", 3, hmm.getStateGraph().getVertexCount());
		assertEquals("2 for each state, 2 from starting", 6, hmm.getStateGraph().getEdgeCount());
		assertEquals("counting non-starting right", 2, hmm.getEmittingStateCount());
	}

	@Test
	public void topologyIsRight() throws Exception {
		IHiddenMarkovModel<ChurnSignal> hmm = new VulnIntroHMM(new SimpleVulnIntroHMMFactory());
		DirectedGraph<IHMMState<ChurnSignal>, IHMMTransition<ChurnSignal>> graph = hmm.getStateGraph();

		VulnerabilityHMMState n = new VulnerabilityHMMState(NEUTRAL);
		VulnerabilityHMMState v = new VulnerabilityHMMState(VULNERABLE);

		assertEquals("6 edges", 6, graph.getEdgeCount());

		assertTrue(graph.containsVertex(n));
		assertTrue(graph.containsVertex(v));

		assertNotNull("has an edge n-n", graph.findEdge(n, n));
		assertNotNull("has an edge n-v", graph.findEdge(n, v));
		assertNotNull("has an edge v-n", graph.findEdge(v, n));
		assertNotNull("has an edge v-v", graph.findEdge(v, v));
	}

	@Test
	public void basicEmissionProbs() throws Exception {
		Collection<IHMMState<ChurnSignal>> vertices = new VulnIntroHMM(new SimpleVulnIntroHMMFactory()).getStateGraph()
				.getVertices();
		for (IHMMState<ChurnSignal> state : vertices) {
			if (state instanceof VulnerabilityHMMState) {
				for (ChurnSignal signal : ChurnSignal.values()) {
					assertEquals("basic emission probability for " + state + "@" + signal, 0.25,
							state.emissionProbability(signal), 0.001);
				}
			}
		}
	}

	@Test
	public void relaxDefaults() throws Exception {
		IHiddenMarkovModel<ChurnSignal> hmm = new VulnIntroHMM(new SimpleVulnIntroHMMFactory());
		VulnerabilityHMMState n = new VulnerabilityHMMState(VULNERABLE);
		VulnerabilityHMMState v = new VulnerabilityHMMState(NEUTRAL);
		assertEquals("Default transition probability is 0.5", 0.5, prob(hmm, n, v), 0.001);
	}

	@Test
	public void relaxTransitionProbs() throws Exception {
		VulnIntroHMM hmm = new VulnIntroHMM(new SimpleVulnIntroHMMFactory());
		VulnerabilityHMMState n = new VulnerabilityHMMState(NEUTRAL);
		VulnerabilityHMMState v = new VulnerabilityHMMState(VULNERABLE);

		// default is 1/2
		assertEquals("Transition probability", 0.5, prob(hmm, n, v), 0.001);
		assertEquals("Transition probability", 0.5, prob(hmm, v, n), 0.001);
		assertEquals("Transition probability", 0.5, prob(hmm, n, n), 0.001);
		assertEquals("Transition probability", 0.5, prob(hmm, v, v), 0.001);

		hmm.incrementTransition(n, v, 2);
		// now it's 3/4 for the one, and 1/4 for the other 
		assertEquals("Transition probability", 0.75, prob(hmm, n, v), 0.001);
		assertEquals("Transition probability", 0.25, prob(hmm, n, n), 0.001);
		assertEquals("Transition probability", 0.50, prob(hmm, v, v), 0.001);
		assertEquals("Transition probability", 0.50, prob(hmm, v, n), 0.001);
		
	}

	private Double prob(IHiddenMarkovModel<ChurnSignal> hmm, VulnerabilityHMMState n1, VulnerabilityHMMState v1) {
		return hmm.getStateGraph().findEdge(n1, v1).getProb().toDouble();
	}
}
