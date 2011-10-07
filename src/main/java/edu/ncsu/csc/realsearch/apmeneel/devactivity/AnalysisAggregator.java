package edu.ncsu.csc.realsearch.apmeneel.devactivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AnalysisAggregator {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AnalysisAggregator.class);

	public static void logResult(String experimentDescription, String metricDescription, double result, Connection conn)
			throws SQLException {
		log.info(experimentDescription + "\t" + metricDescription + ":\t" + result);
		PreparedStatement ps = conn
				.prepareStatement("INSERT INTO AnalysisResults(ExperimentDescription, MetricName, MetricValue) VALUES(?,?,?)");
		ps.setString(1, experimentDescription);
		ps.setString(2, metricDescription);
		if (Double.isNaN(result))
			ps.setDouble(3, 0.0);
		else
			ps.setDouble(3, result);
		ps.executeUpdate();
		ps.close();
	}
}
