package org.chaoticbits.devactivity.analysis;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Random;

import org.chaoticbits.devactivity.DBUtil;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.core.Instances;
import weka.experiment.InstanceQuery;
import weka.filters.Filter;
import weka.filters.supervised.attribute.Discretize;

import com.mysql.jdbc.Connection;


public class WekaAnalysis {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(WekaAnalysis.class);
	private final DBUtil dbUtil;
	private final Properties props;
	private Random rand;
	private Integer numFolds;
	private Integer repetitions;

	public WekaAnalysis(DBUtil dbUtil, Properties props) {
		this.dbUtil = dbUtil;
		this.props = props;
		this.rand = new Random(Integer.valueOf(props.getProperty("history.cv.randomSeed")));
		this.numFolds = Integer.valueOf(props.getProperty("history.cv.folds"));
		this.repetitions = Integer.valueOf(props.getProperty("history.cv.repetitions"));
	}

	public void run() throws Exception {
		Instances data = loadData();
		data = discretize(data);
		for (int i = 0; i < repetitions; i++) {
			Evaluation eval = analysis(data);
			saveResults(eval, i);
		}
	}

	private Instances loadData() throws Exception {
		log.debug("Loading data...");
		InstanceQuery query = new InstanceQuery();
		query.setDatabaseURL(props.getProperty("history.dburl"));
		query.setUsername(props.getProperty("history.dbuser"));
		query.setPassword(props.getProperty("history.dbpw"));
		Instances instances = query
				.retrieveInstances("SELECT DNMaxEdgeBetweenness, NumDevs, NumCommits, CNBetweenness, HadVulns"
						+ " FROM AllCounts");
		instances.setClassIndex(instances.numAttributes() - 1);
		return instances;
	}

	private Instances discretize(Instances data) throws Exception {
		log.debug("Discretizing data...");
		Discretize discretize = new Discretize();
		discretize.setInputFormat(data);
		return Filter.useFilter(data, discretize);
	}

	private Evaluation analysis(Instances data) throws Exception {
		log.debug("Bayesian network analysis with cross-validation...");
		Evaluation eval = new Evaluation(data);
		eval.crossValidateModel(new BayesNet(), data, numFolds, rand);
		log.info("Confusion Matrix:\n\t\t" + eval.confusionMatrix()[0][0] + "\t"
				+ eval.confusionMatrix()[0][1] + "\n\t\t" + eval.confusionMatrix()[1][0] + "\t"
				+ eval.confusionMatrix()[1][1]);
		return eval;
	}

	private void saveResults(Evaluation eval, int repetitionIndex) throws SQLException {
		String description = "from " + props.getProperty("history.backto") + " to "
				+ props.getProperty("history.until") + " #" + repetitionIndex;
		log.debug("Saving results: " + description + "...");
		Connection conn = dbUtil.getConnection();
		PreparedStatement ps = conn
				.prepareStatement("INSERT INTO Results(Description,TP,TN,FP,FN) VALUES (?,?,?,?,?)");
		ps.setString(1, description);
		ps.setInt(2, (int) eval.confusionMatrix()[1][1]);
		ps.setInt(3, (int) eval.confusionMatrix()[0][0]);
		ps.setInt(4, (int) eval.confusionMatrix()[0][1]);
		ps.setInt(5, (int) eval.confusionMatrix()[1][0]);
		ps.executeUpdate();
		ps.close();
		conn.close();
	}

}
