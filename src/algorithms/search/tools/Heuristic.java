package algorithms.search.tools;

public interface Heuristic<V extends State> {

	public int evaluate(V vertex);
	
}
