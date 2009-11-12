package edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.factory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.HashMap;

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
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.DeveloperNetwork;

public class SVNXMLDeveloperFactory implements IDeveloperNetworkFactory {

	private final File input;
	private final DBDevAdjacencyFactory factory;
	private DBUtil dbUtil;

	public SVNXMLDeveloperFactory(File input, DBDevAdjacencyFactory factory) {
		this.input = input;
		this.factory = factory;
		this.dbUtil = factory.getDbUtil();
	}

	@Override
	public DeveloperNetwork build() throws Exception {
		System.out.println("Parsing SVN XML log file...");
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
				// new SimpleDateFormat("")
				
				// TODO parse the date
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
				svnLogFilesInsert.setString(1, revision);
				svnLogFilesInsert.setString(2, filepath);
				svnLogFilesInsert.setString(3, action);
				svnLogFilesInsert.addBatch();
			}
		}
		svnLogInsert.executeBatch();
		svnLogFilesInsert.executeBatch();
		conn.close();
		return factory.build();
	}

	public static Document getXMLDocument(File string) throws ParserConfigurationException, SAXException,
			IOException, FileNotFoundException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new InputSource(new BufferedReader(new FileReader(string))));
		return document;
	}

}
