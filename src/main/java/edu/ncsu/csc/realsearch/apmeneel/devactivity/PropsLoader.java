package edu.ncsu.csc.realsearch.apmeneel.devactivity;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropsLoader {
	public static Properties getProperties(String propsFileName) throws IOException {
		Properties props = new Properties(System.getProperties());
		props.load(new FileInputStream(propsFileName));
		return props;
	}

	
}