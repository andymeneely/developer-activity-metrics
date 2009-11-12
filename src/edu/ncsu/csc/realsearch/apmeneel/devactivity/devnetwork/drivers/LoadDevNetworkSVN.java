package edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.drivers;

import java.io.File;

import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.factory.SVNXMLDeveloperFactory;

public class LoadDevNetworkSVN {

	public static void main(String[] args) throws Exception {
		File input = new File("c:/data/openmrs/openmrs-svnlog-full-verbose");
		new SVNXMLDeveloperFactory(input).build();
	}
}
