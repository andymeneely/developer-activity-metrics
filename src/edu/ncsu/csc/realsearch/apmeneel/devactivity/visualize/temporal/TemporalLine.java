package edu.ncsu.csc.realsearch.apmeneel.devactivity.visualize.temporal;

public class TemporalLine {
	private long backToTime;
	private long untilTime;
	private double score;

	public TemporalLine(long backToTime, long untilTime, double score) {
		this.backToTime = backToTime;
		this.untilTime = untilTime;
		this.score = score;
	}

	public long getBackToTime() {
		return backToTime;
	}

	public void setBackToTime(long backToTime) {
		this.backToTime = backToTime;
	}

	public long getUntilTime() {
		return untilTime;
	}

	public void setUntilTime(long untilTime) {
		this.untilTime = untilTime;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}
}
