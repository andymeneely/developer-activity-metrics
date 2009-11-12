package edu.ncsu.csc.realsearch.apmeneel.devactivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class DBUtil {
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	private String user;

	private String password;

	private String url;

	public DBUtil(String user, String password, String url) {
		this.user = user;
		this.password = password;
		this.url = url;
	}

	public DBUtil(Properties prop) {
		this.user = prop.getProperty("dbuser");
		this.password = prop.getProperty("dbpw");
		this.url = prop.getProperty("dburl");
	}

	public Connection getConnection() throws SQLException {
		return (com.mysql.jdbc.Connection) DriverManager.getConnection(url, user, password);
	}

	public void executeSQL(List<String> queries) throws SQLException {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		for (String createQuery : queries) {
			System.out.println("\tExecuting: " + createQuery);
			ps = conn.prepareStatement(createQuery);
			ps.execute();
			ps.close();
		}
		conn.close();

	}

	public void executeSQLFile(String filepath) throws FileNotFoundException, SQLException, IOException {
		executeSQL(parseSQLFile(filepath));
	}

	private List<String> parseSQLFile(String filepath) throws FileNotFoundException, IOException {
		List<String> queries = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(new File(filepath)));
		String line = "";
		String currentQuery = "";
		while ((line = reader.readLine()) != null) {
			for (int i = 0; i < line.length(); i++) {
				if (line.charAt(i) == ';') {
					queries.add(currentQuery);
					currentQuery = "";
				} else
					currentQuery += line.charAt(i);
			}
		}
		reader.close();
		return queries;
	}

	public void loadLocalFile(Statement stmt, String file, String table) throws Exception {
		stmt.setLocalInfileInputStream(new FileInputStream(file));
		stmt.execute("LOAD DATA LOCAL INFILE '' " + "INTO TABLE " + table + " FIELDS ENCLOSED BY '\"' IGNORE 1 LINES");
	}
}
