package edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.visualize;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.apache.commons.collections15.functors.ConstantTransformer;

import edu.ncsu.csc.realsearch.apmeneel.devactivity.DBUtil;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.Developer;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.DeveloperNetwork;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.FileSet;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.factory.DBDevAdjacencyFactory;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.factory.SVNXMLDeveloperFactory;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalLensGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.transform.shape.HyperbolicShapeTransformer;
import edu.uci.ics.jung.visualization.transform.shape.ViewLensSupport;

public class DeveloperNetworkVisualize extends JApplet {

	private Graph<Developer, FileSet> graph;
	private VisualizationViewer<Developer, FileSet> vv;
	private KKLayout<Developer, FileSet> layout;
	private ViewLensSupport<Developer, FileSet> hyperbolicViewSupport;

	public DeveloperNetworkVisualize(DeveloperNetwork dn) {
		graph = dn.getGraph();

		layout = new KKLayout<Developer, FileSet>(graph);

		vv = new VisualizationViewer<Developer, FileSet>(layout, new Dimension(600, 600));
		vv.setBackground(Color.white);
		vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		// add a listener for ToolTips
		vv.setVertexToolTipTransformer(new ToStringLabeller());
		vv.getRenderContext().setArrowFillPaintTransformer(new ConstantTransformer(Color.lightGray));

		Container content = getContentPane();
		final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
		content.add(panel);

		final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();

		vv.setGraphMouse(graphMouse);
		vv.addKeyListener(graphMouse.getModeKeyListener());

		hyperbolicViewSupport = new ViewLensSupport<Developer, FileSet>(vv, new HyperbolicShapeTransformer(
				vv, vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW)),
				new ModalLensGraphMouse());

		graphMouse.addItemListener(hyperbolicViewSupport.getGraphMouse().getModeListener());

		JComboBox modeBox = graphMouse.getModeComboBox();
		modeBox.addItemListener(graphMouse.getModeListener());
		graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);

		final ScalingControl scaler = new CrossoverScalingControl();

		vv.scaleToLayout(scaler);

		JButton plus = new JButton("+");
		plus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(vv, 1.1f, vv.getCenter());
			}
		});
		JButton minus = new JButton("-");
		minus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(vv, 1 / 1.1f, vv.getCenter());
			}
		});

		final JCheckBox hyperView = new JCheckBox("Hyperbolic View");
		hyperView.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				hyperbolicViewSupport.activate(e.getStateChange() == ItemEvent.SELECTED);
			}
		});

		JPanel scaleGrid = new JPanel(new GridLayout(1, 0));
		scaleGrid.setBorder(BorderFactory.createTitledBorder("Zoom"));

		JPanel controls = new JPanel();
		scaleGrid.add(plus);
		scaleGrid.add(minus);
		controls.add(scaleGrid);
		controls.add(modeBox);
		controls.add(hyperView);
		content.add(controls, BorderLayout.SOUTH);
	}

	/**
	 * a driver for this demo
	 */
	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		props.load(new FileReader("devactivitytests.properties"));
		DBUtil dbUtil = new DBUtil(props);
		dbUtil.executeSQLFile("sql/createSVNRepoLog.sql");
		File input = new File("C:/data/openmrs/openmrs-svnlog-full-verbose.xml");

		JFrame frame = new JFrame();
		Container content = frame.getContentPane();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		content.add(new DeveloperNetworkVisualize(new DeveloperNetwork(new SVNXMLDeveloperFactory(input,
				new DBDevAdjacencyFactory(dbUtil)).build().getGraph())));
		frame.pack();
		frame.setVisible(true);
	}
}
