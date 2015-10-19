package core.stocks;

import core.components.Edge;

public class StockEdge extends Edge {

	private double correlationCoefficient;
	
	private StockVertex v1;
	private StockVertex v2;
	
	public StockEdge() {
		correlationCoefficient = 0;
	}
	
	public StockEdge(double coeff) {
		this.correlationCoefficient = coeff;
	}
	
	public StockEdge(double coeff, StockVertex v1, StockVertex v2) {
		this.correlationCoefficient = coeff;
		this.v1 = v1;
		this.v2 = v2;
	}
	
	public String toString() {
		return String.valueOf(correlationCoefficient);
	}
	
	public double getCorrelation() {
		return correlationCoefficient;
	}
	
	public StockVertex getV1() {
		return v1;
	}

	public StockVertex getV2() {
		return v2;
	}
}
