package core.components;

public interface Heuristic<V extends State> {

	public int evaluate(V vertex);
	
}
