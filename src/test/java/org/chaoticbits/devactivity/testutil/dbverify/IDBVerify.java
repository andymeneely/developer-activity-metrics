package org.chaoticbits.devactivity.testutil.dbverify;

import org.chaoticbits.devactivity.DBUtil;

public interface IDBVerify {
	public DBVerifyResult verify(DBUtil dbUtil) throws Exception;
}
