
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by Tony on 2015-11-14.
 */
public class EdgeSamplingTest {

    @Test
    public void testRandom(){
        Graph<Integer, String> graph = new SparseGraph<>();
        for (int i = 1; i <= 10000; i++) {
            graph.addVertex(i);
        }

        Random random = new Random();

        for (int i = 1; i <= 99; i++) {
            for (int j = i + 1; j <= 99; j++) {
                if (random.nextDouble() < 0.5) {
                    graph.addEdge(i + "-" + j, i, j);
                }
            }
        }

        EdgeSampling sample = new EdgeSampling();
        double trans = sample.transApprox(graph, 10);
        assertTrue(0.0 < trans);
    }


    @Test
    public void testK6(){

        Graph<Integer, String> graph = new SparseGraph<>();
        for (int i = 0; i < 6; i++) {
            graph.addVertex(i);
        }

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (!(graph.containsEdge(i + "-" + j) || graph.containsEdge(j + "-" + i) || i == j)) {
                    graph.addEdge(i + "-" + j, i, j);
                }
            }
        }

        EdgeSampling sample = new EdgeSampling();
        double trans = sample.transApprox(graph, 10);
        assertTrue(1.0 == trans);
    }

    @Test
    public void testTriangle(){

        Graph<Integer, String> graph = new SparseGraph<>();
        for (int i = 0; i < 3; i++) {
            graph.addVertex(i);
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (!(graph.containsEdge(i + "-" + j) || graph.containsEdge(j + "-" + i)  || i == j)) {
                    graph.addEdge(i + "-" + j, i, j);
                }
            }
        }

        EdgeSampling sample = new EdgeSampling();
        double trans = sample.transApprox(graph, 3);
        assertTrue((1.0 == trans));
    }

    @Test
    public void testPair(){

        Graph<Integer, String> graph = new SparseGraph<>();
        graph.addVertex(1);
        graph.addVertex(2);

        graph.addEdge("1-2",1,2);

        EdgeSampling sample = new EdgeSampling();
        double trans = sample.transApprox(graph, 1);
        assertTrue(0.0 == trans);

    }

}