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

/**
 * Estrutura de dados central que implementa um grafo direcionado ponderado (Multi-Grafo)
 * adaptado ao ecossistema de streaming.
 * Mantém uma correspondência bidirecional entre identificadores textuais únicos (Strings)
 * e índices numéricos sequenciais. Utiliza a infraestrutura de algoritmos da biblioteca
 * {@code algs4} de Princeton para computar caminhos mínimos através do algoritmo de Dijkstra
 * e analisar a conectividade da rede relacional.
 */
public class StreamingGraph implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<String, Integer> vertexIndexes;
    private final List<String> vertexIds;
    private final List<GraphEdge> edges;
    private transient EdgeWeightedDigraph graph;

    /**
     * Constrói e inicializa uma instância vazia do grafo de streaming.
     * Aloca os mapas de mapeamento interno, as listas estruturais para registo de arestas e
     * instancia um dígrafo ponderado inicializado com zero vértices.
     */
    public StreamingGraph() {
        this.vertexIndexes = new HashMap<>();
        this.vertexIds = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.graph = new EdgeWeightedDigraph(0);
    }

    /**
     * Insere de forma segura um novo vértice isolado no grafo a partir do seu identificador único.
     * Caso o vértice ainda não exista na malha, ele é registado com o próximo índice disponível e
     * a estrutura matemática subjacente de Princeton é reconstruída de forma incremental.
     *
     * @param id O identificador alfanumérico único do nó a ser adicionado.
     * @throws IllegalArgumentException Se o identificador for nulo ou vazio.
     */
    public void addVertex(String id) {
        validateId(id);
        if (!vertexIndexes.containsKey(id)) {
            vertexIndexes.put(id, vertexIds.size());
            vertexIds.add(id);
            rebuildGraph();
        }
    }

    /**
     * Adiciona uma entidade {@link User} como vértice no grafo.
     * Adiciona de forma automática arestas direcionadas do tipo {@link RelationType#USER_PREFERS_GENRE}
     * ligando o utilizador a cada um dos géneros cinematográficos configurados nas suas preferências.
     *
     * @param user A instância da entidade utilizador a ser injetada.
     * @throws IllegalArgumentException Se o objeto fornecido for nulo.
     */
    public void addUser(User user) {
        requireEntity(user, "User");
        addVertex(user.getId());
        for (Genre genre : user.getGenres()) {
            addUserPrefersGenre(user, genre, 1.0);
        }
    }

    /**
     * Adiciona uma entidade {@link Content} (filme ou série) como vértice no grafo.
     * Varre incrementalmente os atributos associados para construir de forma síncrona as arestas de:
     * <ul>
     * <li>Classificação temática ({@link RelationType#CONTENT_HAS_GENRE})</li>
     * <li>Participação no elenco ({@link RelationType#ACTOR_IN})</li>
     * <li>Direção de autoria ({@link RelationType#DIRECTED_BY})</li>
     * </ul>
     *
     * @param content A instância da produção multimédia a ser mapeada.
     * @throws IllegalArgumentException Se o objeto fornecido for nulo.
     */
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

    /**
     * Adiciona uma entidade de categoria ou temática {@link Genre} como um vértice isolado na topologia.
     *
     * @param genre O objeto representativo do género.
     * @throws IllegalArgumentException Se o género for nulo.
     */
    public void addGenre(Genre genre) {
        requireEntity(genre, "Genre");
        addVertex(genre.getId());
    }

    /**
     * Estabelece uma conexão direcionada ponderada genérica entre dois vértices identificados por texto.
     * Garante de forma preemptiva a criação dos nós de origem e destino caso estes não existam e acopla
     * a nova aresta ao repositório local e ao dígrafo estrutural de Princeton.
     *
     * @param from     Identificador textual do vértice emissor (origem).
     * @param to       Identificador textual do vértice recetor (destino).
     * @param metadata Objeto contendo os qualificadores de peso, tempo e tipo de relação ({@link EdgeMetadata}).
     * @throws IllegalArgumentException Se qualquer um dos identificadores for inválido ou se os metadados forem nulos.
     */
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

    /**
     * Adiciona uma aresta do tipo {@link RelationType#USER_WATCHED} para indicar o consumo
     * de media por parte de um utilizador.
     *
     * @param user    Entidade utilizador que assistiu ao conteúdo.
     * @param content Entidade da obra visual consumida.
     * @param weight  O peso numérico (p. ex., frequência, tempo de visualização ou relevância).
     */
    public void addUserWatchedContent(User user, Content content, double weight) {
        addEdge(user.getId(), content.getId(), new EdgeMetadata(RelationType.USER_WATCHED, weight));
    }

    /**
     * Adiciona uma aresta do tipo {@link RelationType#USER_RATED} para representar a avaliação
     * crítica e a atribuição de uma nota de pontuação de um utilizador a uma obra.
     * Insere a pontuação inteira obtida no mapa interno de atributos customizados dos metadados.
     *
     * @param user    Entidade utilizadora avaliadora.
     * @param content Entidade do conteúdo que recebe a nota.
     * @param score   A nota ou pontuação inteira concedida.
     */
    public void addUserRatedContent(User user, Content content, int score) {
        Map<String, Object> data = new HashMap<>();
        data.put("score", score);
        addEdge(user.getId(), content.getId(), new EdgeMetadata(RelationType.USER_RATED, score, null, data));
    }

    /**
     * Adiciona uma aresta direcionada indicando a predileção expressa ou afinidade de um utilizador
     * por uma determinada categoria temática ({@link RelationType#USER_PREFERS_GENRE}).
     *
     * @param user   Entidade utilizador interessada.
     * @param genre  Entidade de categoria temática de destino.
     * @param weight Relevância ou peso estatístico atribuído à preferência.
     */
    public void addUserPrefersGenre(User user, Genre genre, double weight) {
        addEdge(user.getId(), genre.getId(), new EdgeMetadata(RelationType.USER_PREFERS_GENRE, weight));
    }

    /**
     * Adiciona uma ligação de atuação do tipo {@link RelationType#ACTOR_IN} estabelecendo o
     * vínculo profissional de participação de um artista no elenco de um conteúdo multimédia.
     *
     * @param actor   Entidade do ator participante.
     * @param content Entidade do conteúdo cinematográfico de destino.
     * @param weight  Peso associado à conexão.
     */
    public void addActorInContent(Actor actor, Content content, double weight) {
        addEdge(actor.getId(), content.getId(), new EdgeMetadata(RelationType.ACTOR_IN, weight));
    }

    /**
     * Adiciona uma aresta de autoria técnica de direção do tipo {@link RelationType#DIRECTED_BY}
     * mapeando que um conteúdo específico foi realizado/dirigido por um determinado diretor.
     *
     * @param content  Entidade da obra realizada.
     * @param director Entidade do artista realizador encarregado.
     * @param weight   Peso associado à ligação.
     */
    public void addDirectedBy(Content content, Director director, double weight) {
        addEdge(content.getId(), director.getId(), new EdgeMetadata(RelationType.DIRECTED_BY, weight));
    }

    /**
     * Adiciona uma ligação de indexação taxonómica de género ({@link RelationType#CONTENT_HAS_GENRE})
     * estabelecendo que uma produção pertence a um determinado nicho temático de catálogo.
     *
     * @param content Entidade da obra catalogada.
     * @param genre   Entidade do género associado.
     * @param weight  Peso associado à ligação.
     */
    public void addContentGenre(Content content, Genre genre, double weight) {
        addEdge(content.getId(), genre.getId(), new EdgeMetadata(RelationType.CONTENT_HAS_GENRE, weight));
    }

    /**
     * Remove de forma definitiva um vértice da rede a partir do seu identificador alfanumérico.
     * Limpa o ID das listas de registo, força a reconstrução sequencial completa de todos os
     * mapeamentos de índices numéricos, descarta de forma retroativa qualquer aresta incidente
     * (de entrada ou de saída) que tocava no nó excluído e atualiza o dígrafo estrutural.
     *
     * @param id Identificador textual do nó a ser extirpado da malha.
     * @throws IllegalArgumentException Se o ID fornecido for nulo ou vazio.
     */
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

    /**
     * Varre a coleção de links e remove todas as arestas direcionadas que partem do vértice
     * de origem fornecido e chegam ao vértice de destino especificado.
     * Caso alguma correspondência seja eliminada, o motor do dígrafo de Princeton é atualizado.
     *
     * @param from Identificador textual do nó de origem da ligação.
     * @param to   Identificador textual do nó de destino da ligação.
     * @return {@code true} se pelo menos uma aresta correspondente foi removida; {@code false} caso contrário.
     * @throws IllegalArgumentException Se algum dos parâmetros de identificação for vazio ou nulo.
     */
    public boolean removeEdge(String from, String to) {
        validateId(from);
        validateId(to);
        boolean removed = edges.removeIf(edge -> edge.getFrom().equals(from) && edge.getTo().equals(to));
        if (removed) {
            rebuildGraph();
        }
        return removed;
    }

    /**
     * Verifica de forma determinística a presença e existência de um determinado nó na tabela
     * de dispersão de índices do grafo através do seu identificador único.
     *
     * @param id O identificador textual do vértice sob consulta.
     * @return {@code true} se o nó existe na estrutura; {@code false} em caso contrário.
     */
    public boolean containsVertex(String id) {
        return vertexIndexes.containsKey(id);
    }

    /**
     * Fornece a cardinalidade de nós ativos presentes na rede do grafo.
     *
     * @return Quantidade total de vértices registados.
     */
    public int vertexCount() {
        return vertexIds.size();
    }

    /**
     * Fornece a quantidade acumulada de conexões e arestas direcionadas mapeadas na rede.
     *
     * @return Quantidade total de arestas registadas.
     */
    public int edgeCount() {
        return edges.size();
    }

    /**
     * Procura e compila todas as arestas de saída (outgoing) que têm como origem
     * o nó especificado.
     *
     * @param from Identificador textual do nó emissor.
     * @return Uma lista contendo as instâncias de {@link GraphEdge} que emergem do vértice.
     * @throws IllegalArgumentException Se o identificador for nulo ou composto por espaços em branco.
     */
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

    /**
     * Procura e compila todas as arestas incidentes de entrada (incoming) que têm como
     * ponto de destino final o nó fornecido por parâmetro.
     *
     * @param to Identificador textual do nó recetor.
     * @return Uma lista contendo as instâncias de {@link GraphEdge} cujo destino coincide com o informado.
     * @throws IllegalArgumentException Se o identificador for nulo ou inválido.
     */
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

    /**
     * Localiza e extrai uma perspetiva imutável contendo os blocos de metadados de todas
     * as arestas que realizam conexão direta de uma origem específica para um destino específico.
     * Suporta o comportamento de multi-grafo para ler atributos de links paralelos concorrentes.
     *
     * @param from Identificador textual do vértice de origem.
     * @param to   Identificador textual do vértice de destino.
     * @return Uma perspetiva em lista não modificável ({@link Collections#unmodifiableList}) de objetos {@link EdgeMetadata}.
     * @throws IllegalArgumentException Se qualquer um dos caminhos textuais for nulo ou inválido.
     */
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

    /**
     * Computa a sequência de menor custo de conexões direcionadas para transitar entre dois nós da rede.
     * Mapeia os caminhos textuais para os índices numéricos internos equivalentes, delega o cálculo
     * à máquina do algoritmo de Dijkstra ({@link DijkstraSP}) e traduz de volta os elos de
     * {@link DirectedEdge} de Princeton para instâncias de {@link GraphEdge} legíveis da aplicação.
     *
     * @param from Identificador alfanumérico do vértice inicial (ponto de partida).
     * @param to   Identificador alfanumérico do vértice final (alvo).
     * @return Uma lista ordenada contendo as arestas {@link GraphEdge} constitutivas do caminho mínimo.
     * Retorna uma lista vazia caso não exista qualquer conectividade entre os nós informados.
     * @throws IllegalArgumentException Se os IDs de entrada violarem as regras estruturais de validação.
     */
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

    /**
     * Avalia a conectividade topológica global da rede do grafo sob uma perspetiva fracamente conectada.
     *
     * @return {@code true} se o grafo for conexo (ou se estiver totalmente vazio); {@code false} caso contrário.
     */
    public boolean isConnected() {
        return isWeaklyConnected();
    }

    /**
     * Determina se o grafo é fracamente conexo ignorando a orientação/direcionalidade das arestas.
     * Inicia uma busca em largura (BFS - Breadth-First Search) a partir do primeiro vértice registado,
     * rastreando links adjacentes bidirecionais e avaliando se o somatório de nós visitados engloba a
     * totalidade de elementos ativos no sistema.
     *
     * @return {@code true} se todos os nós forem alcançáveis desconsiderando o sentido dos arcos;
     * {@code false} se houver componentes isolados de rede.
     */
    public boolean isWeaklyConnected() {
        if (vertexIds.isEmpty()) {
            return true;
        }

        @SuppressWarnings("SR")
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

    /**
     * Gera e retorna uma subestrutura de grafo independente preenchida de forma derivada.
     * Transpõe a totalidade dos nós originais e efetua o teste de um predicado de filtragem funcional
     * sobre cada aresta para decidir a sua inserção no novo subgrafo gerado.
     *
     * @param filter Expressão lambda ou predicado condicional ({@link Predicate}) de filtragem de arestas.
     * Se for nulo, todas as ligações originais serão espelhadas.
     * @return Uma nova instância de {@link StreamingGraph} constituindo o subgrafo filtrado.
     */
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

    /**
     * Expõe e retorna uma perspetiva protegida e não modificável contendo todas as arestas
     * registadas na malha estrutural.
     *
     * @return Um invólucro de lista imutável com as instâncias de {@link GraphEdge}.
     */
    public List<GraphEdge> edges() {
        return Collections.unmodifiableList(edges);
    }

    /**
     * Expõe e retorna o conjunto completo imutável de identificadores textuais únicos associados
     * aos vértices que compõem o grafo corrente.
     *
     * @return Um {@link Set} protegido com as chaves textuais de identificação de nós.
     */
    public Set<String> vertices() {
        return Collections.unmodifiableSet(vertexIndexes.keySet());
    }

    /**
     * Recupera e expõe diretamente o objeto nativo subjacente da biblioteca Princeton.
     *
     * @return A instância ativa do dígrafo ponderado {@link EdgeWeightedDigraph}.
     */
    public EdgeWeightedDigraph getPrincetonGraph() {
        return graph;
    }

    /**
     * Resolve e localiza o índice numérico sequencial associado a uma determinada string de ID.
     *
     * @param id O identificador textual do vértice sob busca.
     * @return O índice inteiro do nó equivalente.
     * @throws IllegalArgumentException Se o identificador não possuir mapeamento ativo ou se for inválido.
     */
    public int indexOf(String id) {
        validateId(id);
        Integer index = vertexIndexes.get(id);
        if (index == null) {
            throw new IllegalArgumentException("Vertex does not exist: " + id);
        }
        return index;
    }

    /**
     * Resolve e localiza a string original do identificador associado a um determinado índice
     * posicional numérico do grafo.
     *
     * @param index O índice posicional inteiro sob consulta.
     * @return A string do identificador mapeado na posição correspondente.
     * @throws IllegalArgumentException Se o índice estiver fora dos limites geográficos da lista.
     */
    public String idOf(int index) {
        if (index < 0 || index >= vertexIds.size()) {
            throw new IllegalArgumentException("Invalid vertex index: " + index);
        }
        return vertexIds.get(index);
    }

    /**
     * Método interno utilitário auxiliar de caminhada estrutural que tenta introduzir um nó
     * no conjunto de visitados e, em caso de sucesso (nó inédito), enfileira-o para processamento posterior.
     */
    private void visit(String vertex, Set<String> visited, ArrayDeque<String> queue) {
        if (visited.add(vertex)) {
            queue.add(vertex);
        }
    }

    /**
     * Método utilitário de validação defensiva textual de identificadores de nós.
     */
    private void validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Vertex id cannot be empty");
        }
    }

    /**
     * Método utilitário de validação defensiva de integridade de referências a objetos.
     */
    private void requireEntity(Object entity, String name) {
        if (entity == null) {
            throw new IllegalArgumentException(name + " cannot be null");
        }
    }

    /**
     * Traduz uma instância de aresta da aplicação para uma aresta nativa ponderada direcionada de Princeton.
     */
    private DirectedEdge toPrincetonEdge(GraphEdge edge) {
        return new DirectedEdge(vertexIndexes.get(edge.getFrom()), vertexIndexes.get(edge.getTo()), edge.getWeight());
    }

    /**
     * Tenta reverter e mapear um elo primitivo {@link DirectedEdge} de Princeton para a sua respetiva
     * contraparte tipificada {@link GraphEdge} presente no repositório local.
     * Caso nenhuma colisão exata por peso e caminhos seja rastreada, constrói de forma preventiva uma
     * aresta de fallback genérica do tipo USER_WATCHED.
     */
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

    /**
     * Reconstrói de forma linear a tabela de dispersão de chaves e índices a partir do estado
     * posicional atual da lista de identificadores únicos de vértices.
     */
    private void rebuildIndexes() {
        vertexIndexes.clear();
        for (int i = 0; i < vertexIds.size(); i++) {
            vertexIndexes.put(vertexIds.get(i), i);
        }
    }

    /**
     * Reinicializa o dígrafo ponderado {@link EdgeWeightedDigraph} redimensionando-o para acomodar
     * a quantidade exata de nós ativos e reinsere em lote todas as arestas armazenadas na coleção.
     */
    private void rebuildGraph() {
        graph = new EdgeWeightedDigraph(vertexIds.size());
        for (GraphEdge edge : edges) {
            graph.addEdge(toPrincetonEdge(edge));
        }
    }

    /**
     * Método de interceção customizado acionado durante os ciclos de desserialização binária.
     * Garante o processamento adequado das propriedades padrão e força a reconstrução dinâmica
     * em tempo de execução do objeto dígrafo marcado como {@code transient}.
     *
     * @param in Fluxo de entrada de dados de objetos de persistência ({@link ObjectInputStream}).
     * @throws IOException            Em caso de falhas de leitura física em disco.
     * @throws ClassNotFoundException Caso a definição da classe não seja localizada.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        rebuildGraph();
    }
}