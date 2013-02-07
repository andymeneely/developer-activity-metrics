package org.chaoticbits.devactivity;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropsLoader {
	public static Properties getProperties(String propsFileName) throws IOException {
		Properties props = new Properties(System.getProperties());
		FileInputStream fis = new FileInputStream(propsFileName);
		props.load(fis);
		fis.close();
		return props;
	}	
}