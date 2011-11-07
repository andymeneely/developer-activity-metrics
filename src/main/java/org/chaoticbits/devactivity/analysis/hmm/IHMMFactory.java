package org.chaoticbits.devactivity.analysis.hmm;

import org.chaoticbits.devactivity.analysis.hmm.vulnintro.ChurnSignal;

import edu.uci.ics.jung.graph.DirectedGraph;

public interface IHMMFactory<T extends IHMMAlphabet<T>> {

	public DirectedGraph<IHMMState<ChurnSignal>, IHMMTransition<ChurnSignal>> getStateGraph();

	public IHMMState<T> getStarting();

}
