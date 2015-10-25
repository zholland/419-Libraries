package algorithms.graphloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import core.components.Edge;
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


/**
 * Load a trivial graph file (TGF) into the application. TGF's are represented as an adjacency list
 * where each row indicates the neighbours of that vertex.
 * An example TGF is:
 * 1
 * 2
 * 3
 * #
 * 1 2 5
 * 1 3 6
 * 2 3 4
 *
 * Representing a triangle with edge weights (5, 6, 4). The hashtag separates the vertices from the
 * edges.
 * 
 * 
 * @author Mike Nowicki
 *
 */
public class TGFLoader {

	/**
	 * Load the graph file. Converts to a sequence of integers to be
	 * handled to convert to a simple node structure. For parsing graphs with
	 * unweighted edges.
	 *
	 * @param graphType - Integer indicating which type of graph to initialize. Any value
	 * 					  outside of 1 - 11 will create a sparse graph. The others can be
	 * 					  initialized using the following integer code:
	 * 				
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
	 */
	public Graph<Integer, Edge> loadGraph(int graphType, boolean isWeighted) {

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        FileNameExtensionFilter graphFilter = new FileNameExtensionFilter("Trivial Graph FIle (*.tgf)", "tgf");
        // add filters
		fileChooser.addChoosableFileFilter(graphFilter);
		fileChooser.setFileFilter(graphFilter);
		
		if (fileChooser.showOpenDialog(fileChooser) == JFileChooser.APPROVE_OPTION) {

			File tgf = fileChooser.getSelectedFile();
				
			try {
				
				FileReader reader = new FileReader(tgf);
				BufferedReader bufferedReader = new BufferedReader(reader);
				String line = bufferedReader.readLine();

				Graph<Integer, Edge> graph = getGraph(graphType);

				while (!line.equals("#")) {
					line = line.trim();
					Integer vertex = Integer.valueOf(line);
					graph.addVertex(vertex);
					line = bufferedReader.readLine();
				}
				// Consume line and move on
				line = bufferedReader.readLine();
				
				// Parse edges
				while (line != null) {
				
					String[] edgeSpecs = line.split(" ");
					
					// Should only be two as it is an unweighted graph file
					Integer v1 = Integer.valueOf(edgeSpecs[0]);
					Integer v2 = Integer.valueOf(edgeSpecs[1]);
					Edge newEdge;
					
					if (isWeighted) {
						newEdge = new Edge(Integer.valueOf(edgeSpecs[2].trim()));
					} else {
						newEdge = new Edge(1);
					}
					
					graph.addEdge(newEdge, v1, v2);

					line = bufferedReader.readLine();
					
				}
				
				reader.close();
				return graph;
				
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null,
						"Must select a valid Trivial Graph Format (.tgf) file.",
						"Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		return null;
	}

	private Graph<Integer, Edge> getGraph(int graphType) {

		switch(graphType) {
			case 1:
				return new SparseMultigraph<>();
			case 2:
				return new DelegateForest<>();
			case 3:
				return new DelegateTree<>();
			case 4:
				return new SortedSparseMultigraph<>();
			case 5:
				return new OrderedSparseMultigraph<>();
			case 6:	
				 return new UndirectedOrderedSparseMultigraph<>();
			case 7: 
				 return new UndirectedSparseGraph<>();
			case 8:	 
				 return new UndirectedSparseMultigraph<>();
			case 9:
				 return new DirectedOrderedSparseMultigraph<>();
			case 10:
				 return new DirectedSparseGraph<>();
			case 11:	 
				 return new DirectedSparseMultigraph<>();
			default:
				return new SparseGraph<>();
		}
	}	

}
