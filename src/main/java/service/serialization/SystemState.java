package service.serialization;

import model.artists.Artist;
import model.graph.GraphEdge;
import model.users.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SystemState implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<User> users;
    private final List<ContentRecord> contents;
    private final List<Artist> artists;
    private final List<String> graphVertices;
    private final List<GraphEdge> graphEdges;

    public SystemState(List<User> users, List<ContentRecord> contents, List<Artist> artists, List<String> graphVertices, List<GraphEdge> graphEdges) {
        this.users = new ArrayList<>(users);
        this.contents = new ArrayList<>(contents);
        this.artists = new ArrayList<>(artists);
        this.graphVertices = new ArrayList<>(graphVertices);
        this.graphEdges = new ArrayList<>(graphEdges);
    }

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    public List<ContentRecord> getContents() {
        return new ArrayList<>(contents);
    }

    public List<Artist> getArtists() {
        return new ArrayList<>(artists);
    }

    public List<String> getGraphVertices() {
        return new ArrayList<>(graphVertices);
    }

    public List<GraphEdge> getGraphEdges() {
        return new ArrayList<>(graphEdges);
    }
}
