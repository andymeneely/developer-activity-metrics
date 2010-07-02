package edu.ncsu.csc.realsearch.apmeneel.devactivity.parse;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

import com.mysql.jdbc.Connection;

import edu.ncsu.csc.realsearch.apmeneel.devactivity.DBUtil;

public class FilterSVNLog {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FilterSVNLog.class);
	private final DBUtil dbUtil;
	private final Properties props;

	public FilterSVNLog(DBUtil dbUtil, Properties props) {
		this.dbUtil = dbUtil;
		this.props = props;
	}

	public void run() throws Exception {
		filterDate();
		filterExcludeAuthors();
		filterCombineAuthors();
		filterSourceCode();
	}

	private void filterDate() throws Exception {
		log.debug("Filtering date...");
		Connection conn = dbUtil.getConnection();
		String sql = "DELETE FROM SVNLog WHERE AuthorDate < '" + props.getProperty("history.backto")
				+ "' OR AuthorDate > '" + props.getProperty("history.until") + "'";
		log.debug(sql);
		conn.createStatement().execute(sql);
		conn.close();
	}

	private void filterExcludeAuthors() throws SQLException, FileNotFoundException {
		log.debug("Filtering exclude authors...");
		Connection conn = dbUtil.getConnection();
		PreparedStatement ps = conn.prepareStatement("DELETE FROM SVNLog WHERE authorname=?");
		Scanner scanner = new Scanner(new File(props.getProperty("history.datadir"), props
				.getProperty("history.excludeAuthorsFile")));
		while (scanner.hasNextLine()) {
			ps.setString(1, scanner.nextLine());
			ps.addBatch();
		}
		ps.executeBatch();
		scanner.close();
		conn.close();
	}

	private void filterCombineAuthors() throws SQLException, FileNotFoundException {
		log.debug("Combining authors...");
		Connection conn = dbUtil.getConnection();
		PreparedStatement ps = conn.prepareStatement("UPDATE SVNLog SET AuthorName=? WHERE AuthorName=? ");
		Scanner scanner = new Scanner(new File(props.getProperty("history.datadir"), props
				.getProperty("history.combineAuthorsFile")));
		while (scanner.hasNextLine()) {
			String[] line = scanner.nextLine().split("\t");
			if (line.length == 2 && !line[0].startsWith("#")) {
				String setTo = line[0];
				log.debug("Combining to " + setTo);
				ps.setString(1, setTo);
				ps.setString(2, line[1]);
				ps.addBatch();
			}
		}
		ps.executeBatch();
		scanner.close();
		conn.close();
	}

	private void filterSourceCode() throws SQLException, FileNotFoundException {
		if (!"true".equals(props.getProperty("history.sourceCodeOnly")))
			return;
		log.debug("Filtering source code on the SVN log...");
		Connection conn = dbUtil.getConnection();
		PreparedStatement ps = conn.prepareStatement("DELETE FROM SVNLogFiles WHERE " +
				"filepath NOT LIKE '%.c' AND filepath NOT LIKE '%.h'");
		ps.executeUpdate();
		conn.close();
	}
}
