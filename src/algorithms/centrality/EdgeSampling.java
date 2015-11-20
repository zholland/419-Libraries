import edu.uci.ics.jung.graph.Graph;

import java.util.ArrayList;
import java.util.Random;
import edu.uci.ics.jung.graph.util.Pair;

/**
 * Created by Tony on 2015-11-13.
 */
public class EdgeSampling<V extends Comparable<V>,E> {

    /**
     * approximates transitivity via edge sampling
     * @param g graph
     * @param m number of samples to take
     * @return
     */
    public double transApprox(Graph<V, E> g, int m){
        double transitivity =0.0;
        ArrayList<E> edges = new ArrayList<>(g.getEdges());
        ArrayList<E> sample = new ArrayList<>();

        //collect M sample vertices
        Random rnd = new Random();
        while(sample.size() != m){
            int choose = rnd.nextInt(edges.size());

            E edge = edges.get(choose);
            if(!sample.contains(edge)){
                sample.add(edge);
            }
        }

        //calculate transitivity of each edge
        for (E edge: sample){
            transitivity += Transitivity(g, edge);
        }

        return (transitivity/(m));
    }

    /**
     * computes centrality of an edge
     * @param edge
     * @return centrality
     */
    private double Transitivity(Graph<V,E> g,E edge) {
        Integer Embeddedness = Embeddedness(g, edge);
        Integer triplets = Triplets(g, edge);

        if (triplets != 0) {
            return ((double) Embeddedness) / ((double) triplets);
        }else {
            return 0.0;
        }
    }

    /**
     * computes the edge embeddedness
     * @param g graph
     * @param edge edge for calculating embeddedness
     * @return
     */
    private Integer Embeddedness(Graph<V,E> g,E edge){
        Integer Embeddedness = 0;

        Pair<V> ends = g.getEndpoints(edge);
        ArrayList<V> N1 = new ArrayList<>(g.getNeighbors(ends.getFirst()));
        ArrayList<V> N2 = new ArrayList<>(g.getNeighbors(ends.getSecond()));

        for(V e: N1){
            if(N2.contains(e)){
                Embeddedness++;
            }
        }

        return Embeddedness;

    }

    /**
     * computes the number of triplets an edge is involved in
     * using (deg(v)+deg(u)-2)/2 as the formula
     * @param g graph
     * @param edge edge to inspect
     * @return
     */
    private Integer Triplets(Graph<V,E> g, E edge){
        Pair<V> ends = g.getEndpoints(edge);
        V v1 = ends.getFirst();
        V v2 = ends.getSecond();

        return (g.getNeighborCount(v1)+g.getNeighborCount(v2)-2)/2;
    }
}