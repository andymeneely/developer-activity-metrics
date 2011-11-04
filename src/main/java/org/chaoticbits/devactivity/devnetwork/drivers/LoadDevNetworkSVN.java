package org.chaoticbits.devactivity.devnetwork.drivers;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import org.chaoticbits.devactivity.DBUtil;
import org.chaoticbits.devactivity.devnetwork.factory.DBDevAdjacencyFactory;

public class LoadDevNetworkSVN {

	public static void main(String[] args) throws Exception {
		File input = new File("c:/data/openmrs/openmrs-svnlog-full-verbose");
		Properties props = new Properties(System.getProperties());
		props.load(new FileReader(new File("devactivity.properties")));
		DBUtil dbUtil = new DBUtil(props);
//		new SVNXMLDeveloperFactory(input, new DBDevAdjacencyFactory(dbUtil)).build();
		new DBDevAdjacencyFactory(dbUtil).build();
		System.out.println("Operation completed");
	}
}
