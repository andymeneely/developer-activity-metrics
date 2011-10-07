package edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.visualize;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

import org.apache.log4j.PropertyConfigurator;

import edu.ncsu.csc.realsearch.apmeneel.devactivity.DBUtil;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.Developer;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.DeveloperNetwork;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.FileSet;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.factory.DBDevAdjacencyFactory;
import edu.ncsu.csc.realsearch.apmeneel.devactivity.devnetwork.factory.SVNXMLDeveloperFactory;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
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
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.transform.shape.HyperbolicShapeTransformer;
import edu.uci.ics.jung.visualization.transform.shape.ViewLensSupport;
import edu.uci.ics.jung.visualization.util.Animator;

public class DeveloperNetworkExplorer extends JApplet {

	private static final long serialVersionUID = 4772441438424199758L;

	private Graph<Developer, FileSet> graph;
	private VisualizationViewer<Developer, FileSet> vv;
	private Layout<Developer, FileSet> layout;
	private ViewLensSupport<Developer, FileSet> hyperbolicViewSupport;

	@SuppressWarnings("unchecked")
	private static Class<? extends Layout>[] getCombos() {
		List<Class<? extends Layout>> layouts = new ArrayList<Class<? extends Layout>>();
		layouts.add(KKLayout.class);
		layouts.add(FRLayout.class);
		layouts.add(CircleLayout.class);
		layouts.add(SpringLayout.class);
		layouts.add(SpringLayout2.class);
		layouts.add(ISOMLayout.class);
		return layouts.toArray(new Class[0]);
	}

	public DeveloperNetworkExplorer(DeveloperNetwork dn) {
		graph = dn.getGraph();

		// layout = new KKLayout<Developer, FileSet>(graph);
		layout = new SpringLayout<Developer, FileSet>(graph);

		vv = new VisualizationViewer<Developer, FileSet>(layout, new Dimension(600, 600));
		vv.setBackground(Color.white);
		vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<Developer, FileSet>());
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Developer>());
		// add a listener for ToolTips
		vv.setVertexToolTipTransformer(new ToStringLabeller<Developer>());
		vv.setEdgeToolTipTransformer(new ToStringLabeller<FileSet>());

		Container content = getContentPane();
		final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
		content.add(panel);

		final DefaultModalGraphMouse<Developer, FileSet> graphMouse = new DefaultModalGraphMouse<Developer, FileSet>();

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

		Class[] combos = getCombos();
		final JComboBox layoutBox = new JComboBox(combos);
		// use a renderer to shorten the layout name presentation
		layoutBox.setRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(JList list, Object value, int index,
					boolean isSelected, boolean cellHasFocus) {
				String valueString = value.toString();
				valueString = valueString.substring(valueString.lastIndexOf('.') + 1);
				return super.getListCellRendererComponent(list, valueString, index, isSelected, cellHasFocus);
			}
		});
		layoutBox.addActionListener(new LayoutChooser(layoutBox, vv, graph));
		layoutBox.setSelectedItem(FRLayout.class);

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
		controls.add(layoutBox);
		controls.add(hyperView);
		content.add(controls, BorderLayout.SOUTH);
	}

	private static final class LayoutChooser implements ActionListener {
		private final JComboBox jcb;
		private final VisualizationViewer<Developer, FileSet> vv;
		private final Graph<Developer, FileSet> graph2;

		private LayoutChooser(JComboBox jcb, VisualizationViewer<Developer, FileSet> vv,
				Graph<Developer, FileSet> graph) {
			super();
			this.jcb = jcb;
			this.vv = vv;
			graph2 = graph;
		}

		public void actionPerformed(ActionEvent arg0) {
			Object[] constructorArgs = { graph2 };

			Class<? extends Layout<Developer, FileSet>> layoutC = (Class<? extends Layout<Developer, FileSet>>) jcb
					.getSelectedItem();
			try {
				Constructor<? extends Layout<Developer, FileSet>> constructor = layoutC
						.getConstructor(new Class[] { Graph.class });
				Object o = constructor.newInstance(constructorArgs);
				Layout<Developer, FileSet> l = (Layout<Developer, FileSet>) o;
				l.setInitializer(vv.getGraphLayout());
				l.setSize(vv.getSize());

				LayoutTransition<Developer, FileSet> lt = new LayoutTransition<Developer, FileSet>(vv, vv
						.getGraphLayout(), l);
				Animator animator = new Animator(lt);
				animator.start();
				vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
				vv.repaint();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * a driver for this demo
	 */
	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		props.load(new FileReader("devactivity.properties"));
		PropertyConfigurator.configure(props);
		DBUtil dbUtil = new DBUtil(props);
		// dbUtil.executeSQLFile("sql/createSVNRepoLog.sql");
		// File input = new File("testdata/exampleTwoNodeSVN.xml");
		// File input = new File("C:/data/openmrs/openmrs-svnlog-full-verbose.xml");

		JFrame frame = new JFrame();
		Container content = frame.getContentPane();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// DeveloperNetwork dn = new DeveloperNetwork(new SVNXMLDeveloperFactory(input,
		// new DBDevAdjacencyFactory(dbUtil)).build().getGraph());
		DeveloperNetwork dn = new DBDevAdjacencyFactory(dbUtil).build();
		content.add(new DeveloperNetworkExplorer(dn));
		frame.pack();
		frame.setVisible(true);
	}
}
