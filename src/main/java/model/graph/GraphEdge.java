package model.graph;

import java.io.Serializable;

/**
 * Representa uma aresta direcionada (ligação) dentro da estrutura do grafo relacional.
 * Interconecta dois vértices através dos seus identificadores textuais únicos (origem e destino)
 * e carrega um objeto de metadados associado para descrever a natureza e propriedades da ligação,
 * suportando os mecanismos de serialização para persistência.
 */
public class GraphEdge implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String from;
    private final String to;
    private final EdgeMetadata metadata;

    /**
     * Constrói e inicializa uma instância estável de uma aresta direcionada.
     * Valida rigorosamente as strings dos vértices e a presença de metadados para garantir
     * a integridade das conexões lógicas do grafo.
     *
     * @param from     O identificador textual único do vértice de origem.
     * @param to       O identificador textual único do vértice de destino.
     * @param metadata O objeto contendo as propriedades e o tipo da relação ({@link EdgeMetadata}).
     * @throws IllegalArgumentException Se o vértice de origem ou de destino estiver vazio, nulo ou
     * composto apenas por espaços em branco, ou se os metadados forem nulos.
     */
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

    /**
     * Recupera o identificador do vértice de origem de onde a aresta direcionada parte.
     *
     * @return A string que identifica o nó de origem.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Recupera o identificador do vértice de destino onde a aresta direcionada termina.
     *
     * @return A string que identifica o nó de destino.
     */
    public String getTo() {
        return to;
    }

    /**
     * Recupera o contentor de informações estruturais e customizadas atrelado a esta aresta.
     *
     * @return O objeto {@link EdgeMetadata} associado.
     */
    public EdgeMetadata getMetadata() {
        return metadata;
    }

    /**
     * Método utilitário de conveniência que extrai e expõe diretamente o peso numérico
     * configurado nos metadados internos da aresta.
     *
     * @return O valor do peso da ligação como um {@code double}.
     */
    public double getWeight() {
        return metadata.getWeight();
    }
}