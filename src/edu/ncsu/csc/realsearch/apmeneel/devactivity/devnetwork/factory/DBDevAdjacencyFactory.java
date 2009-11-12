package edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.factory;

import edu.ncsu.csc.realsearch.apmeneel.devactivity.DBUtil;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.DeveloperNetwork;

public class DBDevAdjacencyFactory implements IDeveloperNetworkFactory {

	private DBUtil dbUtil;

	public DBDevAdjacencyFactory(DBUtil dbUtil) {
		this.dbUtil = dbUtil;

	}

	@Override
	public DeveloperNetwork build() {
		throw new IllegalStateException("unimplemented!");
	}

	public DBUtil getDbUtil() {
		return dbUtil;
	}
}
