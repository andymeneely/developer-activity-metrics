package org.chaoticbits.devactivity.contribution;

public class ContributionFile extends ContributionNode {

	public ContributionFile(String name) {
		super(false, name);
	}

	@Override
	public String toString() {
		return "[file]" + getName();
	}
}
