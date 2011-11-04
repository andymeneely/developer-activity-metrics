package org.chaoticbits.devactivity.visualize.temporal;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.apache.log4j.xml.DOMConfigurator;
import org.chaoticbits.devactivity.DBUtil;
import org.chaoticbits.devactivity.PropsLoader;

import weka.core.Debug.Random;

import com.mysql.jdbc.Connection;


public class TemporalGraph {
	public static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TemporalGraph.class);
	public static final int WIDTH = 600;
	public static final int HEIGHT = 400;

	public static void main(String[] args) throws Exception {
		Properties props = setUpProps();
		DBUtil dbUtil = setUpDB(props);
		new TemporalGraph().createTemporalAnalysisGraph(dbUtil, props);

	}

	private static Properties setUpProps() throws IOException {
		Properties props = PropsLoader.getProperties("example-tempgraph.properties");
		DOMConfigurator.configure("log4j.properties.xml");
		return props;
	}

	private static DBUtil setUpDB(Properties props) throws ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		DBUtil dbUtil = new DBUtil(props.getProperty("history.dbuser"), props.getProperty("history.dbpw"),
				props.getProperty("history.dburl"));
		return dbUtil;
	}

	private void createTemporalAnalysisGraph(DBUtil dbUtil, Properties props) throws Exception {
		log.info("Creating temporal analysis graph...");
		BufferedImage image = new BufferedImage(600, 400, BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				image.setRGB(x, y, Color.WHITE.getRGB());
			}
		}
		Graphics2D graphics = image.createGraphics();
		drawLines(dbUtil, props, graphics);
		log.info("Writing file...");
		ImageIO.write(image, "PNG", new FileOutputStream("graph.png"));
		log.info("Done.");
	}

	private void drawLines(DBUtil dbUtil, Properties props, Graphics2D graphics) throws Exception {
		graphics.setColor(Color.BLACK);
		graphics.drawLine(0, HEIGHT - 1, 0, 0);
		graphics.drawLine(0, HEIGHT - 1, WIDTH - 1, HEIGHT - 1);

		List<TemporalLine> lines = new ArrayList<TemporalLine>(500);
		Connection conn = dbUtil.getConnection();
		ResultSet rs = conn
				.createStatement()
				.executeQuery(
						"SELECT MID(dateRange, 6,10) as backToDate, MID(dateRange, 20,10) as untilDate, AVG(F1_Score) AvgF1_Score FROM "
								+ "(SELECT *, Left(Description,Instr(Description,'#')-1) as dateRange FROM phphistory.analysis) as DateRanged "
								+ "GROUP BY dateRange");
		long minBackTo = Long.MAX_VALUE;
		long maxUntil = Long.MIN_VALUE;

		while (rs.next()) {
			long backToTime = FORMAT.parse(rs.getString("backToDate")).getTime();
			long untilTime = FORMAT.parse(rs.getString("untilDate")).getTime();
			double score = rs.getDouble("AvgF1_Score");
			lines.add(new TemporalLine(backToTime, untilTime, score));
			if (backToTime < minBackTo)
				minBackTo = backToTime;
			if (untilTime > maxUntil)
				maxUntil = untilTime;
		}
		conn.close();
		long timeSpan = maxUntil - minBackTo;
		double pixelsPerTime = (double) (WIDTH - 2) / (double) timeSpan;
		double MAX_SCORE = 1.0;
		double pixelsPerScore = HEIGHT / MAX_SCORE;
		Random rand = new Random();
		for (TemporalLine line : lines) {
			graphics.setColor(new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
			int backToX = (int) ((line.getBackToTime() - minBackTo) * pixelsPerTime) + 2;
			int untilX = (int) ((line.getUntilTime() - minBackTo) * pixelsPerTime) + 1;
			int y = HEIGHT - (int) (line.getScore() * pixelsPerScore);
			graphics.drawLine(backToX, y - 1, untilX, y - 1);
			graphics.drawLine(backToX, y, untilX, y);
			graphics.drawLine(backToX, y + 1, untilX, y + 1);
			graphics.drawLine(backToX, y - 8, backToX, y + 8);
			graphics.drawLine(untilX, y - 8, untilX, y + 8);
		}
	}
}
