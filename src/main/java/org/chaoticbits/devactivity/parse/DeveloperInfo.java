package org.chaoticbits.devactivity.parse;

import java.io.FileInputStream;
import java.sql.Connection;
import java.util.Properties;

import org.chaoticbits.devactivity.DBUtil;

import com.mysql.jdbc.Statement;


public class DeveloperInfo {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(DeveloperInfo.class);
	private final DBUtil dbUtil;
	private final Properties props;

	public DeveloperInfo(DBUtil dbUtil, Properties props) {
		this.dbUtil = dbUtil;
		this.props = props;
	}

	public void run() throws Exception {
		Connection conn = dbUtil.getConnection();
		Statement stmt = (Statement) conn.createStatement();
		log.debug("Loading developer file...");
		stmt.setLocalInfileInputStream(new FileInputStream(props.getProperty("history.datadir")
				+ "/developer-info.txt"));
		stmt.execute("LOAD DATA LOCAL INFILE '' "
				+ "INTO TABLE Developers LINES TERMINATED BY '\r\n' (SVNID, Name, Email)");
		conn.close();
	}

}
