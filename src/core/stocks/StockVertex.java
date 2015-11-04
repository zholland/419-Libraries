package core.stocks;

import core.components.Vertex;

import java.util.Arrays;

public class StockVertex extends Vertex {

	private double[] dataPoints;
	private int index;
	private double average;
	private double max;
	private double min;
	private double variance;
	private double standardDeviation;

	private String company;

	public StockVertex() {
		dataPoints = new double[140];
		index = 0;
		average = 0;
		max = Integer.MIN_VALUE;
		min = Integer.MAX_VALUE;
	}

	public StockVertex(String company, int dataSize) {
		dataPoints = new double[dataSize];
		index = 0;
		average = 0;
		max = Integer.MIN_VALUE;
		min = Integer.MAX_VALUE;
		setId(company);
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
