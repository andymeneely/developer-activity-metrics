package org.chaoticbits.devactivity.devnetwork;

import org.apache.commons.collections15.Transformer;

/**
 * An edge weight transformer that calculates the distance of an edge by subtracting from a maximum.
 * The maximum parameter represents the number of total source code files in the system. Thus, the
 * distance between two developers is represented by the number of files they DID NOT work on. <br>
 * <br>
 * For example, suppose there are 5 files in the system, and the shortest path from A to C is A-B-C.
 * A and B worked on 2 files together, and B and C worked on 4 files together. Thus, the weights are
 * A--3--B--1--c, and the weight of the whole path is 4 files. Now that DOESN'T mean that 4 is the
 * number of files not worked on by A, B, and C (we could be double-counting), but the relative
 * score of 4 files still matches our notion of weighting a given path. This scheme preserves unit
 * validity, and scale validity, but is not easily interpreted in isolation.
 * 
 * @author apmeneel
 * 
 */
public class ComplementFileSetSizeDistance implements Transformer<FileSet, Number> {

	private int numFilesInSystem;

	public ComplementFileSetSizeDistance(int numFilesInSystem) {
		this.numFilesInSystem = numFilesInSystem;
	}

	public Number transform(FileSet fileSet) {
		if (fileSet.getFiles().isEmpty()) {
			throw new IllegalArgumentException(
					"Edge for file set has zero files, so this edge should not even exist");
		}
		
		return numFilesInSystem - fileSet.getFiles().size();
	}

}
