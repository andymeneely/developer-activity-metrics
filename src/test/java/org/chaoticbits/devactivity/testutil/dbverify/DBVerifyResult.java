package org.chaoticbits.devactivity.testutil.dbverify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A fancy-ish return class for the results of a database verification test
 * 
 * @author andy
 * 
 */
public class DBVerifyResult {

	private boolean verified = false; // guilty until proven innocent here!
	private List<String> errors = new ArrayList<String>();

	public void error(String... strings) {
		errors.addAll(Arrays.asList(strings));
		verified = false;
	}

	public void verify() {
		verified = true;
	}

	public boolean isVerified() {
		return verified;
	}

	public boolean failed() {
		return !verified;
	}

	@Override
	public String toString() {
		return getMessage();
	}

	public String getMessage() {
		if (verified)
			return "verified";
		else {
			String str = "";
			for (String error : errors) {
				str += error + "\n";
			}
			return str;
		}
	}

}
