package edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.factory;

import java.io.File;

import edu.ncsu.csc.realsearch.apmeneel.devactivity.DBUtil;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.DeveloperNetwork;

public class SVNXMLDeveloperFactory implements IDeveloperNetworkFactory {

	private final File input;
	private final DBDevAdjacencyFactory factory;
	private DBUtil dbUtil;

	public SVNXMLDeveloperFactory(File input, DBDevAdjacencyFactory factory) {
		this.input = input;
		this.factory = factory;
		this.dbUtil = factory.getDbUtil();
	}

	@Override
	public DeveloperNetwork build() throws Exception {
		System.out.println("Parsing SVN XML log file...");
		new LoadSVNtoDB(dbUtil, input).run();
		return factory.build();
	}

}
