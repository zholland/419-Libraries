package algorithms.search;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import core.components.Edge;
import core.components.Vertex;
import edu.uci.ics.jung.graph.Graph;

/**
 * Generic Uniform cost search method. This is meant for well defined graph searches, 
 * does not handle implicit generation of states (YET). Graph must be an implementation
 * of the generic Node and Edge classes provided.
 * 
 * @author Mike Nowicki
 *
 * @param <V> - The weight type for the search
 */
public class UniformCostSearch<V extends Vertex, E extends Edge> {

	public UniformCostSearch() { }

	public List<V> search(Graph<V, E> graph, V root, V goal) {

		List<V> nodeOrdering = new ArrayList<>();
		List<V> closedList = new ArrayList<>();
		Queue<V> frontier = new PriorityQueue<V>(new NodeComparator());
		int totalCost = 0;
		
		frontier.add(root);
		
		while (!frontier.isEmpty()) {
			
			V currentNode = frontier.poll();
			nodeOrdering.add(currentNode);
			
			totalCost += (Double)currentNode.getCost();
			
			if (currentNode.equals(goal)) {
				System.out.println(totalCost);
				return nodeOrdering;
			}
			
			closedList.add(currentNode);
			
			for (V neighbour : graph.getNeighbors(currentNode)) {
				
				E e = graph.findEdge(currentNode, neighbour);
				
				Double pathCost = (Double)currentNode.getCost() + (Double)e.getWeight();
				neighbour.setCost(pathCost);
				
				if (!closedList.contains(neighbour) && !frontier.contains(neighbour)) {
					frontier.add(neighbour);
				} else if (frontier.contains(neighbour)) {
					// Something here breaks it, might need a custom queue..........
					for (Vertex n : frontier) {
						if (n.equals(neighbour)) {
							if (n.getCost().doubleValue() > neighbour.getCost().doubleValue()) {
								frontier.remove(n);
								frontier.add(neighbour);
								break;
							}
						}
					}
					
					frontier.remove(neighbour);
					frontier.add(neighbour);
				}
			}	
		}
		return null;
	}
	
	private class NodeComparator implements Comparator<Vertex>{
		@Override
		public int compare(Vertex lhs, Vertex rhs) {
			if (lhs.getCost().doubleValue() < rhs.getCost().doubleValue()) {
				return -1;
			} else if (lhs.getCost() == rhs.getCost()) {
				return 0;
			}
			return 1;
		}	
	}
}
