package org.chaoticbits.devactivity.analysis.hmm;

import edu.uci.ics.jung.graph.DirectedGraph;


public interface IHiddenMarkovModel<T extends IHMMAlphabet<T>> {

	public DirectedGraph<IHMMState<T>, IHMMTransition<T>> getStateGraph();
	
	public IHMMState<T> starting();

	public IHMMState<T> find(IHMMState<T> state);
	
	public int numNonSilentStates();
	
	public abstract void incrementTransition(IHMMState<T> from, IHMMState<T> to);
	
}
