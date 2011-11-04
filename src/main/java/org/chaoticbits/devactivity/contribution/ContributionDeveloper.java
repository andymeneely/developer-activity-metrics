package org.chaoticbits.devactivity.contribution;

public class ContributionDeveloper extends ContributionNode {

	public ContributionDeveloper(String name) {
		super(true, name);
	}

	@Override
	public String toString() {
		return "[dev]" + getName();
	}

}
