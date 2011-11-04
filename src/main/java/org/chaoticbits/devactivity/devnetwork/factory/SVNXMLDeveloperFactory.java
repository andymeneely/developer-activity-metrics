package org.chaoticbits.devactivity.devnetwork.factory;

import java.io.File;

import org.chaoticbits.devactivity.DBUtil;
import org.chaoticbits.devactivity.devnetwork.DeveloperNetwork;

public class SVNXMLDeveloperFactory implements IDeveloperNetworkFactory {

	private final File input;
	private final DBDevAdjacencyFactory factory;
	private DBUtil dbUtil;

	public SVNXMLDeveloperFactory(File input, DBDevAdjacencyFactory factory) {
		this.input = input;
		this.factory = factory;
		this.dbUtil = factory.getDbUtil();
	}

	public DeveloperNetwork build() throws Exception {
		System.out.println("Parsing SVN XML log file...");
		new LoadSVNtoDB(dbUtil, input).run();
		return factory.build();
	}

}
