package org.chaoticbits.devactivity.analysis.hmm.vulnintro;

import static org.chaoticbits.devactivity.analysis.hmm.vulnintro.VulnerabilityState.NEUTRAL;
import static org.chaoticbits.devactivity.analysis.hmm.vulnintro.VulnerabilityState.VULNERABLE;

import org.chaoticbits.devactivity.analysis.hmm.IHMMAlphabet;
import org.chaoticbits.devactivity.analysis.hmm.IHMMFactory;
import org.chaoticbits.devactivity.analysis.hmm.IHMMState;
import org.chaoticbits.devactivity.analysis.hmm.IHMMTransition;
import org.chaoticbits.devactivity.analysis.hmm.builtin.SimpleTransition;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class SimpleVulnIntroHMMFactory implements IHMMFactory<ChurnSignal> {

	private final int maxNumDevs;
	private DirectedGraph<IHMMState<ChurnSignal>, IHMMTransition<ChurnSignal>> graph;

	public SimpleVulnIntroHMMFactory(int maxNumDevs) {
		this.maxNumDevs = maxNumDevs;
	}

	public DirectedGraph<IHMMState<ChurnSignal>, IHMMTransition<ChurnSignal>> getStateGraph() {
		init();
		return graph;
	}

	private void init() {
		if (graph != null)
			return;
		graph = new DirectedSparseGraph<IHMMState<ChurnSignal>, IHMMTransition<ChurnSignal>>();

		NumDevsState n_iminus1 = new NumDevsState(NEUTRAL, 1);
		NumDevsState v_iminus1 = new NumDevsState(VULNERABLE, 1);
		graph.addEdge(e("n1-v1"), n_iminus1, v_iminus1);
		graph.addEdge(e("v1-n1"), v_iminus1, n_iminus1);
		for (int i = 2; i <= maxNumDevs; i++) {
			NumDevsState n_i = new NumDevsState(NEUTRAL, i);
			NumDevsState v_i = new NumDevsState(VULNERABLE, i);
			graph.addEdge(e("n" + (i - 1) + "-v" + i), n_iminus1, v_i); // new dev inject
			graph.addEdge(e("v" + (i - 1) + "-n" + i), v_iminus1, n_i); // new dev fix
			graph.addEdge(e("n" + (i - 1) + "-n" + i), n_iminus1, n_i); // new dev sameN
			graph.addEdge(e("v" + (i - 1) + "-v" + i), v_iminus1, v_i); // new dev sameV
			graph.addEdge(e("n" + (i) + "-v" + (i)), n_i, v_i); // old dev inj
			graph.addEdge(e("v" + (i) + "-n" + (i)), v_i, n_i); // old dev fix
			n_iminus1 = n_i;
			v_iminus1 = v_i;
		}

		for (IHMMState<ChurnSignal> state : graph.getVertices()) {
			graph.addEdge(e("self loop for " + state), state, state); // self loop
		}
	}

	private SimpleTransition<ChurnSignal> e(String name) {
		return new SimpleTransition<ChurnSignal>(name, 0.25);
	}
}
