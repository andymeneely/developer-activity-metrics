package org.chaoticbits.devactivity.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.chaoticbits.devactivity.devnetwork.Developer;
import org.chaoticbits.devactivity.devnetwork.DeveloperNetwork;
import org.chaoticbits.devactivity.devnetwork.FileSet;

import edu.uci.ics.jung.graph.Tree;

public class TreePartitionModularityAnalysis {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TreePartitionModularityAnalysis.class);
	private final DeveloperNetwork dn;
	private final Properties props;
	private final Tree<Developer, String> mgmtTree;

	public TreePartitionModularityAnalysis(DeveloperNetwork dn, Tree<Developer, String> mgmtTree, Properties props) {
		this.dn = dn;
		this.mgmtTree = mgmtTree;
		this.props = props;
	}

	public void run() {
		int minPartitionDepth = Integer.valueOf(props.getProperty("history.mgmttree.partitiondepth.min"));
		int maxPartitionDepth = Integer.valueOf(props.getProperty("history.mgmttree.partitiondepth.max"));
		for (int partitionDepth = minPartitionDepth; partitionDepth <= maxPartitionDepth; partitionDepth++) {
			log.debug("Partitioning management tree at level " + partitionDepth);
			Set<Set<Developer>> partitions = new PartitionsFromTree<Developer, String>(mgmtTree)
					.getPartitions(partitionDepth);

			log.debug("Removing non-developer network people...");
			int numPeopleRemoved = 0;
			List<Developer> toRemove = new ArrayList<Developer>();
			for (Set<Developer> partition : partitions) {
				for (Developer mgmtDeveloper : partition) {
					if (!dn.getGraph().containsVertex(mgmtDeveloper))
						toRemove.add(mgmtDeveloper);
				}
				partition.removeAll(toRemove);
				numPeopleRemoved += toRemove.size();
				toRemove.clear();
			}
			log.debug("Removed " + numPeopleRemoved + " non-dn people from partitions.");

			log.debug("Running modularity analysis...");

//			int depth = 0;
//			List<Developer> queue = new ArrayList<Developer>();
//			queue.add(mgmtTree.getRoot());
//			while (queue.size() > 0) {
//				Developer developer = queue.remove(0);
//				System.out.print(developer.getName() + " ");
//				int thisDepth = mgmtTree.getDepth(developer);
//				if (thisDepth - depth > 0) {
//					System.out.println("");
//					depth = thisDepth;
//				}
//				queue.addAll(mgmtTree.getChildren(developer));
//			}

			double calculate = new Modularity<Developer, FileSet>(dn.getGraph()).calculate(partitions);
			log.info("\tGraph modularity (partition depth " + partitionDepth + ", " + partitions.size()
					+ " partitions):\t" + calculate);
		}
	}
}
