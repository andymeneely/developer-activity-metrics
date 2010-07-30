package edu.ncsu.csc.realsearch.apmeneel.devactivity.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import edu.uci.ics.jung.graph.Tree;

public class PartitionsFromTree<V, E> {

	private final Tree<V, E> tree;

	public PartitionsFromTree(Tree<V, E> tree) {
		this.tree = tree;
	}

	public Set<Set<V>> getPartitions(int distanceFromRoot) {
		HashMap<V, Set<V>> partitionsMap = new HashMap<V, Set<V>>();
		for (V v : tree.getVertices()) {
			int depth = tree.getDepth(v);
			if (depth == distanceFromRoot && partitionsMap.get(v) == null) { // add a new head root
																				// of that partition
				addPartition(partitionsMap, v);
			} else if (depth > distanceFromRoot) { // non-manager of the partition
				V parent = v;
				// traverse up the tree
				for (int i = 0; i < depth - distanceFromRoot; i++)
					parent = tree.getParent(v);
				// Add this vertex - initializing that partition if necessary
				Set<V> partition = partitionsMap.get(parent);
				if (partition == null)
					partition = addPartition(partitionsMap, parent);
				partition.add(v);
			} // else if(depth < distanceFromRoot) then do nothing, we don't care
		}
		// Dump the map into HashMaps
		Set<Set<V>> partitions = new HashSet<Set<V>>(partitionsMap.size());
		for (Entry<V, Set<V>> entry : partitionsMap.entrySet()) {
			partitions.add(entry.getValue());
		}
		return partitions;
	}

	private Set<V> addPartition(HashMap<V, Set<V>> partitionsMap, V manager) {
		HashSet<V> partition = new HashSet<V>();
		partition.add(manager);// add the manager to his own partition
		partitionsMap.put(manager, partition);
		return partition;
	}

}
