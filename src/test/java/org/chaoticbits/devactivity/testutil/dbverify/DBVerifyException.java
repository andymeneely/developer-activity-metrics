package org.chaoticbits.devactivity.testutil.dbverify;

public class DBVerifyException extends Exception {
	private static final long serialVersionUID = 5398547070761805844L;
	private DBVerifyResult[] results;

	public DBVerifyException(DBVerifyResult... results) {
		this.results = results;
	}

	public DBVerifyResult[] getResults() {
		return results;
	}

	@Override
	public String getMessage() {
		String str = "";
		for (DBVerifyResult result : results) {
			str += result.getMessage() + "\n";
		}
		return str;
	}
}
