package org.chaoticbits.devactivity.devnetwork;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FileSet implements Serializable {

	private static final long serialVersionUID = 8017327006475847353L;
	private List<String> files = new ArrayList<String>();

	// TODO need to have timestamps as well

	public List<String> getFiles() {
		return files;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && obj.getClass().equals(this.getClass()) && this.equals((FileSet) obj);
	}

	private boolean equals(FileSet other) {
		if (files.size() != other.files.size())
			return false;
		boolean isEqual = true;
		for (int i = 0; i < files.size(); i++) {
			isEqual = isEqual && (files.get(i).equals(other.files.get(i)));
		}
		return isEqual;
	}

	@Override
	public String toString() {
		return files.toString();
	}
}
