package core.visualizer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import edu.uci.ics.jung.algorithms.layout.Layout;
import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

/**
 * General purpose visualization class, provides a full screen viewing with panning, scaling
 * and the ability to click/drag vertices.
 * 
 * @author Mike Nowicki
 *
 */
public class Visualizer {

	/**
	 * Creates a new frame and presents the forest/tree using the JUNG TreeLayout.
	 *
	 * @param forest The forest to view.
	 * @param <V> The vertex type.
	 * @param <E> The edge type.
	 */
	public static <V,E> void viewTree(Forest<V,E> forest) {
		viewTree(forest, null);
	}

	/**
	 * General viewer for the graph.
	 *
	 * @param graph - The graph to visualize.
	 * @param <V> The vertex type.
	 * @param <E> The edge type.
	 */
	public static <V,E> void viewGraph(Graph<V,E> graph) {
		viewGraph(graph, null, false);
	}

	/**
	 * View the graph and choose to show the edge labels.
	 * @param graph The graph to view.
	 * @param viewEdgeLabels Boolean indicating if edge labels should be shown.
	 * @param <V> The vertex type.
	 * @param <E> The edge type.
	 */
	public static <V,E> void viewGraph(Graph<V,E> graph, boolean viewEdgeLabels) {
		viewGraph(graph, null, viewEdgeLabels);
	}

	/**
	 * General viewer that highlights a specific node (the root)
	 * @param graph - The graph to view
	 * @param node - A node to highlight (typically to show where searches began)
	 * @param <V> The vertex type.
	 * @param <E> The edge type.
	 */
	public static <V,E> void viewGraph(Graph<V,E> graph, V node, boolean viewEdgeLabels) {
		viewGraph(graph, null, node, viewEdgeLabels);
	}

	/**
	 * Visualize the forest with a given layout. If null is provided for the
	 * layout a new TreeLayout will be created for the visualization.
	 * @param forest The forest/tree to visualize.
	 * @param givenLayout The layout to use or null.
	 * @param <V> The vertex type.
	 * @param <E> The edge type.
	 */
	public static <V,E> void viewTree(Forest<V,E> forest, Layout<V,E> givenLayout) {

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
		double height = screenSize.getHeight();

		Layout<V,E> layout = givenLayout;

		if (givenLayout == null ) {
			layout = new TreeLayout<>(forest);
		}

		VisualizationViewer<V,E> vv =	new VisualizationViewer<>(layout);
		vv.setPreferredSize(new Dimension((int)width, (int)height)); //Sets the viewing area size

		Transformer<V, Paint> vertexPaint = new Transformer<V, Paint>() {
			public Paint transform(V v) {
				return Color.RED;
			}
		};

		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<V>());

		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<E>());

		// Set structure of JUNG pane and mouse control.
		GraphZoomScrollPane zoomPane = new GraphZoomScrollPane(vv);

		PluggableGraphMouse pm = new PluggableGraphMouse();
		pm.add(new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0, 1 / 1.1f, 1.1f));
		pm.add(new TranslatingGraphMousePlugin());

		vv.setGraphMouse(pm);

		createFrame(vv, zoomPane);
	}

	/**
	 * View the graph with the layout provided by the user.
	 * @param givenLayout The layout style to view with, can be null.
	 * @param graph The graph to view.
	 * @param node The vertex to indicate as the root, if any.
	 * @param viewEdgeLabels Boolean to indicate if edge labels should be shown.
	 * @param <V> The vertex type.
	 * @param <E> The edge type.
	 */
	public static <V,E> void viewGraph(Graph<V,E> graph, Layout<V,E> givenLayout, V node, boolean viewEdgeLabels) {

		Layout<V,E> layout = givenLayout;

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
		double height = screenSize.getHeight();

		if (layout == null) {
			layout = new KKLayout<>(graph);
		}

		VisualizationViewer<V,E> vv =	new VisualizationViewer<>(layout);
		vv.setPreferredSize(new Dimension((int)width, (int)height)); //Sets the viewing area size

		Transformer<V, Paint> vertexPaint = new Transformer<V, Paint>() {
			public Paint transform(V v) {
				if (v == node) {
					return Color.YELLOW;
				}
				return Color.RED;
			}
		};
		
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
		
		if (viewEdgeLabels) {
			vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<E>());
		}

		// Set structure of JUNG pane and mouse control.
		GraphZoomScrollPane zoomPane = new GraphZoomScrollPane(vv);

		PluggableGraphMouse pm = new PluggableGraphMouse();
		pm.add(new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0, 1 / 1.1f, 1.1f));
		pm.add(new TranslatingGraphMousePlugin());

		vv.setGraphMouse(pm);

		createFrame(vv, zoomPane);

	}

	private static <V,E> void createFrame(VisualizationViewer<V, E> vv, GraphZoomScrollPane zoomPane) {

		JFrame frame = new JFrame("Simple Graph View");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setExtendedState(frame.getExtendedState()|JFrame.MAXIMIZED_BOTH);
				
		JMenuBar menuPanel = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
			
		});
		
		file.add(exit);	
		menuPanel.add(file);
		frame.setJMenuBar(menuPanel);

		frame.getContentPane().add(zoomPane);
		frame.setContentPane(vv);
		
		frame.pack();
		frame.setVisible(true); 

	}
	
}
