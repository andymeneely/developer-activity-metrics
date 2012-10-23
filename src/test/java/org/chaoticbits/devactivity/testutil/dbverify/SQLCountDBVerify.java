package org.chaoticbits.devactivity.testutil.dbverify;

import java.sql.ResultSet;

import org.chaoticbits.devactivity.DBUtil;

import com.mysql.jdbc.Connection;

abstract public class SQLCountDBVerify implements IDBVerify {

	private final int expCount;
	private final String sql;
	private final String description;

	public SQLCountDBVerify(String description, int expCount, String sql) {
		this.description = description;
		this.expCount = expCount;
		this.sql = sql;
	}

	public DBVerifyResult verify(DBUtil dbUtil) throws Exception {
		DBVerifyResult result = new DBVerifyResult();
		Connection conn = dbUtil.getConnection();
		ResultSet rs = conn.createStatement().executeQuery(sql);
		rs.next();
		int actualCount = rs.getInt(1);
		if (actualCount == expCount)
			result.verify();
		else {
			result.error("DB failed verification for: " + description, "Expected " + expCount + ", but was "
					+ actualCount, "Using SQL: " + sql);
		}
		conn.close();
		return result;
	}

}
