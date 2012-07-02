package org.chaoticbits.devactivity.churn.git;

/**
 * Storage and basic arithmetic for code churn results. @see CodeChurn
 * 
 * @author andy
 * 
 */
public class ChurnResult {

	private int linesAdded = 0;
	private int linesDeleted = 0;
	private int selfLinesAdded = 0;
	private int selfLinesDeleted = 0;
	private int nonSelfLinesAdded = 0;
	private int nonSelfLinesDeleted = 0;
	private int authorsAffected = 0;

	public int getTotalChurn() {
		return getLinesAdded() + getLinesDeleted();
	}

	public int getTotalSelfChurn() {
		return getSelfLinesAdded() + getSelfLinesDeleted();
	}

	public int getTotalNonSelfChurn() {
		return getNonSelfLinesAdded() + getNonSelfLinesDeleted();
	}

	public int getLinesAdded() {
		return linesAdded;
	}

	public void setLinesAdded(int linesAdded) {
		this.linesAdded = linesAdded;
	}

	public int getLinesDeleted() {
		return linesDeleted;
	}

	public void setLinesDeleted(int linesDeleted) {
		this.linesDeleted = linesDeleted;
	}

	public int getSelfLinesAdded() {
		return selfLinesAdded;
	}

	public void setSelfLinesAdded(int selfLinesAdded) {
		this.selfLinesAdded = selfLinesAdded;
	}

	public int getSelfLinesDeleted() {
		return selfLinesDeleted;
	}

	public void setSelfLinesDeleted(int selfLinesDeleted) {
		this.selfLinesDeleted = selfLinesDeleted;
	}

	public int getNonSelfLinesAdded() {
		return nonSelfLinesAdded;
	}

	public void setNonSelfLinesAdded(int nonSelfLinesAdded) {
		this.nonSelfLinesAdded = nonSelfLinesAdded;
	}

	public int getNonSelfLinesDeleted() {
		return nonSelfLinesDeleted;
	}

	public void setNonSelfLinesDeleted(int nonSelfLinesDeleted) {
		this.nonSelfLinesDeleted = nonSelfLinesDeleted;
	}

	public int getAuthorsAffected() {
		return authorsAffected;
	}

	public void setAuthorsAffected(int authorsAffected) {
		this.authorsAffected = authorsAffected;
	}

	public int getNonSelfChurn() {
		return getNonSelfLinesAdded() + getNonSelfLinesDeleted();
	}

	public int getSelfChurn() {
		return getSelfLinesAdded() + getSelfLinesDeleted();
	}
}
