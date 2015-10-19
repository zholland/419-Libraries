package core.webcrawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import core.stocks.StockEdge;
import core.stocks.StockVertex;
import core.stocks.TSXCompanies;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;

public class StockGraphBuilder {

	/**
	 * Loads vertices from file and adds them to the graph
	 * 
	 * @param spTSXCompanies - List of companies to load
	 * @param sparseGraph - The graph to store them in.
	 * @return - Initialized graph with all vertices
	 * @throws IOException
	 */
	private Graph<StockVertex, StockEdge> buildVertices(Graph<StockVertex, StockEdge> sparseGraph) throws IOException {

		int minLength = Integer.MAX_VALUE;

		File directory = new File("Results");
		
		for (String company : TSXCompanies.COMPANIES) {

			File file = new File(directory, company+".txt");
			InputStream ins = new FileInputStream(file);
			BufferedReader reader =  new BufferedReader(new InputStreamReader(ins));
			String line = reader.readLine();
			//First line is headers, skip to the next line.
			line = reader.readLine();

			int fileLength = getFileLength(file);
			if (fileLength < minLength) {
				minLength = fileLength;
			}

			StockVertex vertex = new StockVertex(fileLength);

			while (line != null) {
				String[] data = line.split(",");
				vertex.addData(Double.valueOf(data[4]));
				line = reader.readLine();
			}
			reader.close();

			vertex.computeAveragePrice();
			vertex.computeVariance();
			vertex.setCompanyName(company);

			sparseGraph.addVertex(vertex);

		}

		return sparseGraph;
		
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

	public Graph<StockVertex, StockEdge> buildGraph() {

		Graph<StockVertex, StockEdge> graph = new SparseGraph<>();
		
		try {
			buildVertices(graph);
			buildAllEdges(graph);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return graph;
	}
}
