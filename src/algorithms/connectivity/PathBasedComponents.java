package algorithms.connectivity;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

//import core.components.Edge;
//import core.components.Vertex;
//
//import algorithms.graphloader.*;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

/**
 *  Generic implementation of Dijkstra's Shortest Path Connected
 *  components algorith. The algorithm works as follows:
 *	
 *  There is a stack S that maintains a record of which nodes have been assigned to a connected component.
 *  There is a stack P that keeps track of nodes visited that have not been assigned to a connected component.
 *	There is a count C that keeps track of the number of nodes visited during the DFS.
 *	There is a vector a that maps each node to -1 until it is assigned to a connected component
 *	There is a vector p called preorder_map that stores where the vertex was found along the path.
 *	These are initially -1 to indicate is has not been visited yet.
 *
 *	The algorithm proceeds as follows:
 *
 *	For each node v in G(V,E):
 *		1. Set p[v] = C
 *		2. Push v onto P and S
 *		3. For each neighbour w of v:
 *			if p[w] == -1: recursively search w;
 *			else if a[v] == -1:
 *				Keep popping vertices from P until the top vertex u on the stack has p[u] > p[w]
 *		4. If v is on top of P
 *			Pop vertices from S until v has been popped and assign them to the same connected component.
 *			Then pop v from P.
 * 
 * @author Mike Nowicki
 *
 * @param <V> - Vertex type, must extend the ComponentVertex class
 * @param <E> - Edge type
 */
public class PathBasedComponents<V, E> implements StrongConnectedComponents<V, E> {

//	public static void main(String[] args) {
//
//		GraphLoader<Vertex, Edge> reader = new GraphMLReader();
//		// Load the graph
//		Graph<Vertex, Edge> graph = reader.loadGraph();
//		PathBasedComponents<Vertex, Edge> pbc = new PathBasedComponents<>();
//		pbc.findComponents(graph);
//		pbc.visualizeSearch();
//	}

	/**
	 * The a list that contains lists representing each connected
	 * component in the graph.
	 */
	private List<List<V>> components;
	/**
	 * This is the stack S in the algorithm for tracking
	 * nodes in the component
	 */
	private Stack<ComponentVertex<V>> unassignedCC;
	/**
	 * The stack P from the algorithm tracking the current
	 * search path.
	 */
	private Stack<ComponentVertex<V>> currentPath;
	/**
	 * This maps vertices to the depth the vertex was visited at
	 */
	private HashMap<ComponentVertex<V>, Integer> preorderMap;
	/**
	 * This map keeps a record of which vertices have been assigned
	 * to connected components.
	 */
	private HashMap<ComponentVertex<V>, Integer> assignedVars;
	/**
	 * The graph to search. It is a wrapping of the given user graph.
	 */
	private Graph<ComponentVertex<V>, E> wrappedGraph;

	/**
	 * Counter for tracking how deep on the search path we are
	 */
	private int ctr = 1;
	/**
	 * Keep track of which component number we are at
	 */
	private int ccNumber = 0;
	
	public PathBasedComponents() {
		components = new ArrayList<>();
		unassignedCC = new Stack<>();
		currentPath = new Stack<>();
		preorderMap = new HashMap<>();
		assignedVars = new HashMap<>();
	}
	
	@Override
	public List<List<V>> findComponents(Graph<V, E> graph) {

		components.clear();
		unassignedCC.clear();
		currentPath.clear();
		preorderMap.clear();
		assignedVars.clear();
		
		// Store user node in a wrapper vertex with info needed
		// for detecting components.
		wrappedGraph = wrapGraph(graph);

		// Place each vertex in the maps and initialize the values
		for (ComponentVertex<V> v : wrappedGraph.getVertices()) {
			preorderMap.put(v,-1);
			assignedVars.put(v,-1);
		}

		// While there is a vertex not assigned to a component perform a DFS
		// search from the vertex
		while (assignedVars.containsValue(-1)) {
			for (ComponentVertex<V> vertex : wrappedGraph.getVertices()) {
				if (assignedVars.get(vertex) == -1) {
					dfs(wrappedGraph, vertex);
				}	
			}	
		}
		return components;
	}

	private void dfs(Graph<ComponentVertex<V>, E> graph, ComponentVertex<V> vertex) {
		
		// Set preorder value
		preorderMap.put(vertex, ctr);
		ctr++;
		
		// Push onto stacks
		unassignedCC.push(vertex);
		currentPath.push(vertex);
		
		Collection<ComponentVertex<V>> neighbours = new HashSet<>();
		// Need to use Out edges to ensure correctness for digraphs
		for (E edge : graph.getOutEdges(vertex)) {
			neighbours.add(graph.getDest(edge));
		}
		
		// Iterate over neighbours
		for (ComponentVertex<V> neighbour : neighbours) {
			// If not marked yet continue dfs from neighbour
			if (preorderMap.get(neighbour) == -1) {
				dfs(graph, neighbour);
			} 
			// Otherwise, if it is not assigned remove all nodes on path with a higher ordering
			else if (assignedVars.get(neighbour) == -1) {
				while(preorderMap.get(currentPath.peek()) > preorderMap.get(neighbour)) {
					currentPath.pop();
				}
			}
		}
		// If the removing terminates at the given vertex add all remaining 
		// into the connected component, clearing the path and finally increasing
		// the connected component number.
		if (currentPath.peek().equals(vertex)) {
			components.add(new ArrayList<>());
			while (!unassignedCC.isEmpty()) {
				ComponentVertex<V> componentPart = unassignedCC.pop();
				components.get(ccNumber).add(componentPart.getData());
				componentPart.setComponentNumber(ccNumber);
				assignedVars.put(componentPart, 1);
				
				if (componentPart.equals(vertex)) {
					currentPath.pop();
					ccNumber++;
					break;
				}
			} // end while
		} // end if
	}
	
	/**
	 * At worst O(|V|^2) time to wrap the generic type vertices into
	 * the ComponentVertices used for the search and visualization.
	 * 
	 * @param graph The generic graph
	 * @return Graph with the same structure, however vertices are wrapped
	 * 			 and edges are Integers
	 */
	private Graph<ComponentVertex<V>, E> wrapGraph(Graph<V, E> graph) {
		
		Graph<ComponentVertex<V>, E> wrappedGraph = new SparseGraph<>();
		HashMap<V, ComponentVertex<V>> wrappedVertices = new HashMap<>();
		
		for (V v : graph.getVertices()) {
			ComponentVertex<V> wrappedVertex = new ComponentVertex<V>(v);
			wrappedVertices.put(v, wrappedVertex);
			wrappedGraph.addVertex(wrappedVertex);
		}
		
		for (V v : graph.getVertices()) {
		
			Collection<V> neighbours = new HashSet<>();
			for (E edge : graph.getOutEdges(v)) {
				neighbours.add(graph.getDest(edge));
			}
			for (V neighbour : neighbours) {			
				ComponentVertex<V> source = wrappedVertices.get(v);
				ComponentVertex<V> destination = wrappedVertices.get(neighbour);
				// If they are not neighbours yet add an edge between the two vertices
				if (!wrappedGraph.isNeighbor(wrappedVertices.get(v), wrappedVertices.get(neighbour))) {
					wrappedGraph.addEdge(graph.findEdge(v, neighbour), source, destination, EdgeType.DIRECTED);
				}
			}
		}		
		return wrappedGraph;
	}
	
	public Graph<ComponentVertex<V>, E> getWrappedGraph() {
		return this.wrappedGraph;
	}
	
//	Visualization Stuff from here down -------------------------------------------------------
	
	/**
	 * Draws the graph with each connect component coloured
	 */
	public void visualizeSearch() {
		
		if (wrappedGraph == null) {
			System.out.println("Graph has not been initialized.");
			return;
		}
		
		if (wrappedGraph.getVertexCount() == 0) {
			System.out.println("Graph has not been loaded yet.");
			return;
		}
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
		double height = screenSize.getHeight();
		
		VisualizationViewer<ComponentVertex<V>, E> originalViewer;
				
		KKLayout<ComponentVertex<V>, E> userLayout = new KKLayout<>(wrappedGraph);
		userLayout.setSize(new Dimension((int)width, (int)height)); // sets the initial size of the space
		userLayout.setLengthFactor(1.5);
		
		originalViewer = new VisualizationViewer<>(userLayout);
		originalViewer.setPreferredSize(new Dimension((int)width, (int)height));
		originalViewer.getRenderContext().setVertexFillPaintTransformer(new VertexPaint());
		originalViewer.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<ComponentVertex<V>>());
		
		PluggableGraphMouse pm = new PluggableGraphMouse();
		pm.add(new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0, 1 / 1.1f, 1.1f));   
		pm.add(new TranslatingGraphMousePlugin());      
		originalViewer.setGraphMouse(pm);

		JFrame frame = new JFrame("Connected Component Viewer");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
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
		});
		
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

	/**
	 * Colours vertices based on the component id. Handles 13
	 * unique components. Currently is a lazy hardcoded solution,
	 * better graphic design can be done at a later date.
	 * 
	 * @author Mike-SSD
	 *
	 */
	private class VertexPaint implements Transformer<ComponentVertex<V>, Paint> {
		
		private ArrayList<Color> colours;
		
		public VertexPaint() {
			colours = new ArrayList<>(13);
			loadColours();
		}
		
		@Override
		public Paint transform(ComponentVertex<V> state) {		
			return colours.get(state.getComponentNumber());
		}
		
		private void loadColours() {			
			colours.add(Color.BLACK);
			colours.add(Color.BLUE);
			colours.add(Color.GREEN);
			colours.add(Color.LIGHT_GRAY);
			colours.add(Color.YELLOW);
			colours.add(Color.WHITE);
			colours.add(Color.CYAN);
			colours.add(Color.RED);
			colours.add(Color.MAGENTA);
			colours.add(Color.ORANGE);
			colours.add(Color.DARK_GRAY);
			colours.add(Color.PINK);
			colours.add(Color.GRAY);
		}	
	}
	
	/**
	 * Private nested vertex class that is used to wrap the original
	 * graph. 
	 * 
	 * @author Mike Nowicki
	 *
	 * @param <T> - The type of the original vertex
	 */
	private class ComponentVertex<T> {

		private int componentID;
		private T data;
		
		public ComponentVertex(T data) {
			this.data = data;
		}
		
		public void setComponentNumber(int componentID) {
			this.componentID = componentID;
		}
		
		public int getComponentNumber() {
			return componentID;
		}
		
		public T getData() {
			return this.data;
		}
		
		public String toString() {
			return "" + componentID;
		}
	}
}
