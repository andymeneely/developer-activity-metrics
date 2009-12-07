package edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.factory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.mysql.jdbc.Connection;

import edu.ncsu.csc.realsearch.apmeneel.devactivity.DBUtil;

public class LoadSVNtoDB{
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LoadSVNtoDB.class);
	
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	private File input;
	private DBUtil dbUtil;

	public LoadSVNtoDB(DBUtil dbUtil, File input) {
		this.dbUtil = dbUtil;
		this.input = input;
	}

	public void run() throws Exception {
		Document document = getXMLDocument(input);
		DocumentTraversal traversal = (DocumentTraversal) document;

		NodeIterator iterator = traversal.createNodeIterator(document.getDocumentElement(),
				NodeFilter.SHOW_ELEMENT, null, true);
		String author = "Oops - not loaded!";
		String revision = "Oops - not loaded!";
		Date date = null;
		String message = "Oops - not loaded!";
		Connection conn = dbUtil.getConnection();
		PreparedStatement svnLogInsert = conn
				.prepareStatement("INSERT INTO SVNLog(Revision, AuthorName, AuthorDate, Message) "
						+ "VALUES (?,?,?,?)");
		PreparedStatement svnLogFilesInsert = conn
				.prepareStatement("INSERT INTO SVNLogFiles(Revision, Filepath, Action) " + "VALUES (?,?,?)");
		for (Node n = iterator.nextNode(); n != null; n = iterator.nextNode()) {
			if ("logentry".equals(n.getNodeName())) {
				revision = n.getAttributes().getNamedItem("revision").getNodeValue();
			} else if ("author".equals(n.getNodeName())) {
				author = n.getTextContent();
			} else if ("date".equals(n.getNodeName())) {
				date = parseDate(filter(n.getTextContent()));
			} else if ("msg".equals(n.getNodeName())) {
				message = n.getTextContent();
				svnLogInsert.setString(1, revision);
				svnLogInsert.setString(2, author);
				svnLogInsert.setDate(3, date);
				svnLogInsert.setString(4, message);
				svnLogInsert.addBatch();
			} else if ("path".equals(n.getNodeName())) {
				String action = n.getAttributes().getNamedItem("action").getNodeValue();
				String filepath = n.getTextContent();
				filepath = filter(filepath);
				svnLogFilesInsert.setString(1, revision);
				svnLogFilesInsert.setString(2, filepath);
				svnLogFilesInsert.setString(3, action);
				svnLogFilesInsert.addBatch();
			}
		}
		svnLogInsert.executeBatch();
		svnLogFilesInsert.executeBatch();
		conn.close();
	}

	public static Document getXMLDocument(File string) throws ParserConfigurationException, SAXException,
			IOException, FileNotFoundException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new InputSource(new BufferedReader(new FileReader(string))));
		return document;
	}

	public static Date parseDate(String dateStr) {
		try {
			java.util.Date parsedDate = DATE_FORMAT.parse(dateStr);
			return new Date(parsedDate.getTime());
		} catch (ParseException e) {
			return null;
		}
	}

	private String filter(String filepath) {
		return filepath.trim();
	}
}
