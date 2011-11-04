package org.chaoticbits.devactivity.analysis.hmm;

import static org.chaoticbits.devactivity.analysis.hmm.vulnintro.VulnerabilityState.NEUTRAL;
import static org.chaoticbits.devactivity.analysis.hmm.vulnintro.VulnerabilityState.VULNERABLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.chaoticbits.devactivity.analysis.hmm.vulnintro.ChurnSignal;
import org.chaoticbits.devactivity.analysis.hmm.vulnintro.NumDevsState;
import org.chaoticbits.devactivity.analysis.hmm.vulnintro.SimpleVulnIntroHMMFactory;
import org.chaoticbits.devactivity.analysis.hmm.vulnintro.VulnIntroHMM;
import org.junit.Test;

import edu.uci.ics.jung.graph.DirectedGraph;

public class SimpleVulnIntroHMMFactoryTest {

	@Test
	public void sizeIsRight() throws Exception {
		int maxNumDevs = 13;
		VulnIntroHMM hmm = new VulnIntroHMM(new SimpleVulnIntroHMMFactory(maxNumDevs));
		assertEquals("Two states for each count", maxNumDevs * 2, hmm.getStateGraph().getVertexCount());
		assertEquals("4 for each state, 2 states for each count ", maxNumDevs * 4 * 2 - 4, hmm.getStateGraph()
				.getEdgeCount());
	}

	@Test
	public void topologyIsRight() throws Exception {
		int maxNumDevs = 3;
		VulnIntroHMM hmm = new VulnIntroHMM(new SimpleVulnIntroHMMFactory(maxNumDevs));
		DirectedGraph<IHMMState<ChurnSignal>, IHMMTransition<ChurnSignal>> graph = hmm.getStateGraph();
		
		NumDevsState n1 = new NumDevsState(NEUTRAL, 1);
		NumDevsState v1 = new NumDevsState(VULNERABLE, 1);
		NumDevsState n2 = new NumDevsState(NEUTRAL, 2);
		NumDevsState v2 = new NumDevsState(VULNERABLE, 2);
		NumDevsState n3 = new NumDevsState(NEUTRAL, 3);
		NumDevsState v3 = new NumDevsState(VULNERABLE, 3);
		
		assertEquals("20 edges", 20 , graph.getEdgeCount());
		
		assertTrue(graph.containsVertex(n1));
		assertTrue(graph.containsVertex(v1));
		assertTrue(graph.containsVertex(n2));
		assertTrue(graph.containsVertex(v2));
		assertTrue(graph.containsVertex(n3));
		assertTrue(graph.containsVertex(v3));
		
		assertNotNull("has an edge n1-n1", graph.findEdge(n1, n1));
		assertNotNull("has an edge n1-n2", graph.findEdge(n1, n2));
		assertNotNull("has an edge n1-v2", graph.findEdge(n1, v2));
		assertNotNull("has an edge n1-v1", graph.findEdge(n1, v1));
		
		assertNotNull("has an edge v1-n1", graph.findEdge(v1, n1));
		assertNotNull("has an edge v1-n2", graph.findEdge(v1, n2));
		assertNotNull("has an edge v1-v2", graph.findEdge(v1, v1));
		assertNotNull("has an edge v1-v1", graph.findEdge(v1, v2));
		
		assertNotNull("has an edge n2-n2", graph.findEdge(n2, n2));
		assertNotNull("has an edge n2-n3", graph.findEdge(n2, n3));
		assertNotNull("has an edge n2-v3", graph.findEdge(n2, v3));
		assertNotNull("has an edge n2-v2", graph.findEdge(n2, n2));
		
		assertNotNull("has an edge v2-n2", graph.findEdge(v2, n2));
		assertNotNull("has an edge v2-n3", graph.findEdge(v2, n3));
		assertNotNull("has an edge v2-v3", graph.findEdge(v2, v3));
		assertNotNull("has an edge v2-v2", graph.findEdge(v2, v2));

		assertNotNull("has an edge n3-n3", graph.findEdge(n3, n3));
		assertNotNull("has an edge n3-v3", graph.findEdge(n3, v3));
		
		assertNotNull("has an edge v3-v3", graph.findEdge(v3, v3));
		assertNotNull("has an edge v3-n3", graph.findEdge(v3, n3));
	}

	@Test
	public void basicEmissionProbs() throws Exception {
		Collection<IHMMState<ChurnSignal>> vertices = new VulnIntroHMM(new SimpleVulnIntroHMMFactory(2))
				.getStateGraph().getVertices();
		for (IHMMState<ChurnSignal> state : vertices) {
			for (ChurnSignal signal : ChurnSignal.values()) {
				assertEquals("basic emission probability for " + state + "@" + signal, 0.25,
						state.emissionProbability(signal), 0.001);
			}
		}
	}
}
