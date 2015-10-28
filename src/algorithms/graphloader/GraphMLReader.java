package algorithms.graphloader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultElement;
import org.dom4j.tree.DefaultText;

import core.components.Edge;
import core.components.Pair;
import core.components.Vertex;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class GraphMLReader implements GraphLoader<Vertex, Edge> {

	/**
	 * HashMap that uses a String for Keys and HashMaps with HashMaps for Values. 
	 * The keys specify the id of the attribute. The values use the attribute name
	 * as the key and maps a pairing, the type and default value (if exists, null otherwise).
	 *   
	 */
	private HashMap<String, HashMap<String, Pair<String, String>>> vertexAttr;

	/**
	 * HashMap that uses a String for Keys and HashMaps with HashMaps for Values. 
	 * The keys specify the id of the attribute. The values use the attribute name
	 * as the key and maps a pairing, the type and default value (if exists, null otherwise).
	 *   
	 */
	private HashMap<String, HashMap<String, Pair<String, String>>> edgeAttr;

	/**
	 * Used to keep record of ids to vertex instances
	 */
	private HashMap<String, Vertex> vertexMap;

	private boolean isDirected;

	/*
	// TODO: Verify XML version, don't blindly parse
	 */

	public GraphMLReader() {
		vertexAttr = new HashMap<>();
		edgeAttr = new HashMap<>();
		vertexMap = new HashMap<>();
		isDirected = false;
	}

	@Override
	public Graph<Vertex, Edge> loadGraph(Graph<Vertex, Edge> graph) {

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		FileNameExtensionFilter graphFilter = 
				new FileNameExtensionFilter("GraphML FIle (*.graphml)", "graphml");
		// add filters
		fileChooser.addChoosableFileFilter(graphFilter);
		fileChooser.setFileFilter(graphFilter);

		int choice = fileChooser.showOpenDialog(fileChooser);
		
		if (choice == JFileChooser.APPROVE_OPTION) {

			File tgf = fileChooser.getSelectedFile();
			URL url = null;

			try {
				url = tgf.toURI().toURL();
			} catch (MalformedURLException e) {
				// These are fine as they get redirected to application
				// console to print to the user
				e.printStackTrace();
			}

			if (url == null) {
				System.out.println("Could not find URL to file");
				return null;
			}

			SAXReader reader = new SAXReader();
			try {

				Document document = reader.read(url);
				Element rootElement = document.getRootElement();

				// Root element iterator gets attribute info for nodes, edges
				Iterator<?> i = rootElement.elementIterator("key");

				// Load default attributes defined for the graph
				while (i.hasNext()) {
					Element attributeSet = (Element) i.next();
					parseAttributeSet(attributeSet);
				}

				i = rootElement.elementIterator("graph");
				Element graphAttributes = (Element) i.next();
				// Iterate over set of graph attributes, look if edges are directed
				for (Iterator<?> itr = graphAttributes.attributeIterator(); itr.hasNext();) {
					Attribute attribute = (Attribute) itr.next(); 
					if (attribute.getName().equals("edgedefault")) {
						String directed = attribute.getValue();
						if (directed.equals("directed")) {
							isDirected = true;
						} else {
							isDirected = false;
						}
					}
				}

				// Now iterate over nodes and edges
				List<?> nodeSet = graphAttributes.elements("node");
				loadNodes(nodeSet, graph);

				List<?> edgeSet = graphAttributes.elements("edge");
				loadEdges(edgeSet, graph);
				
				// Clean-up
				vertexAttr.clear();
				edgeAttr.clear();
				vertexMap.clear();
							
			} catch (DocumentException | GraphLoadingException e) {
				// Invalid option, tell the user and prompt again.
				JOptionPane.showMessageDialog(null,
						"Must select a valid GraphML Format (.graphml) file.",
						"Error",
						JOptionPane.ERROR_MESSAGE);
				loadGraph(graph);
			}

		} else if (choice != JOptionPane.CANCEL_OPTION) {
			try {
				throw new GraphLoadingException("File must be selected to load graph");
			} catch (GraphLoadingException e) {	/* Do nothing */ }
			
		}

		return graph;
	}

	/**
	 * Iterates over the specified set of attributes. Is currently used for parsing
	 * edge and vertex attribute information.
	 * 
	 * @param attributeSet A collection of elements that define the attributes of the graph
	 */
	private void parseAttributeSet(Element attributeSet) {

		for (Iterator<?> itr = attributeSet.attributeIterator(); itr.hasNext();) {

			// First two attributes specify key and value for vertices or edges,
			// have to iterate after
			Attribute attribute = (Attribute) itr.next(); 
			String id = attribute.getText();
			attribute = (Attribute) itr.next();
			String forElement = attribute.getText();
			attribute = (Attribute) itr.next();
			String attributeName = attribute.getText();
			attribute = (Attribute) itr.next();
			String attributeType = attribute.getText();

			HashMap<String, Pair<String, String>> nameType = new HashMap<>();
			Pair<String, String> typeValue = new Pair<>(attributeType, null);
			nameType.put(attributeName, typeValue);

			if (forElement.equals("node")) {
				vertexAttr.put(id, nameType);
			} else if (forElement.equals("edge")) {
				edgeAttr.put(id, nameType);
			}

			if (attributeSet.hasContent()) {

				for (Iterator<?> contItr = attributeSet.content().iterator(); contItr.hasNext();) {

					Object c = contItr.next();
					// Look for the element that specifies the default value
					if (c instanceof DefaultText)
						continue;

					DefaultElement content = (DefaultElement) c;

					Pair<String, String> pairing = new Pair<>(attributeType, content.getText());

					// Store the default value
					if (forElement.equals("node")) {
						vertexAttr.get(id).put(attributeName, pairing);
					} else if (forElement.equals("edge")) {
						edgeAttr.get(id).put(attributeName, pairing);
					}	
				}
			}
		}
	}

	private void loadNodes(List<?> nodeList, Graph<Vertex, Edge> graph) {

		for (Object obj : nodeList) {
			Vertex vertex = new Vertex();
			Element element = (Element) obj;
			for (Iterator<?> itr = element.attributeIterator(); itr.hasNext();) {
				Attribute attr = (Attribute) itr.next();
				String type = attr.getName();
				String value = attr.getText();
				
				if (type.equals("id")) {
					vertex.setId(value);
					vertexMap.put(value, vertex);
					continue;
				}
				
				vertex.addAttribute(type, value);
									
			}

			// If the node has data, assign it to the attributes. Otherwise, check
			// if there are any default values and assign the attribute(s) accordingly
				if (element.hasContent()) {
				for (Iterator<?> itr = element.elementIterator(); itr.hasNext();) {

					Element data = (Element) itr.next();
					String key = data.attribute(0).getText();
					String value = data.getText();

					// Works under assumption only one item in the bucket, need to
					// think this through further if multiple attributes allowed
					String attr = vertexAttr.get(key).keySet().iterator().next();

					vertex.addAttribute(attr, value);
				}
			} else {
				if (!vertexAttr.isEmpty()) {
					// Otherwise add the default values from the attribute list into the
					// vertex set of attributes
					for (HashMap<String, Pair<String, String>> values : vertexAttr.values()) {
						for (String type : values.keySet()) {
							String defaultValue = values.get(type).getRight();
							if (defaultValue != null) {
								vertex.addAttribute(type, defaultValue);
							}
						}
					}

				}
			}

			graph.addVertex(vertex);
		}

	}

	private void loadEdges(List<?> edgeList, Graph<Vertex, Edge> graph) 
			throws GraphLoadingException {

		for (Object obj : edgeList) {
			Edge edge = new Edge();
			Element element = (Element) obj;

			// Iterate over attributes specified in <edge> tag
			for (Iterator<?> itr = element.attributeIterator(); itr.hasNext();) {
				Attribute attr = (Attribute) itr.next();
				String type = attr.getName();
				String value = attr.getText();

				if (type.equals("id")) {
					edge.setId(value);
					continue;
				} else if (type.equals("source")) {
					Vertex source = vertexMap.get(value);

					// Next attribute should be target, iterate and validate
					attr = (Attribute) itr.next();
					type = attr.getName();
					value = attr.getText();
					if (!type.equals("target")) {
						throw new GraphLoadingException(
								"Error parsing edge data. Edge target expected to follow source.");
					}

					Vertex target = vertexMap.get(value);
					
					if (isDirected) {
						graph.addEdge(edge, source, target, EdgeType.DIRECTED);
					} else {
						graph.addEdge(edge, source, target, EdgeType.UNDIRECTED);
					}
					continue;
				}
				edge.addAttribute(type, value);
			}

			// If the edge has additional data iterate over it and store in the
			// edges map.
			if (element.hasContent()) {
				for (Iterator<?> itr = element.elementIterator(); itr.hasNext();) {
					Element data = (Element) itr.next();
					// Attribute key
					String key = data.attribute(0).getText();
					// Value for the key
					String value = data.getText();

					// Get the name of attribute (ie weight)
					String attr = edgeAttr.get(key).keySet().iterator().next();

					if (attr.equals("weight")) {
						String type = edgeAttr.get(key).get(attr).getLeft();
						Number weight = getNumberValue(type, Double.valueOf(value));
						edge.setWeight(weight);
						continue;
					}

					edge.addAttribute(attr, value);

				}
			// Otherwise load the default edge attributes (if any) into the
			// attribute list of the edge being constructed.
			} else {
			
				if (!edgeAttr.isEmpty()) {
					// Otherwise add the default values from the attribute list into the
					// vertex set of attributes
					for (HashMap<String, Pair<String, String>> values : edgeAttr.values()) {
						for (String type : values.keySet()) {
							String defaultValue = values.get(type).getRight();
							if (defaultValue != null) {
								edge.addAttribute(type, defaultValue);
							}
						}
					}
				}

			}
		}
		
	}

	/**
	 *  Instance where graph type isn't specified, signaled by int,
	 *  see the GraphLoader Interface to see allowed ints
	 *  
	 *  @param graphType An integer indicating the type of graph to create
	 *  		when loading the GraphML file
	 *  
	 *  @return The JUNG graph after loading from the GraphML file.
	 */	
	@Override
	public Graph<Vertex, Edge> loadGraph(int graphType) {
		Graph<Vertex, Edge> graph = getGraph(graphType); 
		return loadGraph(graph);
	}
	
	/**
	 *  Instance where graph type isn't specified, loads a SparseGraph
	 *  
	 *  
	 *  @return The JUNG graph after loading from the GraphML file.
	 */	
	@Override
	public Graph<Vertex, Edge> loadGraph() {
		Graph<Vertex, Edge> graph = getGraph(0); 
		return loadGraph(graph);
	}

	/**
	 * Return new object of the number type specified
	 * 
	 * @param type String identifying the number type
	 * @param value Default value to instantiate to
	 * @return A new instance of that number class instantiated to 0
	 */
	private Number getNumberValue(String type, Number value) {

		switch(type) {
			case "double":
				return value.doubleValue();
			case "float":
				return value.floatValue();
			case "long":
				return value.longValue();
			case "integer":
				return value.intValue();
		}
		// This would be really bad, should never have a null value.. maybe add
		// an exception here.
		return null;
	}
}
