package edu.ncsu.csc.realsearch.apmeneel.devactivity.parse;

import java.io.FileInputStream;
import java.sql.Connection;
import java.util.Properties;

import com.mysql.jdbc.Statement;

import edu.ncsu.csc.realsearch.apmeneel.devactivity.DBUtil;

public class FileListingParser {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FileListingParser.class);

	public void parse(DBUtil dbUtil, Properties props) throws Exception {
		Connection conn = dbUtil.getConnection();
		Statement stmt = (Statement) conn.createStatement();
		log.info("Loading all files...");
		stmt.setLocalInfileInputStream(new FileInputStream(props.getProperty("history.datadir")
				+ "/allfiles.txt"));
		stmt.execute("LOAD DATA LOCAL INFILE '' "
				+ "INTO TABLE SourceCode LINES TERMINATED BY '\r\n' (filepath)");
		if ("true".equals(props.getProperty("wiresharkhistory.sourceCodeOnly"))) {
			log.debug("\tDeleting non-source code files...");
			int affected = stmt.executeUpdate("DELETE FROM sourcecode WHERE "
					+ "filepath NOT LIKE '%.c' AND filepath NOT LIKE '%.h'");
			if (affected != 901)
				throw new IllegalStateException("DELETED " + affected + " non .c and non .h instead of 2688");
		} else {
			log.debug("\tKeeping all files, source code or not...");
		}
		conn.close();
	}

}
