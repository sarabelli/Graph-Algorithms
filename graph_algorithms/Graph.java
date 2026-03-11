package grafoV2;

import java.util.ArrayList;

public class Graph {
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;
    private boolean directed;
    private boolean weighted;

    public Graph(boolean directed, boolean weighted) {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        this.directed = directed;
        this.weighted = weighted;
    }

    public boolean isDirected() { return directed; }
    public boolean isWeighted() { return weighted; }
    public void setDirected(boolean directed) { this.directed = directed; }
    public void setWeighted(boolean weighted) { this.weighted = weighted; }

    public void addNode(Node node) { nodes.add(node); }
    public ArrayList<Node> getNodes() { return nodes; }
    public ArrayList<Edge> getEdges() { return edges; }

    public void addEdge(Node from, Node to, int weight) {
        if (!directed) {
            // Undirected graph: single edge without arrow,
            // only if it doesn't already exist (call is already filtered by updateGraph)
            boolean exists = edges.stream().anyMatch(e ->
                (e.getFrom() == from && e.getTo() == to) ||
                (e.getFrom() == to   && e.getTo() == from));
            if (!exists) {
                edges.add(new Edge(from, to, weight, 0, false, weighted));
            }
            return;
        }

        // Directed graph: arrows, curve if reverse edge exists
        long same    = edges.stream().filter(e -> e.getFrom() == from && e.getTo() == to).count();
        long reverse = edges.stream().filter(e -> e.getFrom() == to   && e.getTo() == from).count();
        int offset = (reverse > 0) ? 60 : (int)(same) * 40;
        edges.add(new Edge(from, to, weight, offset, true, weighted));
    }
}
