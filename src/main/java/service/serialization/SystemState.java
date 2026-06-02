package service.serialization;

import model.artists.Artist;
import model.graph.GraphEdge;
import model.users.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa o estado global unificado de toda a aplicação (Memento/Snapshot) para
 * efeitos de persistência total do sistema.
 * Agrupa de forma centralizada as coleções completas de utilizadores, registos de conteúdo,
 * artistas e a topologia estrutural (vértices e arestas) do grafo relacional, permitindo
 * salvar e restaurar a sessão de execução através de serialização binária.
 */
public class SystemState implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<User> users;
    private final List<ContentRecord> contents;
    private final List<Artist> artists;
    private final List<String> graphVertices;
    private final List<GraphEdge> graphEdges;

    /**
     * Constrói e inicializa um novo instantâneo do estado global do sistema.
     * Aplica cópias defensivas em todas as listas de entrada para garantir o desacoplamento
     * completo dos objetos em memória e salvaguardar a integridade do arquivo binário.
     *
     * @param users         Lista contendo todos os utilizadores ({@link User}) ativos.
     * @param contents      Lista contendo os registos simplificados de conteúdos ({@link ContentRecord}).
     * @param artists       Lista contendo os artistas ({@link Artist}) registados.
     * @param graphVertices Lista com os identificadores únicos textuais dos nós do grafo.
     * @param graphEdges    Lista contendo as arestas direcionadas ({@link GraphEdge}) que mapeiam as relações.
     */
    public SystemState(List<User> users, List<ContentRecord> contents, List<Artist> artists, List<String> graphVertices, List<GraphEdge> graphEdges) {
        this.users = new ArrayList<>(users);
        this.contents = new ArrayList<>(contents);
        this.artists = new ArrayList<>(artists);
        this.graphVertices = new ArrayList<>(graphVertices);
        this.graphEdges = new ArrayList<>(graphEdges);
    }

    /**
     * Recupera a lista de utilizadores registados no instantâneo de estado.
     * Retorna uma nova instância de lista (cópia defensiva) para evitar efeitos secundários e
     * mutações externas indesejadas na coleção de retaguarda.
     *
     * @return Uma {@link List} com os objetos {@link User}.
     */
    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    /**
     * Recupera a lista de registos de conteúdos multimédia guardados no estado global.
     * Retorna uma nova instância de lista (cópia defensiva) como mecanismo de proteção de dados.
     *
     * @return Uma {@link List} preenchida com instâncias de {@link ContentRecord}.
     */
    public List<ContentRecord> getContents() {
        return new ArrayList<>(contents);
    }

    /**
     * Recupera a lista de artistas (atores e realizadores) armazenados neste estado.
     * Retorna uma nova instância de lista (cópia defensiva).
     *
     * @return Uma {@link List} contendo os objetos {@link Artist}.
     */
    public List<Artist> getArtists() {
        return new ArrayList<>(artists);
    }

    /**
     * Recupera a listagem de identificadores únicos textuais dos vértices pertencentes ao grafo.
     * Retorna uma nova instância de lista (cópia defensiva) contendo as chaves dos nós.
     *
     * @return Uma {@link List} de strings com os identificadores dos vértices.
     */
    public List<String> getGraphVertices() {
        return new ArrayList<>(graphVertices);
    }

    /**
     * Recupera a listagem das ligações direcionadas ponderadas que conectavam os nós no
     * momento da captura do estado.
     * Retorna uma nova instância de lista (cópia defensiva).
     *
     * @return Uma {@link List} contendo os elos estruturais {@link GraphEdge}.
     */
    public List<GraphEdge> getGraphEdges() {
        return new ArrayList<>(graphEdges);
    }
}