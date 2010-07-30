package edu.ncsu.csc.realsearch.apmeneel.devactivity.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import edu.ncsu.csc.realsearch.apmeneel.devactivity.analysis.PartitionsFromTree;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Tree;

public class PartitionsFromTreeTest {

	@org.junit.Test
	public void twoPartitionsOfTwoEach() throws Exception {
		Tree<String, String> tree = new DelegateTree<String, String>();
		tree.addVertex("root");
		tree.addEdge("e1", "root", "v1");
		tree.addEdge("e2", "root", "v2");

		tree.addEdge("e3", "v1", "v3");
		tree.addEdge("e4", "v1", "v4");
		tree.addEdge("e5", "v1", "v5");

		tree.addEdge("e6", "v2", "v6");
		tree.addEdge("e7", "v2", "v7");
		tree.addEdge("e8", "v2", "v8");

		Set<Set<String>> partitions = new PartitionsFromTree<String, String>(tree).getPartitions(1);
		assertEquals(2, partitions.size());
		for (Set<String> partition : partitions) {
			boolean option1 = is(partition, "v1", "v3", "v4", "v5");
			boolean option2 = is(partition, "v2", "v6", "v7", "v8");
			assertTrue("Partition" + partition.toString(), option1 || option2);
		}
	}

	@org.junit.Test
	public void twoPartitionsOfTwoEachDepth2() throws Exception {
		Tree<String, String> tree = new DelegateTree<String, String>();
		tree.addVertex("root");
		tree.addEdge("e0", "root", "root1");

		tree.addEdge("e1", "root1", "v1");
		tree.addEdge("e2", "root1", "v2");

		tree.addEdge("e3", "v1", "v3");
		tree.addEdge("e4", "v1", "v4");
		tree.addEdge("e5", "v1", "v5");

		tree.addEdge("e6", "v2", "v6");
		tree.addEdge("e7", "v2", "v7");
		tree.addEdge("e8", "v2", "v8");

		Set<Set<String>> partitions = new PartitionsFromTree<String, String>(tree).getPartitions(2);
		assertEquals(2, partitions.size());
		for (Set<String> partition : partitions) {
			boolean option1 = is(partition, "v1", "v3", "v4", "v5");
			boolean option2 = is(partition, "v2", "v6", "v7", "v8");
			assertTrue("Partition" + partition.toString(), option1 || option2);
		}
	}

	@org.junit.Test
	public void threePartitionsOfTwoEachDepth3() throws Exception {
		Tree<String, String> tree = new DelegateTree<String, String>();
		tree.addVertex("root");
		tree.addEdge("e0", "root", "root1");
		tree.addEdge("e02", "root1", "root2");

		tree.addEdge("e1", "root2", "v1");
		tree.addEdge("e2", "root2", "v2");
		tree.addEdge("e3", "root2", "v3");

		tree.addEdge("e4", "v1", "v4");
		tree.addEdge("e5", "v1", "v5");
		tree.addEdge("e6", "v1", "v6");

		tree.addEdge("e7", "v2", "v7");
		tree.addEdge("e8", "v2", "v8");
		tree.addEdge("e9", "v2", "v9");

		tree.addEdge("e10", "v3", "v10");
		tree.addEdge("e11", "v3", "v11");
		tree.addEdge("e12", "v3", "v12");

		Set<Set<String>> partitions = new PartitionsFromTree<String, String>(tree).getPartitions(3);
		assertEquals(3, partitions.size());
		for (Set<String> partition : partitions) {
			boolean option1 = is(partition, "v1", "v4", "v5", "v6");
			boolean option2 = is(partition, "v2", "v7", "v8", "v9");
			boolean option3 = is(partition, "v3", "v10", "v11", "v12");
			assertTrue("Partition" + partition.toString(), option1 || option2 || option3);
		}
	}

	private boolean is(Set<String> partition, String... vs) {
		boolean is = partition.size() == vs.length;
		for (String v : vs) {
			is = is && partition.contains(v);
		}
		return is;
	}
}
