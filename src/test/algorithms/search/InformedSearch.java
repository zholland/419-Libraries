package test.algorithms.search;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.apache.commons.collections15.Transformer;

import algorithms.search.tools.State;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

/**
 * Informed search interface that defines the primary methods to include.
 * 
 * @author Mike Nowicki
 *
 * @param <V> - The vertex type, must extend the Search.Tool.State class
 * @param <E> - The edge type.
 */
public interface InformedSearch<V extends State,E> {
		
	/**
	 * Main search method.
	 * 
	 * @param root - The root node to search from.
	 * @param goal - The node being searched for.
	 * 
	 * @return - A list indicating the order nodes were traversed, or a
	 * 			null list if the root is not in the graph.
	 */
	List<V> search(V start, V goal);
	
	/**
	 * Builds a list of the nodes that were traversed
	 * by backtracking from the goal
	 * 
	 * @param path - A hashmap indicating child(key) and parent(value)
	 * @param node - The node to trace back from.
	 * 
	 * @return - A list indicating the order nodes were traversed, or a
	 * 			null list if the root is not in the graph.
	 */
	List<V> tracePath(HashMap<V, V> path, V node);
	
	/**
	 * Allows for a simple visualization to show
	 * the search path.
	 */
	default void visualizeSearch(Graph<V,E> graph) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
		double height = screenSize.getHeight();
		
		VisualizationViewer<V, E> originalViewer;
				
		KKLayout<V, E> userLayout = new KKLayout<>(graph);
		userLayout.setSize(new Dimension((int)width, (int)height)); // sets the initial size of the space
		userLayout.setLengthFactor(1.5);
		
		originalViewer = new VisualizationViewer<V, E>(userLayout);
		originalViewer.setPreferredSize(new Dimension((int)width, (int)height));
		originalViewer.getRenderContext().setVertexFillPaintTransformer(new VertexPaint<V>());
		originalViewer.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<V>());
		originalViewer.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<E>());
		
		PluggableGraphMouse pm = new PluggableGraphMouse();
		pm.add(new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0, 1 / 1.1f, 1.1f));   
		pm.add(new TranslatingGraphMousePlugin());      
		originalViewer.setGraphMouse(pm);

		JFrame frame = new JFrame("Simple Graph View");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		frame.setExtendedState(frame.getExtendedState()|JFrame.MAXIMIZED_BOTH);
		frame.setLayout(null);
		
		JPanel originalPanel = new JPanel();
				
		JMenuBar menuPanel = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				frame.dispose();
			}
			
		}
		);
		
		frame.setBounds(0, 0, (int)width, (int)height);
		
		file.add(exit);	
		menuPanel.add(file);
		frame.setJMenuBar(menuPanel);
		
		Container c = frame.getContentPane();
		
		originalPanel.setBounds(0, 0, (int)width, (int)height);
		originalPanel.add(originalViewer);
		c.add(originalPanel);
		
		frame.pack();
		frame.setVisible(true); 
	}
	
	class VertexPaint<T extends State> implements Transformer<T, Paint> {

		@Override
		public Paint transform(T state) {

			if (state.isStart()) {
				return Color.GREEN;
			}
			if (state.isGoal()) {
				return Color.RED;
			}
			if (state.isOnPath()) {
				return Color.YELLOW;
			}
			if (state.isSearched()) {
				return Color.BLUE;
			}
			return Color.BLACK;
		}

	}
	
}
