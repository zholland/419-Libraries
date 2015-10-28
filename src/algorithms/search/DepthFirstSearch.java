package algorithms.search;

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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class DepthFirstSearch<V, E extends Edge> implements UninformedSearch<V, E>{

	VisualizationViewer<V, E> originalViewer;
	VisualizationViewer<ExtendedVertex<V>, Integer> searchViewer;


	/**
	 * Non-recursive implementation of a Depth First Search
	 * @param graph The graph to search
	 * @param root The root node to search from.
	 * @return A list with the ordering nodes were visited during
	 *         the search.
	 */
	public List<V> search(Graph<V,E> graph, V root) {

		List<V> nodeOrder = new ArrayList<>();
		Stack<V> openList = new Stack<>();
		boolean directed = false;

		if (graph.getDefaultEdgeType() == EdgeType.DIRECTED) {
			directed = true;
		}
		
		openList.push(root);
		
		while (!openList.isEmpty()) {
	
			V currentNode = openList.pop();
			nodeOrder.add(currentNode);
			
			Collection<V> neighbours = null;
			
			if (directed) {
				neighbours = getDirectedNeighbours(graph, currentNode);
			} else {
				neighbours = graph.getNeighbors(currentNode);
			}
			
			for (V neighbour : neighbours) {
				if (nodeOrder.contains(neighbour) || openList.contains(neighbour)){
					continue;
				}
				openList.push(neighbour);
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

	@Override
	public void visualizeSearch(Graph<V, E> userGraph, V root) {

		Forest<ExtendedVertex<V>, Integer> wrapperGraph = new DelegateForest<>();
		List<List<V> > layerList = new ArrayList<>();

		boolean directed = false;
		E edge = userGraph.getEdges().iterator().next();
		if (userGraph.getEdgeType(edge) == EdgeType.DIRECTED) {
			directed = true;
		}


		// Overkill but will do for now
		for (int i = 0; i <= userGraph.getVertexCount(); i++) {
			layerList.add(new ArrayList<V>());
		}

		List<ExtendedVertex<V>> nodeOrder = new ArrayList<>();
		Stack<ExtendedVertex<V>> openList = new Stack<>();
		ExtendedVertex<V> extendedRoot = new ExtendedVertex<V>(root, 0);

		openList.push(extendedRoot);

		int edgeCtr = 0;

		while (!openList.isEmpty()) {

			ExtendedVertex<V> currentVertex = openList.pop();
			V currentNode = currentVertex.getVertex();
			int layer = currentVertex.getLayer();

			layerList.get(layer).add(currentNode);

			nodeOrder.add(currentVertex);
			Collection<V> neighbours = null;

			if (directed) {
				neighbours = getDirectedNeighbours(userGraph, currentNode);
			} else {
				neighbours = userGraph.getNeighbors(currentNode);
			}

			for (V neighbour : neighbours) {
				ExtendedVertex<V> nextVertex = new ExtendedVertex<>(neighbour, layer+1);
				if (nodeOrder.contains(nextVertex) || openList.contains(nextVertex)){
					continue;
				}
				if (!wrapperGraph.containsVertex(nextVertex)){
					wrapperGraph.addVertex(nextVertex);
				}
				wrapperGraph.addEdge(new Integer(edgeCtr++), currentVertex, nextVertex);
				openList.push(nextVertex);
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

//	public static void main(String[] args) {
//		GraphLoader<Vertex, Edge> graphLoader = new GraphMLReader();
//		Graph<Vertex, Edge> graph = graphLoader.loadGraph(0);
//
//		new DepthFirstSearch<Vertex, Edge>().visualizeSearch(graph, graph.getVertices().iterator().next());
//	}
}
