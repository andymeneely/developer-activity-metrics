package org.chaoticbits.devactivity.analysis.hmm;

import java.util.Collection;

import edu.uci.ics.jung.graph.DirectedGraph;


public interface IHiddenMarkovModel<T extends IHMMAlphabet<T>> {

	public DirectedGraph<IHMMState<T>, IHMMTransition<T>> getStateGraph();
	
	public IHMMState<T> starting();

	public IHMMState<T> find(IHMMState<T> state);
	
	public int getEmittingStateCount();
	
	public Collection<IHMMState<T>> emittingStates();
	
	public abstract void incrementTransition(IHMMState<T> from, IHMMState<T> to);
	
}
