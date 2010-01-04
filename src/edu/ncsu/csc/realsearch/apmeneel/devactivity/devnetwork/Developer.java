package edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork;

import java.io.Serializable;

public class Developer implements Serializable {

	private static final long serialVersionUID = -1805942134852848557L;
	private String name;

	public String getName() {
		return name;
	}

	public Developer(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && obj.getClass().equals(this.getClass()) && this.equals((Developer) obj);
	}

	private boolean equals(Developer other) {
		return name.equals(other.name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return name;
	}
}
