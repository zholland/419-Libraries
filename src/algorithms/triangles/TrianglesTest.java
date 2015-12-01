package algorithms.triangles;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test class for Triangles.
 *
 * @author Zach Holland
 */
public class TrianglesTest {
    @Test
    public void count_10vertexGraphWithDefaultAlgorithm_10triangles() {
        assertEquals(10, Triangles.count(create10vertexGraphWith10Triangles()));
    }

    @Test
    public void count_0vertexGraphWithDefaultAlgorithm_0triangles() {
        Graph<Integer, String> graph = new SparseGraph<>();
        assertEquals(0, Triangles.count(graph));
    }

    @Test
    public void count_10vertexGraphWithDefaultAlgorithm_0triangles() {
        assertEquals(0, Triangles.count(create10vertexGraph()));
    }

    @Test
    public void count_10vertexGraphWithNodeIterator_10triangles() {
        Triangles.GraphTriangles<String> graphTriangles = Triangles.count(create10vertexGraphWith10Triangles(), Triangles.CountAlgorithm.NODE_ITERATOR);

        assertEquals(10, graphTriangles.getTotalTriangles());
        assertEquals(2, graphTriangles.getNumberOfTrianglesForEdge("1-2"));
    }

    @Test
    public void count_0vertexGraphWithNodeIterator_0triangles() {
        Graph<Integer, String> graph = new SparseGraph<>();
        Triangles.GraphTriangles<String> graphTriangles = Triangles.count(graph, Triangles.CountAlgorithm.NODE_ITERATOR);

        assertEquals(0, graphTriangles.getTotalTriangles());
        assertEquals(0, graphTriangles.getNumberOfTrianglesForEdge("1-2"));
    }

    @Test
    public void count_10vertexGraphWithNodeIterator_0triangles() {
        Triangles.GraphTriangles<String> graphTriangles = Triangles.count(create10vertexGraph(), Triangles.CountAlgorithm.NODE_ITERATOR);

        assertEquals(0, graphTriangles.getTotalTriangles());
        assertEquals(0, graphTriangles.getNumberOfTrianglesForEdge("1-2"));
    }

    @Test
    public void count_10vertexGraphWithFastForward_10triangles() {
        Triangles.GraphTriangles<String> graphTriangles = Triangles.count(create10vertexGraphWith10Triangles(), Triangles.CountAlgorithm.FAST_FORWARD);


        assertEquals(10, graphTriangles.getTotalTriangles());
        assertEquals(2, graphTriangles.getNumberOfTrianglesForEdge("1-2"));
    }

    @Test
    public void count_0vertexGraphWithFastForward_0triangles() {
        Graph<Integer, String> graph = new SparseGraph<>();
        Triangles.GraphTriangles<String> graphTriangles = Triangles.count(graph, Triangles.CountAlgorithm.FAST_FORWARD);

        assertEquals(0, graphTriangles.getTotalTriangles());
        assertEquals(0, graphTriangles.getNumberOfTrianglesForEdge("1-2"));
    }

    @Test
    public void count_10vertexGraphWithFastForward_0triangles() {
        Triangles.GraphTriangles<String> graphTriangles = Triangles.count(create10vertexGraph(), Triangles.CountAlgorithm.FAST_FORWARD);

        assertEquals(0, graphTriangles.getTotalTriangles());
        assertEquals(0, graphTriangles.getNumberOfTrianglesForEdge("1-2"));
    }

    @Test
    public void count_50vertexCompleteGraphGraphWithFastForward_50triangles() {
        Triangles.GraphTriangles<String> graphTriangles = Triangles.count(create50VertexCompleteGraph(), Triangles.CountAlgorithm.FAST_FORWARD);

        assertEquals(19600, graphTriangles.getTotalTriangles());
        assertEquals(48, graphTriangles.getNumberOfTrianglesForEdge("1-2"));
    }

    @Test
    public void count_50vertexCompleteGraphGraphWithNodeIterator_50triangles() {
        Triangles.GraphTriangles<String> graphTriangles = Triangles.count(create50VertexCompleteGraph(), Triangles.CountAlgorithm.NODE_ITERATOR);

        assertEquals(19600, graphTriangles.getTotalTriangles());
        assertEquals(48, graphTriangles.getNumberOfTrianglesForEdge("1-2"));
    }

    private Graph<Integer, String> create50VertexCompleteGraph() {
        Graph<Integer, String> graph = new SparseGraph<>();

        for (int i = 1; i <= 50; i++) {
            graph.addVertex(i);
        }

        for (int i = 1; i <= 50; i++) {
            for (int j = i + 1; j <= 50; j++) {
                graph.addEdge(i + "-" + j, i, j);
            }
        }

        return graph;
    }

    private Graph<Integer, String> create10vertexGraph() {
        Graph<Integer, String> graph = new SparseGraph<>();

        for (int i = 1; i <= 10; i++) {
            graph.addVertex(i);
        }

        graph.addEdge("1-2", 1, 2);
        graph.addEdge("2-3", 2, 3);
        graph.addEdge("3-4", 3, 4);
        graph.addEdge("4-5", 4, 5);
        graph.addEdge("5-6", 5, 6);
        graph.addEdge("6-7", 6, 7);
        graph.addEdge("7-8", 7, 8);
        graph.addEdge("8-9", 8, 9);
        graph.addEdge("9-10", 9, 10);
        graph.addEdge("10-1", 10, 1);

        return graph;
    }

    private Graph<Integer, String> create10vertexGraphWith10Triangles() {
        Graph<Integer, String> graph = create10vertexGraph();

        graph.addEdge("1-3", 1, 3);
        graph.addEdge("2-4", 2, 4);
        graph.addEdge("3-5", 3, 5);
        graph.addEdge("4-6", 4, 6);
        graph.addEdge("5-7", 5, 7);
        graph.addEdge("6-8", 6, 8);
        graph.addEdge("7-9", 7, 9);
        graph.addEdge("8-10", 8, 10);
        graph.addEdge("9-1", 9, 1);
        graph.addEdge("10-2", 10, 2);

        return graph;
    }
}
