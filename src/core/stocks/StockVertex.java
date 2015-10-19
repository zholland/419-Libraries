package core.stocks;

import java.util.ArrayList;
import java.util.Arrays;

import core.components.Vertex;

public class StockVertex extends Vertex {

	private double[] dataPoints;
	private int index;
	private double average;
	private double max;
	private double min;
	private double variance;
	private double standardDeviation;

	private String company;
	
	private static final int visibilityGraphSize = 60;

	private ArrayList<ArrayList<Integer> > visibilityGraph;

	public StockVertex() {
		dataPoints = new double[140];
		index = 0;
		average = 0;
		max = Integer.MIN_VALUE;
		min = Integer.MAX_VALUE;
		visibilityGraph = new ArrayList<>();
	}

	public StockVertex(int dataSize) {
		dataPoints = new double[dataSize];
		index = 0;
		average = 0;
		max = Integer.MIN_VALUE;
		min = Integer.MAX_VALUE;
		visibilityGraph = new ArrayList<>();
	}

	public void initializeVisibilityGraph() {
		for (int i = 0; i <= visibilityGraphSize; i++) {
			visibilityGraph.add(new ArrayList<Integer>());
		}
		constructGraph();
	}

	private void constructGraph() {
		
		// Make graph for last 60 days
		int startPoint = dataPoints.length-61;
		
		for (int i = startPoint; i < dataPoints.length-1; i++) {

			boolean endOfNeighbourhood = false;
			int iAdjustedIndex = i - startPoint;

			for (int j = i+1; j < dataPoints.length; j++) {
				
				int jAdjustedIndex = j - startPoint;

				// Edge already exists between the two
				if (visibilityGraph.get(iAdjustedIndex).contains(jAdjustedIndex)) {
					continue;
				}

				// Neighbours are visible by default
				if (j == i+1) {
					visibilityGraph.get(iAdjustedIndex).add(jAdjustedIndex);
					visibilityGraph.get(jAdjustedIndex).add(iAdjustedIndex);
					continue;
				}

				// Contiune until an obstruction is reached, move to next index when end of neighbourhood reached.
				for (int k = i+1; k < j; k++) {

					double yA = dataPoints[i];
					double yB = dataPoints[j];
					double yC = dataPoints[k];

					double tA = i;
					double tB = j;
					double tC = k;

					double meanValue = yB + (yA - yB)*((tB - tC)/(tB - tA));

					// If a point is greater than or equal to the mean value then the points {ta,tb} are not visible to each other 
					if (yC >= meanValue) {
						endOfNeighbourhood = true;
						break;
					}
				}
				// At the end of the neighbourhood increase the index for ta
				if (endOfNeighbourhood) { 
					break;
				} else {
					// Otherwise no obstructions, add edge between them
					visibilityGraph.get(iAdjustedIndex).add(jAdjustedIndex);
					visibilityGraph.get(jAdjustedIndex).add(iAdjustedIndex);
				}
			}	
		}
	}

	public void addData(double data) {
		dataPoints[index] = data;

		if (dataPoints[index] > max) {
			max = dataPoints[index];
		}
		if (dataPoints[index] < min) {
			min = dataPoints[index];
		}

		index++;	
	}	

	public void computeAveragePrice() {
		for (int i = 0; i < dataPoints.length; i++) {
			average += dataPoints[i];	
		}
		average = average/dataPoints.length;
		average = Math.round(average * 100.0) / 100.0;
	}

	/**
	 * Computes and updates the variance and standard deviation of market prices
	 */
	public void computeVariance() {
		for (int i = 0; i < dataPoints.length; i++) {
			variance += Math.pow((dataPoints[i] - average),2);	
		}
		variance = variance/dataPoints.length;
		standardDeviation = Math.sqrt(variance);
	}

	public ArrayList<ArrayList<Integer>> getAdjacencyMatrix() {
		return visibilityGraph;
	}

	public void setCompanyName(String name) {
		this.company = name;
	}	

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public double getAverage() {
		return average;
	}

	public double getMax() {
		return max;
	}

	public double getMin() {
		return min;
	}

	public double getVariance() {
		return variance;
	}

	public double getStandardDeviation() {
		return standardDeviation;
	}

	public double[] getDataPoints() {
		return dataPoints;
	}

	@Override
	public boolean equals(Object otherVertex) {

		StockVertex v = (StockVertex)otherVertex;

		return Arrays.equals(this.dataPoints, v.getDataPoints());
	}

	public String toString() {
		return company;
	}
}
