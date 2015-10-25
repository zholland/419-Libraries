package algorithms.graphloader;

import core.components.Edge;
import core.components.Vertex;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.OrderedSparseMultigraph;
import edu.uci.ics.jung.graph.SortedSparseMultigraph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.UndirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;

public interface GraphLoader<V, E> {

	int SparseGraph = 0;
	int SparseMultigraph = 1;
	int DelegateForest =	2;
	int DelegateTree = 3;
	int SortedSparseMultigraph = 4;
	int OrderedSparseMultigraph = 5;
	int UndirectedOrderedSparseMultigraph = 6;
	int UndirectedSparseGraph = 7;
	int UndirectedSparseMultigraph = 8;
	int DirectedOrderedSparseMultigraph = 9;
	int DirectedSparseGraph = 10;
	int DirectedSparseMultigraph = 11;

	/**
	 * Loads the graph file into the type of graph passed.
	 * 
	 * @param graph - The graph to load to
	 * @return - The graph as specified by the given file.
	 * @throws GraphLoadingException 
	 */
	Graph<V, E> loadGraph(Graph<V, E> graph) throws GraphLoadingException;
	
	/**
	 * Load the graph file, indicate which type of graph to use
	 * by passing an integer [0-11]
	 * 
	 * @param graphType - An integer from 0-11
	 * @return The type of graph specified.
	 */
	Graph<V, E> loadGraph(int graphType);

	/**
	 *  Instance where graph type isn't specified, loads a SparseGraph
	 *  
	 *  
	 *  @return The JUNG graph after loading from the GraphML file.
	 */	
	Graph<Vertex, Edge> loadGraph();	
	
	/**
	 * Allows the user to get a new graph instance specified by 
	 * 
	 * * @param graphType - Integer indicating which type of graph to initialize. Any value
	 * 					  outside of 1 - 11 will create a generic sparse graph. The others 
	 * 					  can be initialized using the following integer code:
	 * 				
	 * 						0 - SparseGraph
	 * 						1 - SparseMultigraph
	 * 						2 - DelegateForest
	 * 						3 - DelegateTree
	 * 						4 - SortedSparseMultigraph
	 * 						5 - OrderedSparseMultigraph
	 * 						6 - UndirectedOrderedSparseMultigraph
	 * 						7 - UndirectedSparseGraph
	 * 						8 - UndirectedSparseMultigraph
	 * 						9 - DirectedOrderedSparseMultigraph
	 * 						10 - DirectedSparseGraph
	 * 						11 - DirectedSparseMultigraph
	 * 
	 * @return - The type of graph specified
	 */
	default Graph<V, E> getGraph(int graphType) {
				
		switch(graphType) {
			case SparseMultigraph:
				return new SparseMultigraph<>();
			case DelegateForest:
				return new DelegateForest<>();
			case DelegateTree:
				return new DelegateTree<>();
			case SortedSparseMultigraph:
				return new SortedSparseMultigraph<>();
			case OrderedSparseMultigraph:
				return new OrderedSparseMultigraph<>();
			case UndirectedOrderedSparseMultigraph:	
				 return new UndirectedOrderedSparseMultigraph<>(); 
			case UndirectedSparseGraph: 
				 return new UndirectedSparseGraph<>();
			case UndirectedSparseMultigraph:	 
				 return new UndirectedSparseMultigraph<>();
			case DirectedOrderedSparseMultigraph:
				 return new DirectedOrderedSparseMultigraph<>();
			case DirectedSparseGraph:
				 return new DirectedSparseGraph<>();
			case DirectedSparseMultigraph:	 
				 return new DirectedSparseMultigraph<>();	 
			default:
				return new SparseGraph<>();
		}
	}
}
