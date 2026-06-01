package model.graph;

import edu.princeton.cs.algs4.DijkstraSP;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import model.artists.Actor;
import model.artists.Director;
import model.content.Content;
import model.content.Genre;
import model.users.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class StreamingGraph implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<String, Integer> vertexIndexes;
    private final List<String> vertexIds;
    private final List<GraphEdge> edges;
    private transient EdgeWeightedDigraph graph;

    public StreamingGraph() {
        this.vertexIndexes = new HashMap<>();
        this.vertexIds = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.graph = new EdgeWeightedDigraph(0);
    }

    public void addVertex(String id) {
        validateId(id);
        if (!vertexIndexes.containsKey(id)) {
            vertexIndexes.put(id, vertexIds.size());
            vertexIds.add(id);
            rebuildGraph();
        }
    }

    public void addUser(User user) {
        requireEntity(user, "User");
        addVertex(user.getId());
        for (Genre genre : user.getGenres()) {
            addUserPrefersGenre(user, genre, 1.0);
        }
    }

    public void addContent(Content content) {
        requireEntity(content, "Content");
        addVertex(content.getId());
        if (content.getGenres() != null) {
            for (Genre genre : content.getGenres()) {
                addContentGenre(content, genre, 1.0);
            }
        }
        if (content.getActors() != null) {
            for (Actor actor : content.getActors()) {
                addActorInContent(actor, content, 1.0);
            }
        }
        if (content.getDirector() != null) {
            addDirectedBy(content, content.getDirector(), 1.0);
        }
    }

    public void addGenre(Genre genre) {
        requireEntity(genre, "Genre");
        addVertex(genre.getId());
    }

    public void addEdge(String from, String to, EdgeMetadata metadata) {
        validateId(from);
        validateId(to);
        if (metadata == null) {
            throw new IllegalArgumentException("Edge metadata cannot be null");
        }
        addVertex(from);
        addVertex(to);
        GraphEdge edge = new GraphEdge(from, to, metadata);
        edges.add(edge);
        graph.addEdge(toPrincetonEdge(edge));
    }

    public void addUserWatchedContent(User user, Content content, double weight) {
        addEdge(user.getId(), content.getId(), new EdgeMetadata(RelationType.USER_WATCHED, weight));
    }

    public void addUserRatedContent(User user, Content content, int score) {
        Map<String, Object> data = new HashMap<>();
        data.put("score", score);
        addEdge(user.getId(), content.getId(), new EdgeMetadata(RelationType.USER_RATED, score, null, data));
    }

    public void addUserPrefersGenre(User user, Genre genre, double weight) {
        addEdge(user.getId(), genre.getId(), new EdgeMetadata(RelationType.USER_PREFERS_GENRE, weight));
    }

    public void addActorInContent(Actor actor, Content content, double weight) {
        addEdge(actor.getId(), content.getId(), new EdgeMetadata(RelationType.ACTOR_IN, weight));
    }

    public void addDirectedBy(Content content, Director director, double weight) {
        addEdge(content.getId(), director.getId(), new EdgeMetadata(RelationType.DIRECTED_BY, weight));
    }

    public void addContentGenre(Content content, Genre genre, double weight) {
        addEdge(content.getId(), genre.getId(), new EdgeMetadata(RelationType.CONTENT_HAS_GENRE, weight));
    }

    public void removeVertex(String id) {
        validateId(id);
        if (!vertexIndexes.containsKey(id)) {
            return;
        }
        vertexIds.remove(id);
        rebuildIndexes();
        edges.removeIf(edge -> edge.getFrom().equals(id) || edge.getTo().equals(id));
        rebuildGraph();
    }

    public boolean removeEdge(String from, String to) {
        validateId(from);
        validateId(to);
        boolean removed = edges.removeIf(edge -> edge.getFrom().equals(from) && edge.getTo().equals(to));
        if (removed) {
            rebuildGraph();
        }
        return removed;
    }

    public boolean containsVertex(String id) {
        return vertexIndexes.containsKey(id);
    }

    public int vertexCount() {
        return vertexIds.size();
    }

    public int edgeCount() {
        return edges.size();
    }

    public List<GraphEdge> getOutgoingEdges(String from) {
        validateId(from);
        List<GraphEdge> result = new ArrayList<>();
        for (GraphEdge edge : edges) {
            if (edge.getFrom().equals(from)) {
                result.add(edge);
            }
        }
        return result;
    }

    public List<GraphEdge> getIncomingEdges(String to) {
        validateId(to);
        List<GraphEdge> result = new ArrayList<>();
        for (GraphEdge edge : edges) {
            if (edge.getTo().equals(to)) {
                result.add(edge);
            }
        }
        return result;
    }

    public List<EdgeMetadata> getEdgesMeta(String from, String to) {
        validateId(from);
        validateId(to);
        List<EdgeMetadata> result = new ArrayList<>();
        for (GraphEdge edge : edges) {
            if (edge.getFrom().equals(from) && edge.getTo().equals(to)) {
                result.add(edge.getMetadata());
            }
        }
        return Collections.unmodifiableList(result);
    }

    public List<GraphEdge> shortestPath(String from, String to) {
        validateId(from);
        validateId(to);
        if (!vertexIndexes.containsKey(from) || !vertexIndexes.containsKey(to)) {
            return Collections.emptyList();
        }

        int source = vertexIndexes.get(from);
        int target = vertexIndexes.get(to);
        DijkstraSP dijkstra = new DijkstraSP(graph, source);
        if (!dijkstra.hasPathTo(target)) {
            return Collections.emptyList();
        }

        List<GraphEdge> path = new ArrayList<>();
        Iterable<DirectedEdge> princetonPath = dijkstra.pathTo(target);
        if (princetonPath != null) {
            for (DirectedEdge edge : princetonPath) {
                path.add(toGraphEdge(edge));
            }
        }
        return path;
    }

    public boolean isConnected() {
        return isWeaklyConnected();
    }

    public boolean isWeaklyConnected() {
        if (vertexIds.isEmpty()) {
            return true;
        }

        Set<String> visited = new HashSet<>();
        ArrayDeque<String> queue = new ArrayDeque<>();
        String first = vertexIds.get(0);
        visited.add(first);
        queue.add(first);

        while (!queue.isEmpty()) {
            String current = queue.remove();
            for (GraphEdge edge : getOutgoingEdges(current)) {
                visit(edge.getTo(), visited, queue);
            }
            for (GraphEdge edge : getIncomingEdges(current)) {
                visit(edge.getFrom(), visited, queue);
            }
        }

        return visited.size() == vertexIds.size();
    }

    public StreamingGraph extractSubgraph(Predicate<GraphEdge> filter) {
        StreamingGraph subgraph = new StreamingGraph();
        for (String vertex : vertexIds) {
            subgraph.addVertex(vertex);
        }
        for (GraphEdge edge : edges) {
            if (filter == null || filter.test(edge)) {
                subgraph.addEdge(edge.getFrom(), edge.getTo(), edge.getMetadata());
            }
        }
        return subgraph;
    }

    public List<GraphEdge> edges() {
        return Collections.unmodifiableList(edges);
    }

    public Set<String> vertices() {
        return Collections.unmodifiableSet(vertexIndexes.keySet());
    }

    public EdgeWeightedDigraph getPrincetonGraph() {
        return graph;
    }

    public int indexOf(String id) {
        validateId(id);
        Integer index = vertexIndexes.get(id);
        if (index == null) {
            throw new IllegalArgumentException("Vertex does not exist: " + id);
        }
        return index;
    }

    public String idOf(int index) {
        if (index < 0 || index >= vertexIds.size()) {
            throw new IllegalArgumentException("Invalid vertex index: " + index);
        }
        return vertexIds.get(index);
    }

    private void visit(String vertex, Set<String> visited, ArrayDeque<String> queue) {
        if (visited.add(vertex)) {
            queue.add(vertex);
        }
    }

    private void validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Vertex id cannot be empty");
        }
    }

    private void requireEntity(Object entity, String name) {
        if (entity == null) {
            throw new IllegalArgumentException(name + " cannot be null");
        }
    }

    private DirectedEdge toPrincetonEdge(GraphEdge edge) {
        return new DirectedEdge(vertexIndexes.get(edge.getFrom()), vertexIndexes.get(edge.getTo()), edge.getWeight());
    }

    private GraphEdge toGraphEdge(DirectedEdge directedEdge) {
        String from = vertexIds.get(directedEdge.from());
        String to = vertexIds.get(directedEdge.to());
        for (GraphEdge edge : edges) {
            if (edge.getFrom().equals(from)
                    && edge.getTo().equals(to)
                    && Double.compare(edge.getWeight(), directedEdge.weight()) == 0) {
                return edge;
            }
        }
        return new GraphEdge(from, to, new EdgeMetadata(RelationType.USER_WATCHED, directedEdge.weight()));
    }

    private void rebuildIndexes() {
        vertexIndexes.clear();
        for (int i = 0; i < vertexIds.size(); i++) {
            vertexIndexes.put(vertexIds.get(i), i);
        }
    }

    private void rebuildGraph() {
        graph = new EdgeWeightedDigraph(vertexIds.size());
        for (GraphEdge edge : edges) {
            graph.addEdge(toPrincetonEdge(edge));
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        rebuildGraph();
    }
}
