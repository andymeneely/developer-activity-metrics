package org.chaoticbits.devactivity.testutil.dbverify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.chaoticbits.devactivity.DBUtil;

public class DBVerifyRunner {
	private final List<IDBVerify> verifies = new ArrayList<IDBVerify>();
	private DBUtil dbUtil;

	public DBVerifyRunner(DBUtil dbUtil) {
		this.dbUtil = dbUtil;
	}

	public void add(IDBVerify... toRun) {
		verifies.addAll(Arrays.asList(toRun));
	}

	public void run() throws Exception {
		List<DBVerifyResult> errors = new ArrayList<DBVerifyResult>();
		for (IDBVerify verify : verifies) {
			DBVerifyResult result = verify.verify(dbUtil);
			if (result.failed()) {
				errors.add(result);
			}
		}
		if (!errors.isEmpty())
			throw new DBVerifyException(errors.toArray(new DBVerifyResult[errors.size()]));
	}

}
