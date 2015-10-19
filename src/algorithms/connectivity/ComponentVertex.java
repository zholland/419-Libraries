package algorithms.connectivity;

/**
 * This class wraps the vertices in the provided graph to store
 * the additional information needed for connected components.
 *
 * @param <T> The type of vertex being wrapped
 */
public class ComponentVertex<T> {

	private int componentID;
	/**
	 * The original vertex
	 */
	private T data;
	
	public ComponentVertex(T data) {
		this.data = data;
	}
	
	public void setComponentNumber(int componentID) {
		this.componentID = componentID;
	}
	
	public int getComponentNumber() {
		return componentID;
	}
	
	public T getData() {
		return this.data;
	}
	
	public String toString() {
		return "" + componentID;
	}
}
