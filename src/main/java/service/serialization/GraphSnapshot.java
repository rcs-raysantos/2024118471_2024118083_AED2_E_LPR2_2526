package service.serialization;

import model.graph.GraphEdge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GraphSnapshot implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<String> vertices;
    private final List<GraphEdge> edges;

    public GraphSnapshot(List<String> vertices, List<GraphEdge> edges) {
        this.vertices = new ArrayList<>(vertices);
        this.edges = new ArrayList<>(edges);
    }

    public List<String> getVertices() {
        return new ArrayList<>(vertices);
    }

    public List<GraphEdge> getEdges() {
        return new ArrayList<>(edges);
    }
}
