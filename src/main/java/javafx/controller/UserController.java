package javafx.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import model.artists.Artist;
import model.users.User;
import model.utilities.Region;
import admin.DataStorageManager;
import model.graph.StreamingGraph;
import model.graph.GraphEdge;
import service.st.UserST;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserController {
    private UserST userST;
    private StreamingGraph streamingGraph;
    private List<String> historicoPesquisas;

    @FXML private TextField txtFiltroNome;
    @FXML private TextField txtFiltroRegiao;
    @FXML private DatePicker dpInicio;
    @FXML private DatePicker dpFim;

    @FXML private TableView<User> tblUsers;
    @FXML private TableColumn<User, String> colId;
    @FXML private TableColumn<User, String> colNome;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRegiao;
    @FXML private TableColumn<User, LocalDate> colRegisto;

    @FXML private TextField txtFormNome;
    @FXML private TextField txtFormEmail;
    @FXML private PasswordField txtFormSenha;
    @FXML private TextField txtFormRegiao;
    @FXML private DatePicker dpFormNascimento;

    @FXML private TableView<model.content.Content> tblContent;
    @FXML private TableColumn<model.content.Content, String> colContentId;
    @FXML private TableColumn<model.content.Content, String> colContentTitulo;
    @FXML private TableColumn<model.content.Content, Integer> colContentAno;

    @FXML private TableView<Artist> tblArtists;
    @FXML private TableColumn<Artist, String> colArtistId;
    @FXML private TableColumn<Artist, String> colArtistNome;
    @FXML private TableColumn<Artist, String> colArtistFuncao;

    @FXML private TableView<GraphEdge> tblEdges;
    @FXML private TableColumn<GraphEdge, String> colEdgeOrigem;
    @FXML private TableColumn<GraphEdge, String> colEdgeDestino;
    @FXML private TableColumn<GraphEdge, String> colEdgeTipo;
    @FXML private TableColumn<GraphEdge, Double> colEdgePeso;
    @FXML private TableColumn<GraphEdge, LocalDateTime> colEdgeData;

    @FXML
    public void initialize() {
        // Instanciar o Core das tuas estruturas
        userST = new UserST();
        streamingGraph = new StreamingGraph();
        historicoPesquisas = new ArrayList<>();

        // Configurar como as Tabelas extraem a informação dos Objetos (Mapeamento)
        configurarColunasUtilizadores();
        configurarColunasCatalogo();
        configurarColunasGrafo();

        // Carregar dados iniciais vazios na interface
        atualizarTabelaUtilizadores(userST.listAll());
        handleAtualizarGrafo();
    }

    private void configurarColunasUtilizadores() {
        colId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        colNome.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        colEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        colRegiao.setCellValueFactory(cellData -> {
            Region r = cellData.getValue().getRegion();
            return new SimpleStringProperty(r != null ? r.getCode() : "N/A");
        });
        colRegisto.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getRegistrationDate()));
    }

    private void configurarColunasCatalogo() {
        colContentId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        colContentTitulo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        colContentAno.setCellValueFactory(cellData -> {
            LocalDate dataLancamento = cellData.getValue().getReleaseDate();
            return new SimpleObjectProperty<>(dataLancamento != null ? dataLancamento.getYear() : null);
        });
    }

    private void configurarColunasArtistas() {
        colArtistId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().toString()));
        colArtistNome.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().toString()));
        colArtistFuncao.setCellValueFactory(cellData -> new SimpleStringProperty("Artista"));
    }

    private void configurarColunasGrafo() {
        colEdgeOrigem.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFrom()));
        colEdgeDestino.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTo()));

        colEdgeTipo.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getMetadata() != null ? cellData.getValue().getMetadata().getType().name() : "N/A"
        ));

        colEdgePeso.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                cellData.getValue().getMetadata() != null ? cellData.getValue().getMetadata().getWeight() : 0.0
        ));

        colEdgeData.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                cellData.getValue().getMetadata() != null ? cellData.getValue().getMetadata().getTimestamp() : null
        ));
    }

    @FXML
    public void handleSalvar() {
        try {
            String nome = txtFormNome.getText();
            String email = txtFormEmail.getText();
            String senha = txtFormSenha.getText();
            String codigoRegiao = txtFormRegiao.getText();
            LocalDate dataNasc = dpFormNascimento.getValue();

            if (nome.isEmpty() || email.isEmpty() || codigoRegiao.isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Campos Vazios", "Por favor, preencha todos os campos obrigatórios.");
                return;
            }

            // Criar a Região baseada no código digitado (ex: "PT")
            Region regiao = new Region(codigoRegiao.toUpperCase(), codigoRegiao.toUpperCase());

            // Instanciar o teu utilizador (Data de registo assume o dia de Hoje)
            User novoUser = new User(nome, "Não Especificado", email, senha, LocalDate.now(), regiao, dataNasc);

            // Guardar na Symbol Table (Dicionário) e associar ao Grafo de Streaming
            userST.insert(novoUser);
            streamingGraph.addVertex(novoUser.getId());

            // Forçar atualização do Grafo visual para o caso de haver arestas automáticas
            handleAtualizarGrafo();

            // Sincronizar Interface Gráfica
            atualizarTabelaUtilizadores(userST.listAll());
            limparFormulario();

            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Utilizador registado com sucesso!");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível registar: " + e.getMessage());
        }
    }

    @FXML
    public void handleRemover() {
        User selecionado = tblUsers.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleção Inválida", "Selecione um utilizador na tabela para remover.");
            return;
        }

        userST.remove(selecionado.getId());
        atualizarTabelaUtilizadores(userST.listAll());
        handleAtualizarGrafo();
        mostrarAlerta(Alert.AlertType.INFORMATION, "Removido", "Utilizador excluído do sistema.");
    }

    @FXML
    public void handleFiltrar() {
        String filtroNome = (txtFiltroNome.getText() != null) ? txtFiltroNome.getText().toLowerCase().trim() : "";
        String filtroRegiao = (txtFiltroRegiao.getText() != null) ? txtFiltroRegiao.getText().toUpperCase().trim() : "";
        LocalDate dataInicio = dpInicio.getValue();
        LocalDate dataFim = dpFim.getValue();

        List<User> todosUtilizadores = userST.listAll();
        List<User> filtrados = new ArrayList<>();

        for (User u : todosUtilizadores) {
            boolean bateNome = filtroNome.isEmpty() || u.getName().toLowerCase().contains(filtroNome);
            boolean bateRegiao = filtroRegiao.isEmpty() ||
                    (u.getRegion() != null && u.getRegion().getCode().toUpperCase().contains(filtroRegiao));

            boolean bateData = true;
            LocalDate dataRegisto = u.getRegistrationDate();
            if (dataRegisto != null) {
                if (dataInicio != null && dataRegisto.isBefore(dataInicio)) bateData = false;
                if (dataFim != null && dataRegisto.isAfter(dataFim)) bateData = false;
            } else if (dataInicio != null || dataFim != null) {
                bateData = false;
            }

            if (bateNome && bateRegiao && bateData) {
                filtrados.add(u);
            }
        }
        atualizarTabelaUtilizadores(filtrados);
    }

    @FXML
    public void handleLimparFiltros() {
        txtFiltroNome.clear();
        txtFiltroRegiao.clear();
        dpInicio.setValue(null);
        dpFim.setValue(null);
        atualizarTabelaUtilizadores(userST.listAll());
    }

    @FXML
    public void handleAtualizarGrafo() {
        if (streamingGraph != null) {
            List<GraphEdge> listaArestas = new ArrayList<>();
            // Itera pela coleção iterável de arestas que o teu StreamingGraph disponibiliza
            for (GraphEdge edge : streamingGraph.edges()) {
                listaArestas.add(edge);
            }
            tblEdges.setItems(FXCollections.observableArrayList(listaArestas));
        }
    }

    @FXML
    public void handleExportarDados() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exportar Base de Dados do Sistema");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ficheiros TXT", "*.txt"));
        File file = fileChooser.showSaveDialog(tblUsers.getScene().getWindow());

        if (file != null) {
            try {
                DataStorageManager.exportData(file, userST.listAll(), streamingGraph, historicoPesquisas);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Dados persistidos com sucesso!");
            } catch (IOException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro na Escrita", "Falha ao gravar ficheiro: " + e.getMessage());
            }
        }
    }

    @FXML
    public void handleImportarDados() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importar Ficheiro de Configuração");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ficheiros TXT", "*.txt"));
        File file = fileChooser.showOpenDialog(tblUsers.getScene().getWindow());

        if (file != null) {
            try {
                List<User> utilizadoresImportados = new ArrayList<>();

                // DataStorageManager limpa e reconstrói o grafo internamente através do ficheiro txt
                historicoPesquisas = DataStorageManager.importData(file, streamingGraph, utilizadoresImportados);

                // limpa a Tabela de Símbolos e repovoar com os registos lidos
                userST.clear();
                for (User u : utilizadoresImportados) {
                    userST.insert(u);
                }

                // sincroniza imediatamente todos os painéis visuais do programa
                atualizarTabelaUtilizadores(userST.listAll());
                handleAtualizarGrafo();

                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Base de dados restaurada e grafo populado!");
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro na Leitura", "Ficheiro corrompido ou inválido: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void atualizarTabelaUtilizadores(List<User> lista) {
        ObservableList<User> obsList = FXCollections.observableArrayList(lista);
        tblUsers.setItems(obsList);
    }

    private void limparFormulario() {
        txtFormNome.clear();
        txtFormEmail.clear();
        txtFormSenha.clear();
        txtFormRegiao.clear();
        dpFormNascimento.setValue(null);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);

        try {
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (Exception ignored) {
            // abre o popup padrao
        }
        alert.showAndWait();
    }
}