package edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.drivers;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import edu.ncsu.csc.realsearch.apmeneel.devactivity.DBUtil;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.factory.DBDevAdjacencyFactory;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.factory.SVNXMLDeveloperFactory;

public class LoadDevNetworkSVN {

	public static void main(String[] args) throws Exception {
		File input = new File("c:/data/openmrs/openmrs-svnlog-full-verbose");
		Properties props = new Properties(System.getProperties());
		props.load(new FileReader(new File("devactivity.properties")));
		DBUtil dbUtil = new DBUtil(props);
		new SVNXMLDeveloperFactory(input, new DBDevAdjacencyFactory(dbUtil)).build();
		System.out.println("Operation completed");
	}
}
