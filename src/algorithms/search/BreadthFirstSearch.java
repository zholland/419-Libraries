package algorithms.search;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import core.components.Edge;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

/**
 * BFS Implementation for graphs with no edge weights. Uses graphs, not JUNG trees
 * 
 * @author Mike Nowicki
 *
 */
public class BreadthFirstSearch<V, E extends Edge> implements UninformedSearch<V, E> {

	VisualizationViewer<V, E> originalViewer;
	VisualizationViewer<ExtendedVertex<V>, Integer> searchViewer;
		
//	public static void main(String[] args) {
//		// Load the graphML reader
//		GraphLoader<Vertex, Edge> reader = new GraphMLReader();
//		// Load the graph
//		Graph<Vertex, Edge> graph = reader.loadGraph(10);
//		// Initialize the search method.  
//		BreadthFirstSearch<Vertex, Edge> bfs = new BreadthFirstSearch<>();
//
//		// Get first vertex pointed to by iterator as root
//		Vertex root = graph.getVertices().iterator().next();
//
//		// Search the graph   
//		List<Vertex> searchResults = bfs.search(graph, root);    // Syntax error.
//
//		String result = "";
//		for (Vertex v : searchResults) {
//		    result += v + ", ";
//		} 
//		// Trim trailing comma
//		result = result.substring(0, result.length() - 2);
//		System.out.println(result);
//
//		// Display the graph and resulting tree
//		bfs.visualizeSearch(graph, root);
//	}
	
	/**
	 * Simple breadth first search implementation. Currently proceeds in reverse order
	 * edges are added in the graph.
	 */
	public List<V> search(Graph<V,E> graph, V root) {
		List<V> nodeOrder = new ArrayList<>();
		Queue<V> queue = new ArrayDeque<>();
		
		boolean directed = false;
		
		if (graph.getDefaultEdgeType() == EdgeType.DIRECTED) {
			directed = true;
		}
		
		queue.add(root);
		
		while (!queue.isEmpty()) {
		
			V currentNode = queue.poll();
			
			nodeOrder.add(currentNode);
			
			Collection<V> neighbours = null;
			if (directed) {
				neighbours = getDirectedNeighbours(graph, currentNode);
			} else {
				neighbours = graph.getNeighbors(currentNode);
			}
			
			for (V neighbour : neighbours) {
				// Discovery check
				if (nodeOrder.contains(neighbour) || queue.contains(neighbour)){
					continue;
				}
				queue.add(neighbour);
				
			}
		}
		
		return nodeOrder;
	}

	/**
	 * Scans all outward edges from the current node and adds the destination
	 * to the collection of neighbours.
	 * 
	 * @param userGraph The graph being searched.
	 * @param currentNode The node who's neighbours we are looking for
	 * @return A collection of nodes 
	 */
	public Collection<V> getDirectedNeighbours(Graph<V, E> userGraph, V currentNode) {
		Collection<V> neighbours = new HashSet<>();
		for (E edge : userGraph.getOutEdges(currentNode)) {
			neighbours.add(userGraph.getDest(edge));
		}
		return neighbours;
	}

//	Visualization Stuff from here down -------------------------------------------------------
	
	/**
	 * Use jung illustration to view the BFS
	 * 
	 * @param root - The node to begin the search from.
	 */
	public void visualizeSearch(Graph<V,E> userGraph, V root) {
				
		Forest<ExtendedVertex<V>, Integer> wrapperGraph = new DelegateForest<>();
		
		List<List<V> > layerList = new ArrayList<>();
		List<V> discoveredList = new ArrayList<>();
		List<V> closedList = new ArrayList<>();
		
		boolean directed = false;
		E edge = userGraph.getEdges().iterator().next();
		if (userGraph.getEdgeType(edge) == EdgeType.DIRECTED) {
			directed = true;
		}

	
		// Overkill but will do for now
		for (int i = 0; i < userGraph.getVertexCount(); i++) {
			layerList.add(new ArrayList<V>());
		}
		
		// Use the wrapper class, make a queue of these extended nodes
		ExtendedVertex<V> extendedRoot = new ExtendedVertex<V>(root, 0);		
		Queue<ExtendedVertex<V> > queue = new ArrayDeque<>();		
		queue.add(extendedRoot);
		
		// Performs BFS, stores nodes and records the layers, maintaining expanded nodes in a bucket
		// to aid visualization
		int edgeCtr = 0;
		while (!queue.isEmpty()) {
		
			ExtendedVertex<V> currentVertex = queue.poll();
			V currentNode = currentVertex.getVertex();
			int layer = currentVertex.getLayer();
			
			layerList.get(layer).add(currentNode);
			closedList.add(currentNode);
			
			if (!wrapperGraph.containsVertex(currentVertex)){
				wrapperGraph.addVertex(currentVertex);
			}
			
			Collection<V> neighbours = null;
			if (directed) {
				neighbours = getDirectedNeighbours(userGraph, currentNode);
			} else {
				neighbours = userGraph.getNeighbors(currentNode);
			}
			
			for (V neighbour : neighbours) {	
				if (closedList.contains(neighbour) || discoveredList.contains(neighbour)) {
					continue;
				}
				discoveredList.add(neighbour);
				
				ExtendedVertex<V> extendedNeighbour = new ExtendedVertex<>(neighbour, layer+1);
				queue.add(extendedNeighbour);
				if (!wrapperGraph.containsVertex(extendedNeighbour)){
					wrapperGraph.addVertex(extendedNeighbour);
				}
				wrapperGraph.addEdge(new Integer(edgeCtr++), currentVertex, extendedNeighbour);
			}	
		}
	
		createGraphViewer(userGraph, wrapperGraph, root);
		
	}

	private void createGraphViewer(Graph<V, E> userGraph, Forest<ExtendedVertex<V>, Integer> wrapperGraph, V root) {

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
		double height = screenSize.getHeight();
				
		KKLayout<V, E> userLayout = new KKLayout<>(userGraph);
		userLayout.setSize(new Dimension((int)width, (int)height)); // sets the initial size of the space
		userLayout.setLengthFactor(1.5);
		
		originalViewer = new VisualizationViewer<V, E>(userLayout);
		originalViewer.setPreferredSize(new Dimension((int)width, (int)height/2));
		originalViewer.getRenderContext().setVertexFillPaintTransformer(new OriginalVertexPaint<V>(root));
		originalViewer.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<V>());	
		
		TreeLayout<ExtendedVertex<V>, Integer> layout = new TreeLayout<>(wrapperGraph, 250);
		searchViewer = new VisualizationViewer<>(layout);
		searchViewer.setPreferredSize(new Dimension((int)width, (int)height/2)); //Sets the viewing area size

		searchViewer.getRenderContext().setVertexFillPaintTransformer(new SearchVertexPaint<V>());
		searchViewer.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<ExtendedVertex<V> >());		
		
		PluggableGraphMouse pm = new PluggableGraphMouse();
		pm.add(new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0, 1 / 1.1f, 1.1f));   
		pm.add(new TranslatingGraphMousePlugin());      

		searchViewer.setGraphMouse(pm);
		originalViewer.setGraphMouse(pm);

		JFrame frame = new JFrame("Simple Graph View");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		frame.setExtendedState(frame.getExtendedState()|JFrame.MAXIMIZED_BOTH);
		frame.setLayout(null);
		
		JPanel originalPanel = new JPanel();
		JPanel searchPanel = new JPanel();
				
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
		
		originalPanel.setBounds(0, 0, (int)width, (int)height/2);
		searchPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,1,true), "Original Graph:"));
		originalPanel.add(originalViewer);
		c.add(originalPanel);
		
		searchPanel.setBounds(0, (int)(height/2)+10, (int)width, (int)height/2 - 10);
		searchPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,1,true), "Search Result:"));
		searchPanel.add(searchViewer);
		c.add(searchPanel);
		
		frame.pack();
		frame.setVisible(true); 

	}

}
