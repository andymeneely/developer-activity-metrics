package org.chaoticbits.devactivity.analysis;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Properties;

import org.chaoticbits.devactivity.DBUtil;

import com.mysql.jdbc.Connection;


public class EdgeWindowAnalysis {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(EdgeWindowAnalysis.class);
	private DBUtil dbUtil;
	private String experiment;

	public EdgeWindowAnalysis(DBUtil dbUtil, Properties props) {
		this.dbUtil = dbUtil;
		this.experiment = props.getProperty("history.experiment");
	}

	public void run() throws Exception {
		log.debug("Building developer network tables...");
		dbUtil.executeSQL(Arrays.asList("DROP TABLE IF EXISTS NetworkRepoLog;",
				"DROP VIEW IF EXISTS ZUngroupedDevAdjacency;", "DROP TABLE IF EXISTS DevAdjacency;",
				"CREATE TABLE NetworkRepoLog AS SELECT filepath, authorname, authordate	FROM repolog;",
				"CREATE INDEX NetworkRepoLogAuthor USING BTREE ON NetworkRepoLog(authorname);",
				"CREATE INDEX NetworkRepoLogFile USING BTREE ON NetworkRepoLog(filepath);",
				"OPTIMIZE TABLE NetworkRepoLog;"));
		Connection conn = dbUtil.getConnection();

		for (int windowDays = 1; windowDays < 365; windowDays++) {

			String sql = "SELECT COUNT(DISTINCT c1.authorname ,c2.authorname) as NumEdges "
					+ "FROM NetworkRepoLog c1,NetworkRepoLog c2 WHERE c1.filepath=c2.filepath "
					+ "AND c1.authorname<c2.authorname AND abs(datediff(c1.authordate, c2.authordate)) < ";
			ResultSet rs = conn.createStatement().executeQuery(sql + windowDays);
			rs.next();
			int numEdges = rs.getInt("NumEdges");
			System.out.println(windowDays + "\t" + numEdges);

		}
		conn.close();
		log.debug("Dropping developer network tables...");
		dbUtil.executeSQL(Arrays.asList("DROP TABLE IF EXISTS NetworkRepoLog;",
				"DROP VIEW IF EXISTS ZUngroupedDevAdjacency;"));
	}
}
