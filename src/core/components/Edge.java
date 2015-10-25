package core.components;

import java.util.HashMap;

/**
 * Basic edge class. Extend as needed.
 * 
 * @author Mike Nowicki
 *
 */
public class Edge {

	/**
	 * Edge id
	 */
	private String id;
	/**
	 * The weight of the edge
	 */
	private Number weight;
	/**
	 * Collection of attributes defining the edge
	 */
	private HashMap<String, Object> edgeAttributes;
	
	public Edge() {
		// Initialize to empty string and Integer 0
		this("", 0);
	}

	public Edge(String id) {
		this(id,0);
	}
	
	public Edge(Number weight) {
		// Initialize to empty string and given weight
		this("", weight);
	}

	public Edge(String id, Number weight) {
		this.id = id;
		this.weight = weight;
	}

	/**
	 * Add the given attribute to the map with the provided value.
	 * @param attribute The attribute name/type
	 * @param value The attribute value
	 *
	 * @return An object if successfully added, null otherwise.
	 */
	public Object addAttribute(String attribute, Object value) {
		if (edgeAttributes == null) {
			edgeAttributes = new HashMap<>();
		}
		return edgeAttributes.put(attribute, value);
	}

	// Collection of getters/setters for weights and ID's
	
	public Number getWeight() {
		return this.weight;
	}
	
	public void setWeight(Number weight) {
		this.weight = weight;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return this.id;
	}
	
	public String toString() {
		return this.id;
	}
}
