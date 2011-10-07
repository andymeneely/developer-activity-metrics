package edu.ncsu.csc.realsearch.apmeneel.devactivity.contribution;

public class ContributionFile extends ContributionNode {

	public ContributionFile(String name) {
		super(false, name);
	}

	@Override
	public String toString() {
		return "[file]" + getName();
	}
}
