package service.serialization;

import model.graph.GraphEdge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa um instantâneo (snapshot) estático e isolado da estrutura de um grafo.
 * Esta classe atua como um objeto de transferência de dados (DTO) dedicado a capturar
 * e armazenar de forma conjunta as listas de vértices e arestas ativas no sistema,
 * facilitando os processos de serialização e persistência binária da malha relacional.
 */
public class GraphSnapshot implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<String> vertices;
    private final List<GraphEdge> edges;

    /**
     * Constrói e inicializa um novo instantâneo estrutural do grafo.
     * Aplica cópias defensivas das listas fornecidas por parâmetro para garantir o
     * desacoplamento das referências originais e salvaguardar a integridade interna do snapshot.
     *
     * @param vertices A lista contendo os identificadores textuais únicos de todos os vértices.
     * @param edges    A lista contendo todas as instâncias de arestas ({@link GraphEdge}) do grafo.
     */
    public GraphSnapshot(List<String> vertices, List<GraphEdge> edges) {
        this.vertices = new ArrayList<>(vertices);
        this.edges = new ArrayList<>(edges);
    }

    /**
     * Recupera a lista de identificadores textuais únicos dos vértices que compõem o instantâneo.
     * Retorna uma nova instância de lista (cópia defensiva) para impedir modificações externas
     * na estrutura armazenada.
     *
     * @return Uma {@link List} de strings contendo os identificadores dos nós.
     */
    public List<String> getVertices() {
        return new ArrayList<>(vertices);
    }

    /**
     * Recupera a lista de arestas direcionadas que compõem o instantâneo.
     * Retorna uma nova instância de lista (cópia defensiva) para mitigar efeitos colaterais
     * indesejados sobre a coleção de retaguarda.
     *
     * @return Uma {@link List} preenchida com os objetos {@link GraphEdge} registados.
     */
    public List<GraphEdge> getEdges() {
        return new ArrayList<>(edges);
    }
}