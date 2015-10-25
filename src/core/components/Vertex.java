package core.components;

import java.util.HashMap;

/**
 * Default vertex class used by the package. Is extended to a StockVertex
 * for graphs using the web crawler.
 * 
 * @author Mike Nowicki
 *
 */
public class Vertex implements Comparable<Vertex> {

	/**
	 * Vertex name
	 */
	private String id = "";
	
	/**
	 * Cost, if any
	 */
	private Number cost;
	
	/**
	 * Collection of attributes defining vertex
	 */
	private HashMap<String, String> attributes;

	public Vertex() {}
	
	public Vertex(String name) {
		this.id = name;
	}
	
	/**
	 * Get the collection of attributes defining the vertex
	 * 
	 * @return A hashmap where keys are the attribute type and
	 * 			values define the attribute value.
	 */
	public HashMap<String, String> getAttributes() {
		
		if (attributes == null) {
			attributes = new HashMap<>();
		}
		
		return attributes;
	}
	
	/**
	 * Add an attribute to the vertex specified by Strings for 
	 * the key and value.
	 * 
	 * @param key The attribute identifier
	 * @param value The value for the attribute
	 */
	public void addAttribute(String key, String value) {

		if (attributes == null) {
			attributes = new HashMap<>();
		}
		
		attributes.put(key, value);
	}
	
	/**
	 * Get the attribute defined by the given key.
	 * 
	 * @param key The attribute to find specified by a String
	 * @return A string specifing the attribute, null if the key
	 * 			does not exist or no attributes have been defined.
	 */
	public String getAttribute(String key) {
		if (attributes == null) {
			return null;
		}
		return attributes.get(key);
	}
	
	public void setId(String id) { this.id = id; }

	public String getId() { return this.id; }

	public String toString() {
		return this.id;
	}
	
	public void setCost(Number number) {
		this.cost = number;
	}
	
	/**
	 * Returns the cost estimate of the vertex. If there has been
	 * no previous value of the cost set a new <code>Double</code>
	 * is initialized with the value 0 and returned.
	 * 
	 * @return The cost value set for the vertex, a new Double set 
	 *  		to 0 if no value was set before.
	 */
	public Number getCost() {
		if (cost == null) {
			cost = new Double(0);
		}
		return cost;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Vertex))
            return false;
        if (obj == this)
            return true;

		Vertex rhs = (Vertex) obj;
        // Compare using vertex id
        return this.id.equals(rhs.id);
	}

	/**
	 * This needs to be updated to handle all possible Number values
	 * or at least Integer/Double/Long, although I haven't been able
	 * to break it yet...
	 *
	 * @param o The object to compare against.
	 * @return -1 if this vertex has lower cost, 0 if equal or 1 if greater.
	 */
	@Override
	public int compareTo(Vertex o) {

		if (this.cost.doubleValue() < o.getCost().doubleValue()) {
			return -1;
		} else if (this.cost == o.getCost()) {
			return 0;
		}
		return 1;
	}
	
}
