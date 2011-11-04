package org.chaoticbits.devactivity.analysis.hmm;

import edu.uci.ics.jung.graph.DirectedGraph;


public interface IHiddenMarkovModel<T extends IHMMAlphabet<T>> {

	public DirectedGraph<IHMMState<T>, IHMMTransition<T>> getStateGraph();
	
}
