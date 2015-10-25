package core.components;

public abstract class WeightedEdge {

	private int edgeWeight;
	
	public WeightedEdge() {
		edgeWeight = 1;
	}
	
	public WeightedEdge(int weight) {
		this.edgeWeight = weight;
	}
	
	public int getEdgeWeight() {
		return this.edgeWeight;
	}
	
	public void setEdgeWeight(int edgeWeight) {
		this.edgeWeight = edgeWeight;
	}
	
}
