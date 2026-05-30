package controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import model.users.User;
import model.utilities.Region;
import model.graph.StreamingGraph;
import service.st.UserST;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserController {
    private UserST userST;
    private StreamingGraph streamingGraph;

    // Lista auxiliar que o JavaFX usa para reagir e mostrar os dados na tabela
    private ObservableList<User> obsUsers;

    // Painel de Filtros (Top)
    @FXML private TextField txtFiltroNome;
    @FXML private TextField txtFiltroRegiao;
    @FXML private DatePicker dpInicio;
    @FXML private DatePicker dpFim;

    // Tabela e Colunas (Center)
    @FXML private TableView<User> tblUsers;
    @FXML private TableColumn<User, String> colId;
    @FXML private TableColumn<User, String> colNome;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRegiao;
    @FXML private TableColumn<User, LocalDate> colRegisto;

    // Formulário de Registo (Right)
    @FXML private TextField txtFormNome;
    @FXML private TextField txtFormEmail;
    @FXML private PasswordField txtFormSenha;
    @FXML private TextField txtFormRegiao;
    @FXML private DatePicker dpFormNascimento;

    @FXML
    public void initialize() {
        // Inicializar por segurança as estruturas para evitar NullPointerException
        this.userST = new UserST();
        this.streamingGraph = new StreamingGraph();
        this.obsUsers = FXCollections.observableArrayList();

        // Configura as regras de extração de dados para cada coluna da tabela
        configurarColunas();

        // Associa a lista observável à tabela visual
        tblUsers.setItems(obsUsers);

        // Ouve cliques de seleção na tabela para preencher o formulário lateral (Edição)
        tblUsers.getSelectionModel().selectedItemProperty().addListener((obs, antigo, selecionado) -> {
            if (selecionado != null) {
                preencherFormulario(selecionado);
            }
        });

        // CARREGA OS UTILIZADORES CHUMBADOS NO CÓDIGO PARA TESTES
        carregarDadosIniciais();
    }

    /**
     * Adiciona utilizadores de teste diretamente no sistema
     */
    private void carregarDadosIniciais() {
        try {
            User u1 = new User("João Silva", "M", "joao@email.com", "12345", LocalDate.now().minusDays(5), new Region("PT", "Portugal"), LocalDate.of(1995, 5, 20));
            User u2 = new User("Maria Sousa", "F", "maria@email.com", "abcde", LocalDate.now().minusDays(2), new Region("BR", "Brasil"), LocalDate.of(1998, 11, 12));
            User u3 = new User("John Doe", "M", "john@email.com", "qwerty", LocalDate.now(), new Region("US", "Estados Unidos"), LocalDate.of(1990, 1, 1));

            // Insere na Symbol Table
            userST.insert(u1);
            userST.insert(u2);
            userST.insert(u3);

            // Sincroniza e insere no Grafo
            if (streamingGraph != null) {
                streamingGraph.addVertex(u1.getId());
                streamingGraph.addVertex(u2.getId());
                streamingGraph.addVertex(u3.getId());
            }

            // Atualiza a tabela visual
            carregarDadosDoBackEnd();
        } catch (Exception e) {
            System.out.println("Erro ao carregar dados simulados: " + e.getMessage());
        }
    }

    /**
     * Permite o Dashboard injetar a estrutura global do Grafo partilhada
     */
    public void setStreamingGraph(StreamingGraph graph) {
        this.streamingGraph = graph;
    }

    /**
     * Permite o Dashboard injetar a Symbol Table partilhada
     */
    public void setUserST(UserST userST) {
        this.userST = userST;
        carregarDadosDoBackEnd();
    }

    private void configurarColunas() {
        colId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        colNome.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        colEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));

        colRegiao.setCellValueFactory(cellData -> {
            Region r = cellData.getValue().getRegion();
            return new SimpleStringProperty(r != null ? r.getCode() : "N/A");
        });

        colRegisto.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getRegistrationDate()));
    }

    private void carregarDadosDoBackEnd() {
        if (userST != null) {
            obsUsers.setAll(userST.listAll());
        }
    }

    @FXML
    public void handleSalvar() {
        if (userST == null) {
            this.userST = new UserST();
        }

        String nome = txtFormNome.getText();
        String email = txtFormEmail.getText();
        String senha = txtFormSenha.getText();
        String codigoRegiao = txtFormRegiao.getText();
        LocalDate dataNasc = dpFormNascimento.getValue();

        if (nome == null || nome.isEmpty() || email == null || email.isEmpty() || codigoRegiao == null || codigoRegiao.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Obrigatórios", "Por favor, preencha Nome, Email e Região.");
            return;
        }

        try {
            User selecionado = tblUsers.getSelectionModel().getSelectedItem();
            Region regiao = new Region(codigoRegiao.toUpperCase(), codigoRegiao.toUpperCase());

            if (selecionado == null) {
                // Caso: NOVO UTILIZADOR
                User novoUser = new User(nome, "Não Especificado", email, senha, LocalDate.now(), regiao, dataNasc);

                userST.insert(novoUser);
                if (streamingGraph != null) {
                    streamingGraph.addVertex(novoUser.getId());
                }
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Utilizador registado com sucesso!");
            } else {
                // Caso: EDITAR UTILIZADOR EXISTENTE
                selecionado.setName(nome);
                selecionado.setEmail(email);
                selecionado.setPassword(senha);
                selecionado.setRegion(regiao);
                selecionado.setBirthDate(dataNasc);

                userST.insert(selecionado);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Dados do utilizador atualizados!");
            }

            carregarDadosDoBackEnd();
            limparFormulario();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Falha na operação: " + e.getMessage());
        }
    }

    @FXML
    public void handleRemover() {
        User selecionado = tblUsers.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleção Necessária", "Por favor, selecione um utilizador na tabela para remover.");
            return;
        }

        if (userST != null) {
            userST.remove(selecionado.getId());
        }

        if (streamingGraph != null && streamingGraph.containsVertex(selecionado.getId())) {
            streamingGraph.removeVertex(selecionado.getId());
        }

        carregarDadosDoBackEnd();
        limparFormulario();
        mostrarAlerta(Alert.AlertType.INFORMATION, "Removido", "Utilizador eliminado com sucesso.");
    }

    @FXML
    public void handleFiltrar() {
        if (userST == null) return;

        String filtroNome = txtFiltroNome.getText() == null ? "" : txtFiltroNome.getText().toLowerCase().trim();
        String filtroRegiao = txtFiltroRegiao.getText() == null ? "" : txtFiltroRegiao.getText().toUpperCase().trim();
        LocalDate de = dpInicio.getValue();
        LocalDate ate = dpFim.getValue();

        List<User> filtrados = new ArrayList<>();

        for (User u : userST.listAll()) {
            boolean matchesNome = filtroNome.isEmpty() || u.getName().toLowerCase().contains(filtroNome);
            boolean matchesRegiao = filtroRegiao.isEmpty() || (u.getRegion() != null && u.getRegion().getCode().toUpperCase().contains(filtroRegiao));

            boolean matchesData = true;
            LocalDate registo = u.getRegistrationDate();
            if (registo != null) {
                if (de != null && registo.isBefore(de)) matchesData = false;
                if (ate != null && registo.isAfter(ate)) matchesData = false;
            } else if (de != null || ate != null) {
                matchesData = false;
            }

            if (matchesNome && matchesRegiao && matchesData) {
                filtrados.add(u);
            }
        }

        obsUsers.setAll(filtrados);
    }

    @FXML
    public void handleLimparFiltros() {
        txtFiltroNome.clear();
        txtFiltroRegiao.clear();
        dpInicio.setValue(null);
        dpFim.setValue(null);
        carregarDadosDoBackEnd();
    }

    @FXML
    public void handleImportarDados() {
        if (userST == null) this.userST = new UserST();

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Importar Utilizadores");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ficheiros TXT", "*.txt"));
        File file = chooser.showOpenDialog(tblUsers.getScene().getWindow());

        if (file != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String linha;
                int contador = 0;

                while ((linha = br.readLine()) != null) {
                    linha = linha.trim();
                    if (linha.isEmpty()) continue;

                    String[] tokens = linha.split(";");
                    if (tokens.length >= 5) {
                        String nome = tokens[0].trim();
                        String email = tokens[1].trim();
                        String senha = tokens[2].trim();
                        String codigoRegiao = tokens[3].trim().toUpperCase();
                        LocalDate dataNasc = LocalDate.parse(tokens[4].trim());

                        Region regiao = new Region(codigoRegiao, codigoRegiao);
                        User u = new User(nome, "Não Especificado", email, senha, LocalDate.now(), regiao, dataNasc);

                        userST.insert(u);
                        if (streamingGraph != null) {
                            streamingGraph.addVertex(u.getId());
                        }
                        contador++;
                    }
                }

                carregarDadosDoBackEnd();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Importação concluída! Carregados " + contador + " utilizadores.");
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro de Importação", "Não foi possível carregar o ficheiro:\n" + e.getMessage());
            }
        }
    }

    @FXML
    public void handleExportarDados() {
        if (userST == null || userST.listAll().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Não existem utilizadores na tabela para exportar.");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exportar Ficheiro de Utilizadores");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ficheiros TXT", "*.txt"));
        chooser.setInitialFileName("utilizadores_exportados.txt");
        File file = chooser.showSaveDialog(tblUsers.getScene().getWindow());

        if (file != null) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                for (User u : userST.listAll()) {
                    String codigoRegiao = u.getRegion() != null ? u.getRegion().getCode() : "GLOBAL";
                    String dataNascStr = u.getBirthDate() != null ? u.getBirthDate().toString() : LocalDate.now().toString();

                    bw.write(u.getName() + ";" +
                            u.getEmail() + ";" +
                            u.getPassword() + ";" +
                            codigoRegiao + ";" +
                            dataNascStr);
                    bw.newLine();
                }

                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Base de dados guardada em: " + file.getName());
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro de Exportação", "Não foi possível gravar o ficheiro:\n" + e.getMessage());
            }
        }
    }

    private void preencherFormulario(User user) {
        txtFormNome.setText(user.getName());
        txtFormEmail.setText(user.getEmail());
        txtFormSenha.setText(user.getPassword());
        txtFormRegiao.setText(user.getRegion() != null ? user.getRegion().getCode() : "");
        dpFormNascimento.setValue(user.getBirthDate());
    }

    private void limparFormulario() {
        tblUsers.getSelectionModel().clearSelection();
        txtFormNome.clear();
        txtFormEmail.clear();
        txtFormSenha.clear();
        txtFormRegiao.clear();
        dpFormNascimento.setValue(null);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}