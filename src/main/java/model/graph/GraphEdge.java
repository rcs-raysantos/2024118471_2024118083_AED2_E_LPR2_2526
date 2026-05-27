package model.graph;

public class GraphEdge {

    private final String from;
    private final String to;
    private final EdgeMetadata metadata;

    public GraphEdge(String from, String to, EdgeMetadata metadata) {
        if (from == null || from.trim().isEmpty()) {
            throw new IllegalArgumentException("Origin vertex cannot be empty");
        }
        if (to == null || to.trim().isEmpty()) {
            throw new IllegalArgumentException("Destination vertex cannot be empty");
        }
        if (metadata == null) {
            throw new IllegalArgumentException("Edge metadata cannot be null");
        }
        this.from = from;
        this.to = to;
        this.metadata = metadata;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public EdgeMetadata getMetadata() {
        return metadata;
    }

    public double getWeight() {
        return metadata.getWeight();
    }
}
