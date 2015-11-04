package core.stocks;

import core.components.Edge;
import core.components.Vertex;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;

import java.io.*;
import java.util.Collection;

public class StockGraphBuilder {

	/**
	 * Loads vertices from file and adds them to the graph
	 *
	 * @return - Initialized graph with all vertices
	 * @throws IOException
	 */
	private Graph<StockVertex, StockEdge> buildVertices() throws IOException {

		Graph<StockVertex, StockEdge> graph = new SparseGraph<>();

		int minLength = Integer.MAX_VALUE;

		File directory = new File("Results");
		
		for (String company : TSXCompanies.COMPANIES) {

			File file = new File(directory, company+".txt");
			InputStream ins = new FileInputStream(file);
			BufferedReader reader =  new BufferedReader(new InputStreamReader(ins));
			//First line is headers, skip to the next line.
			reader.readLine();
			String line = reader.readLine();

			int fileLength = getFileLength(file);
			if (fileLength < minLength) {
				minLength = fileLength;
			}

			StockVertex vertex = new StockVertex(company, fileLength);

			while (line != null) {
				String[] data = line.split(",");
				vertex.addData(Double.valueOf(data[4]));
				line = reader.readLine();
			}
			reader.close();

			vertex.computeAveragePrice();
			vertex.computeVariance();
			vertex.setCompanyName(company);

			graph.addVertex(vertex);

		}

		return graph;
		
	}

	/**
	 * Count number of data points for the file
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private int getFileLength(File file) throws IOException {

		int fileLength = 0;

		InputStream ins = new FileInputStream(file);
		BufferedReader reader =  new BufferedReader(new InputStreamReader(ins));

		while (reader.readLine() != null) {
			fileLength++;
		}
		reader.close();

		return fileLength-1;

	}

	// Here's a method that can be used to create the vertices for each stock. The
	// data points stored in a stock vertex are used to
	public static void main(String[] args) {

		StockGraphBuilder builder = new StockGraphBuilder();
		VisibilityGraph visGraph = new VisibilityGraph();

		// Use this to get the stock information. It won't crawl if it has
		// been run in the last 24 hours.
//		Crawler crawler = new Crawler();
//		crawler.crawl();

		Collection<StockVertex> vertices = builder.getStockVertices();

		StockVertex vertex = builder.getVertex(vertices, "AGU");
		Graph<Vertex, Edge> visibilityGraph = visGraph.createGraph(vertex);

		VisibilityGraphViewer.viewGraph(visibilityGraph);

	}

	/********************
	 * Helper methods   *
	 ********************/

	/**
	 * Initializes all the stock vertices based on the data in the
	 * Results directory and returns a collection of stock vertices.
	 * @return A collection of stock vertices that can be used to create a visibility graph.
	 */
	public Collection<StockVertex> getStockVertices() {
		try {
			return buildVertices().getVertices();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Given a collection of vertices find the specified vertex.
	 * @param vertices The collection of vertices to search in.
	 * @param id The String identifier of the vertex.
	 * @return The specified vertex, or null if it does not exist.
	 */
	public StockVertex getVertex(Collection<StockVertex> vertices, String id) {
		for (StockVertex vertex : vertices) {
			if (vertex.getId().equals(id)) {
				return vertex;
			}
		}
		return null;
	}




	////////////////////////////////////////////////////////////////////////////////////
	// The following code has no relevance to the project, it was created as part of a
	// different project.
	////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Computes the Pearson Correlation Coefficient for every pair of vertices
	 * in the graph and adds the edge to the graph.
	 *
	 * @param graph - Graph with all vertices loaded and no edges.
	 */
	private void buildAllEdges(Graph<StockVertex, StockEdge> graph) {

		for (StockVertex v1 : graph.getVertices()) {
			for (StockVertex v2 : graph.getVertices()) {

				if (v1.equals(v2)) {
					continue;
				}

				if (graph.isNeighbor(v1, v2)) {
					continue;
				}

				double[] dataSet1 = v1.getDataPoints();
				double[] dataSet2 = v2.getDataPoints();

				// Make sure that we are comparing data points over the same period
				// of days as the stock histories differ in length
				if (dataSet1.length <= dataSet2.length) {

					double avg1 = v1.getAverage();
					double avg2 = v2.getAverage();

					double numerator = 0;
					int difference = dataSet2.length - dataSet1.length;

					for (int index = dataSet2.length-1; index >= difference; index--) {
						numerator += (dataSet2[index] - avg2) * (dataSet1[index - difference] - avg1);
					}

					double standDev1 = 0;
					for (int index = 0; index < dataSet1.length; index++) {
						standDev1 += Math.pow((dataSet1[index] - avg1),2);
					}
					standDev1 = Math.sqrt(standDev1);

					double standDev2 = 0;
					for (int index = dataSet2.length-1; index >= difference; index--) {
						standDev2 += Math.pow((dataSet2[index] - avg2),2);
					}
					standDev2 = Math.sqrt(standDev2);

					double correlation = numerator/(standDev1 * standDev2);

					graph.addEdge(new StockEdge(correlation, v1, v2), v1, v2);

				} else {

					double avg1 = v1.getAverage();
					double avg2 = v2.getAverage();

					double numerator = 0;
					int difference = dataSet1.length - dataSet2.length;

					for (int index = dataSet1.length-1; index >= difference; index--) {
						numerator += (dataSet2[index - difference] - avg2) * (dataSet1[index] - avg1);
					}

					double standDev2 = 0;
					for (int index = 0; index < dataSet2.length; index++) {
						standDev2 += Math.pow((dataSet2[index] - avg2),2);
					}
					standDev2 = Math.sqrt(standDev2);

					double standDev1 = 0;
					for (int index = dataSet1.length-1; index >= difference; index--) {
						standDev1 += Math.pow((dataSet1[index] - avg1),2);
					}
					standDev1 = Math.sqrt(standDev1);

					double correlation = numerator/(standDev1 * standDev2);

					graph.addEdge(new StockEdge(correlation, v1, v2), v1, v2);

				}
			}
		}
	}

	/**
	 * Creates a graph where stocks are connected by edges that
	 * are weighted based on the correlation of the two companies
	 * stock prices.
	 * @return A graph of companies and their correlation between each other.
	 */
	public Graph<StockVertex, StockEdge> buildGraph() {

		try {
			Graph<StockVertex, StockEdge> graph = buildVertices();
			buildAllEdges(graph);
			return graph;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

}
