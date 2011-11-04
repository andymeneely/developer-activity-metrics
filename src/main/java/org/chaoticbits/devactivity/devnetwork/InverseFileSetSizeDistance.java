package org.chaoticbits.devactivity.devnetwork;

import org.apache.commons.collections15.Transformer;

/**
 * An edge weight transformer that calculates the length of an edge by doing 1.0 / {@link FileSet}
 * .size()
 * 
 * @author apmeneel
 * 
 */
public class InverseFileSetSizeDistance implements Transformer<FileSet, Number> {
	
	public Number transform(FileSet fileSet) {
		if (fileSet.getFiles().isEmpty()) {
			throw new IllegalArgumentException(
					"Edge for file set has zero files, so this edge should not even exist");
		}
		return (double) (1.0 / ((double) fileSet.getFiles().size()));
	}

}
