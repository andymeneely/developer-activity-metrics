package edu.ncsu.csc.realsearch.apmeneel.devactivity.contribution;

abstract public class ContributionNode {

	private boolean isDeveloper = false;
	protected String name = "";

	public ContributionNode(boolean isDeveloper, String name) {
		this.isDeveloper = isDeveloper;
		this.name = name;
	}

	public boolean isDeveloper() {
		return isDeveloper;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && obj.getClass().equals(this.getClass()) && this.equals((ContributionNode) obj);
	}

	private boolean equals(ContributionNode other) {
		return name.equals(other.name) && isDeveloper == other.isDeveloper;
	}
}
