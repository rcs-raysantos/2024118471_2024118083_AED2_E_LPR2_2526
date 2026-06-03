package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import java.io.*;

import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.GraphEdgeList;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;

import model.graph.GraphEdge;
import model.graph.StreamingGraph;
import model.graph.EdgeMetadata;
import model.graph.RelationType;
import model.users.User;
import service.st.UserST;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador JavaFX encarregado de gerir a renderização e interação visual do grafo.
 * Utiliza a biblioteca SmartGraph para mapear as interconexões entre utilizadores,
 * conteúdos, artistas e géneros cinematográficos, oferecendo suporte a filtros bidirecionais
 * e serialização de dados de rede.
 */
public class GraphController {

    @FXML private BorderPane graphContainer;

    // Injeções FXML para os Filtros Superiores
    @FXML private TextField txtPesquisaVertice;
    @FXML private ComboBox<String> cmbFiltroRelacao;

    private StreamingGraph streamingGraph;
    private UserST userST;
    private SmartGraphPanel<String, String> smartGraphView;

    /**
     * Inicializa os controlos gráficos do ecossistema de grafos. Instancia o grafo de streaming,
     * popula os critérios de filtragem de relacionamentos a partir do enumerador {@link RelationType},
     * gera a teia de dados demonstrativos e agenda a primeira renderização visual da malha.
     */
    @FXML
    public void initialize() {
        if (this.streamingGraph == null) {
            this.streamingGraph = new StreamingGraph();
        }

        // Popula o ComboBox com a opção genérica + os Enums do teu RelationType
        cmbFiltroRelacao.getItems().add("TODOS");
        for (RelationType tipo : RelationType.values()) {
            cmbFiltroRelacao.getItems().add(tipo.name());
        }
        cmbFiltroRelacao.setValue("TODOS");

        // Carrega a teia de relações fictícias para testes iniciais
        carregarGrafoInicial();

        Platform.runLater(() -> {
            if (streamingGraph.vertexCount() > 0) {
                handleDesenhar();
            }
        });
    }

    /**
     * Popula a estrutura interna em memória com uma massa inicial de vértices e arestas tipificadas.
     * Insere utilizadores, produções, artistas e géneros e estabelece as conexões lógicas de rede
     * como realização, atuação, consumo de media e avaliações de utilizador.
     */
    private void carregarGrafoInicial() {
        try {
            // Adicionar Utilizadores (IDs idênticos aos do UserController)
            streamingGraph.addVertex("joao@email.com");
            streamingGraph.addVertex("maria@email.com");
            streamingGraph.addVertex("john@email.com");

            // Adicionar Conteúdos (Filmes/Séries)
            streamingGraph.addVertex("Inception");
            streamingGraph.addVertex("Oppenheimer");
            streamingGraph.addVertex("Breaking Bad");

            // Adicionar Artistas
            streamingGraph.addVertex("Cillian Murphy");
            streamingGraph.addVertex("Christopher Nolan");
            streamingGraph.addVertex("Bryan Cranston");

            // Adicionar Géneros
            streamingGraph.addVertex("Sci-Fi");
            streamingGraph.addVertex("Drama");

            // Relações: Quem realizou o quê (DIRECTED_BY)
            streamingGraph.addEdge("Christopher Nolan", "Inception",
                    new EdgeMetadata(model.graph.RelationType.DIRECTED_BY, 1.0));
            streamingGraph.addEdge("Christopher Nolan", "Oppenheimer",
                    new EdgeMetadata(model.graph.RelationType.DIRECTED_BY, 1.0));

            // Relações: Quem atuou onde (ACTOR_IN)
            streamingGraph.addEdge("Cillian Murphy", "Inception",
                    new EdgeMetadata(model.graph.RelationType.ACTOR_IN, 1.0));
            streamingGraph.addEdge("Cillian Murphy", "Oppenheimer",
                    new EdgeMetadata(model.graph.RelationType.ACTOR_IN, 1.0));
            streamingGraph.addEdge("Bryan Cranston", "Breaking Bad",
                    new EdgeMetadata(model.graph.RelationType.ACTOR_IN, 1.0));

            // Relações: Histórico de Visualizações (USER_WATCHED)
            streamingGraph.addEdge("joao@email.com", "Inception",
                    new EdgeMetadata(model.graph.RelationType.USER_WATCHED, 1.0));
            streamingGraph.addEdge("maria@email.com", "Oppenheimer",
                    new EdgeMetadata(model.graph.RelationType.USER_WATCHED, 1.0));

            // Relações: Avaliações/Notas dos Utilizadores (USER_RATED)
            streamingGraph.addEdge("joao@email.com", "Inception",
                    new EdgeMetadata(model.graph.RelationType.USER_RATED, 5.0)); // Nota 5
            streamingGraph.addEdge("john@email.com", "Breaking Bad",
                    new EdgeMetadata(model.graph.RelationType.USER_RATED, 4.8)); // Nota 4.8

            // Relações: Classificação de Géneros (CONTENT_HAS_GENRE)
            streamingGraph.addEdge("Inception", "Sci-Fi",
                    new EdgeMetadata(model.graph.RelationType.CONTENT_HAS_GENRE, 1.0));
            streamingGraph.addEdge("Breaking Bad", "Drama",
                    new EdgeMetadata(model.graph.RelationType.CONTENT_HAS_GENRE, 1.0));

        } catch (Exception e) {
            System.out.println("Erro ao gerar conexões simuladas: " + e.getMessage());
        }
    }

    /**
     * Define a referência para a Symbol Table de utilizadores, permitindo
     * resolver IDs (emails) para nomes reais durante a renderização.
     *
     * @param userST A instância da Symbol Table de utilizadores.
     */
    public void setUserST(UserST userST) {
        this.userST = userST;
    }

    private String getDisplayName(String id) {
        if (userST != null && userST.contains(id)) {
            User u = userST.get(id);
            return u.getName() + " (" + id + ")";
        }
        return id;
    }

    /**
     * Define externamente a referência da topologia de rede do grafo de streaming.
     * Caso o contentor esteja pronto para exibição e contenha dados válidos, solicita
     * de forma assíncrona o redesenho imediato do grafo.
     *
     * @param graph Instância do grafo de streaming a ser injetada.
     */
    public void setStreamingGraph(StreamingGraph graph) {
        this.streamingGraph = graph;
        if (graphContainer != null && streamingGraph != null && streamingGraph.vertexCount() > 0) {
            Platform.runLater(this::handleDesenhar);
        }
    }

    /**
     * Despoleta o desenho ou reposicionamento completo do grafo na interface.
     * Invoca o mecanismo de desenho padrão limpando quaisquer restrições textuais ou filtros de aresta.
     */
    @FXML
    public void handleDesenhar() {
        desenharGrafoFiltrado("", "TODOS");
    }

    /**
     * Captura os critérios inseridos nos inputs de filtro superiores e dispara o motor
     * de reconstrução parcial do grafo com base nas restrições fornecidas.
     */
    @FXML
    public void handleFiltrarGrafo() {
        String textoVertice = txtPesquisaVertice.getText() == null ? "" : txtPesquisaVertice.getText().trim().toLowerCase();
        String relacaoSelecionada = cmbFiltroRelacao.getValue() == null ? "TODOS" : cmbFiltroRelacao.getValue();

        desenharGrafoFiltrado(textoVertice, relacaoSelecionada);
    }

    /**
     * Limpa os inputs textuais e seleções dos critérios de pesquisa superiores
     * e aciona o motor gráfico para restaurar a renderização integral da rede.
     */
    @FXML
    public void handleLimparFiltros() {
        txtPesquisaVertice.clear();
        cmbFiltroRelacao.setValue("TODOS");
        handleDesenhar();
    }

    /**
     * Motor centralizador de renderização e estruturação do grafo bidimensional.
     * Filtra iterativamente arestas e nós em conformidade com as restrições introduzidas,
     * constrói uma instância concreta de {@link GraphEdgeList}, injeta os elementos sob uma
     * estratégia de distribuição circular ordenada e inicializa o painel visual.
     *
     * @param filtroVertice Rótulo de texto usado para filtrar IDs de nós (origem ou destino).
     * @param filtroRelacao Nome específico do tipo de relacionamento (aresta) para filtragem exclusiva.
     */
    private void desenharGrafoFiltrado(String filtroVertice, String filtroRelacao) {
        if (streamingGraph == null || streamingGraph.vertexCount() == 0) {
            mostrarAlerta("Aviso", "O grafo está vazio! Adiciona dados primeiro nas outras abas.");
            return;
        }

        Graph<String, String> graphModel = new GraphEdgeList<>();

        // Percorre e adiciona apenas as arestas que combinam com os filtros
        for (GraphEdge edge : streamingGraph.edges()) {
            String origem = edge.getFrom();
            String destino = edge.getTo();
            String tipoAresta = edge.getMetadata().getType().name();
            
            String displayOrigem = getDisplayName(origem);
            String displayDestino = getDisplayName(destino);

            boolean matchRelacao = filtroRelacao.equals("TODOS") || tipoAresta.equalsIgnoreCase(filtroRelacao);
            boolean matchVertice = filtroVertice.isEmpty() ||
                    origem.toLowerCase().contains(filtroVertice) ||
                    destino.toLowerCase().contains(filtroVertice);

            if (matchRelacao && matchVertice) {
                // Tenta inserir os vértices de forma segura.
                // Se já existirem no modelo, o catch apanha e avança sem crashar a app.
                try {
                    graphModel.insertVertex(displayOrigem);
                } catch (Exception e) {
                    // Ignora: o vértice já tinha sido inserido antes
                }

                try {
                    graphModel.insertVertex(displayDestino);
                } catch (Exception e) {
                    // Ignora: o vértice já tinha sido inserido antes
                }

                String labelUnica = displayOrigem + " -> " + displayDestino + " (" + tipoAresta + ")";
                try {
                    graphModel.insertEdge(displayOrigem, displayDestino, labelUnica);
                } catch (Exception e) {
                    // Ignora colisões visuais duplicadas de arestas repetidas
                }
            }
        }

        // Caso o utilizador filtre um nó isolado válido que existe, impede-o de sumir
        if (!filtroVertice.isEmpty()) {
            for (String vertexId : streamingGraph.vertices()) {
                // Verifica se o ID condiz com o filtro
                if (vertexId.toLowerCase().contains(filtroVertice)) {

                    String displayId = getDisplayName(vertexId);
                    // Procura na lista de vértices inseridos se já existe algum com este ID (String)
                    boolean jaExiste = graphModel.vertices().stream()
                            .anyMatch(v -> v.element().equals(displayId));

                    if (!jaExiste) {
                        graphModel.insertVertex(displayId);
                    }
                }
            }
        }
        if (graphModel.numVertices() == 0) {
            mostrarAlerta("Busca Vazia", "Nenhuma conexão mapeada para esses critérios.");
            return;
        }

        SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
        smartGraphView = new SmartGraphPanel<>(graphModel, strategy);
        smartGraphView.setAutomaticLayout(true);

        graphContainer.setCenter(smartGraphView);
        smartGraphView.init();
    }

    /**
     * Interrompe as rotinas de layout físico automático e desvincula
     * por completo o painel do grafo do centro do container visual principal.
     */
    @FXML
    public void handleLimpar() {
        if (smartGraphView != null) {
            smartGraphView.setAutomaticLayout(false);
        }
        graphContainer.setCenter(null);
    }

    /**
     * Exibe um explorador de ficheiros para leitura de um arquivo de texto (.txt),
     * processa as linhas contendo a estrutura adjacente formatada linearmente por ponto e vírgula,
     * reconstrói o grafo e renderiza a nova topologia.
     */
    @FXML
    public void handleImportarDados() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Importar Estrutura do Grafo (.txt)");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ficheiros TXT", "*.txt"));
        File file = chooser.showOpenDialog(graphContainer.getScene().getWindow());

        if (file != null) {
            StreamingGraph novoGrafo = new StreamingGraph();
            int countArestas = 0;

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String javaLinha;
                while ((javaLinha = br.readLine()) != null) {
                    javaLinha = javaLinha.trim();
                    if (javaLinha.isEmpty()) continue;

                    String[] tokens = javaLinha.split(";");
                    if (tokens.length >= 4) {
                        String origem = tokens[0].trim();
                        String destino = tokens[1].trim();
                        String tipoStr = tokens[2].trim().toUpperCase();
                        double peso = Double.parseDouble(tokens[3].trim());

                        RelationType tipoEnum = RelationType.valueOf(tipoStr);

                        if (!novoGrafo.containsVertex(origem)) {
                            novoGrafo.addVertex(origem);
                        }
                        if (!novoGrafo.containsVertex(destino)) {
                            novoGrafo.addVertex(destino);
                        }

                        EdgeMetadata metadata = new EdgeMetadata(tipoEnum, peso);
                        novoGrafo.addEdge(origem, destino, metadata);
                        countArestas++;
                    }
                }

                this.streamingGraph = novoGrafo;
                handleDesenhar();
                mostrarAlerta("Sucesso", "Grafo carregado!\nVértices: " + streamingGraph.vertexCount() + " | Arestas: " + countArestas);

            } catch (IllegalArgumentException e) {
                mostrarAlerta("Erro de Formato", "O ficheiro contém um Tipo de Relação inválido.");
            } catch (Exception e) {
                mostrarAlerta("Erro de Importação", "Falha ao processar o ficheiro:\n" + e.getMessage());
            }
        }
    }

    /**
     * Varre as arestas presentes na estrutura e serializa de forma persistente
     * os atributos de conexão (origem, destino, tipo e peso) em um ficheiro de texto externo.
     */
    @FXML
    public void handleExportarDados() {
        if (streamingGraph == null || streamingGraph.edgeCount() == 0) {
            mostrarAlerta("Aviso", "Não existem ligações (arestas) no grafo para exportar.");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exportar Estrutura do Grafo (.txt)");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ficheiros TXT", "*.txt"));
        chooser.setInitialFileName("rede_conexoes.txt");
        File file = chooser.showSaveDialog(graphContainer.getScene().getWindow());

        if (file != null) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                for (GraphEdge edge : streamingGraph.edges()) {
                    String origem = edge.getFrom();
                    String destino = edge.getTo();
                    String tipoEnumStr = edge.getMetadata().getType().name();
                    double peso = edge.getMetadata().getWeight();

                    bw.write(origem + ";" + destino + ";" + tipoEnumStr + ";" + peso);
                    bw.newLine();
                }

                mostrarAlerta("Sucesso", "Estrutura do grafo guardada com sucesso em: " + file.getName());
            } catch (IOException e) {
                mostrarAlerta("Erro de Exportação", "Não foi possível gravar o ficheiro:\n" + e.getMessage());
            }
        }
    }

    /**
     * Configura e gera uma janela modal de informação síncrona para feedback ao utilizador.
     *
     * @param titulo   O cabeçalho impresso na janela.
     * @param mensagem O conteúdo principal descritivo.
     */
    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    /**
     * Cria e retorna uma cópia de segurança instantânea (snapshot) de todas as arestas do grafo corrente.
     *
     * @return Uma nova lista contendo as instâncias de {@link GraphEdge}.
     */
    public List<GraphEdge> getGraphEdgesSnapshot() {
        if (streamingGraph == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(streamingGraph.edges());
    }

    /**
     * Cria e retorna uma cópia de segurança instantânea (snapshot) de todos os IDs de vértices do grafo.
     *
     * @return Uma nova lista de strings contendo os identificadores de vértices.
     */
    public List<String> getGraphVerticesSnapshot() {
        if (streamingGraph == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(streamingGraph.vertices());
    }

    /**
     * Limpa o grafo atual e reconstrói integralmente a malha estrutural de dados a partir das
     * listas de instantâneos (snapshots) passadas por parâmetro, disparando a rotina de re-layout visual.
     *
     * @param vertices Lista contendo os identificadores de vértices para inserção.
     * @param edges    Lista contendo os objetos de aresta estruturada para interconexão.
     */
    public void loadGraphSnapshot(List<String> vertices, List<GraphEdge> edges) {
        StreamingGraph novoGrafo = new StreamingGraph();

        for (String vertex : vertices) {
            novoGrafo.addVertex(vertex);
        }

        for (GraphEdge edge : edges) {
            novoGrafo.addEdge(edge.getFrom(), edge.getTo(), edge.getMetadata());
        }

        this.streamingGraph = novoGrafo;
        if (graphContainer != null) {
            graphContainer.setCenter(null);
            desenharQuandoPainelEstiverPronto();
        }
    }

    /**
     * Recupera o objeto de grafo de streaming atual em uso.
     *
     * @return A instância do modelo {@link StreamingGraph}.
     */
    public StreamingGraph getStreamingGraphSnapshot() {
        return streamingGraph;
    }

    /**
     * Agenda assintoticamente a renderização do grafo. Garante que as dimensões do container na árvore
     * JavaFX já foram calculadas e estão maiores que zero ou anexa escutadores de propriedade para atrasar
     * a montagem gráfica até que o painel esteja redimensionado de forma correta.
     */
    private void desenharQuandoPainelEstiverPronto() {
        Platform.runLater(() -> {
            if (graphContainer.getWidth() > 0 && graphContainer.getHeight() > 0) {
                handleDesenhar();
            } else {
                graphContainer.widthProperty().addListener((obs, oldValue, newValue) -> tentarDesenharGrafoImportado());
                graphContainer.heightProperty().addListener((obs, oldValue, newValue) -> tentarDesenharGrafoImportado());
            }
        });
    }

    /**
     * Método interno utilitário que atua como barreira de validação condicional nas propriedades de escuta,
     * efetuando a renderização apenas se o container apresentar dimensões físicas válidas e estiver limpo.
     */
    private void tentarDesenharGrafoImportado() {
        if (streamingGraph != null
                && streamingGraph.vertexCount() > 0
                && graphContainer.getWidth() > 0
                && graphContainer.getHeight() > 0
                && graphContainer.getCenter() == null) {
            handleDesenhar();
        }
    }
}