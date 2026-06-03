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

/**
 * Controlador JavaFX responsável pela gestão lógica e visual dos utilizadores.
 * Coordena as operações CRUD efetuadas na interface, a sincronização em tempo real
 * com uma estrutura de dados de tabela de símbolos (Symbol Table) e com o grafo de
 * conexões, além de tratar da importação/exportação de dados em lote.
 */
public class UserController {
    private UserST userST;
    private StreamingGraph streamingGraph;

    /**
     * Lista auxiliar observável que encapsula o modelo de dados dos utilizadores.
     * Utilizada como motor de vinculação (data binding) para atualizar automaticamente
     * os componentes visuais da tabela do JavaFX diante de qualquer alteração estrutural.
     */
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

    /**
     * Inicializa os subsistemas e controlos da aba de utilizadores.
     * Configura preventivamente as coleções de retaguarda, define as fábricas de células
     * das colunas da tabela, estabelece listeners de seleção para preenchimento automático
     * do formulário lateral de edição e injeta a massa de dados simulados de teste.
     */
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
     * Popula o sistema com registos de teste codificados de forma nativa.
     * Insere os utilizadores na Symbol Table, regista os seus identificadores únicos como
     * vértices no grafo relacional partilhado e atualiza o estado de visualização da tabela.
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
     * Permite à dashboard coordenadora injetar a topologia e a referência global partilhada
     * do grafo de streaming de forma centralizada.
     *
     * @param graph Instância do grafo relacional do sistema.
     */
    public void setStreamingGraph(StreamingGraph graph) {
        this.streamingGraph = graph;
    }

    /**
     * Permite à dashboard coordenadora injetar uma instância global unificada da Symbol Table.
     * Despoleta em sequência o recarregamento imediato dos registos na interface.
     *
     * @param userST A tabela de símbolos contendo os mapeamentos de utilizadores.
     */
    public void setUserST(UserST userST) {
        this.userST = userST;
        carregarDadosDoBackEnd();
    }

    /**
     * Mapeia as propriedades dos atributos do modelo {@link User} às respetivas colunas
     * da {@link TableView}, estabelecendo invólucros observáveis de cadeias de caracteres e objetos de tempo.
     */
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

    /**
     * Sincroniza o estado atual da interface gráfica recuperando todos os utilizadores
     * presentes na Symbol Table de suporte e reinjetando-os na lista observável vinculada.
     */
    private void carregarDadosDoBackEnd() {
        if (userST != null) {
            obsUsers.setAll(userST.listAll());
        }
    }

    /**
     * Trata o processamento do botão de gravação. Valida o preenchimento dos campos textuais
     * obrigatórios e bifurca a ação: se nenhum utilizador estiver selecionado na tabela, realiza
     * a criação e inserção de um novo utilizador (acrescentando-o também ao grafo); caso contrário,
     * atualiza mutavelmente os atributos do objeto selecionado na tabela de símbolos.
     */
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

    /**
     * Executa a exclusão do utilizador atualmente selecionado na interface visual.
     * Remove o registo mapeado na Symbol Table por ID e elimina o respetivo vértice e arestas
     * associadas de forma síncrona na malha topológica do grafo.
     */
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

    /**
     * Aplica uma filtragem linear multicritério cumulativa com base nos valores preenchidos na
     * barra superior (filtragem parcial por nome, código da região e limites temporais de datas de registo).
     * Os elementos aprovados sob as restrições reconstroem a grelha visual da tabela.
     */
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

    /**
     * Limpa os controlos gráficos de filtragem (campos de texto e seletores de data) e
     * restaura a perspetiva integral de dados recuperados a partir da Symbol Table de suporte.
     */
    @FXML
    public void handleLimparFiltros() {
        txtFiltroNome.clear();
        txtFiltroRegiao.clear();
        dpInicio.setValue(null);
        dpFim.setValue(null);
        carregarDadosDoBackEnd();
    }

    /**
     * Abre uma janela modal de seleção de arquivos (.txt) e executa o parsing linear das linhas.
     * Extrai os dados tokenizados por ponto e vírgula, instancia os utilizadores sob parâmetros padronizados,
     * atualiza de forma incremental a Symbol Table bem como o grafo e anexa os novos dados à tabela.
     */
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

    /**
     * Trata o mecanismo de exportação massiva em ficheiro texto estruturado CSV.
     * Varre sequencialmente os utilizadores ativos e grava em colunas separadas por ponto e vírgula
     * os atributos de nome, correio eletrónico, credencial, código de região e string de nascimento.
     */
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

    /**
     * Preenche os campos textuais e seletores de data do formulário de registo lateral com
     * as propriedades do utilizador injetado por parâmetro, permitindo a sua modificação.
     *
     * @param user Instância da entidade utilizador cujos dados serão exibidos.
     */
    private void preencherFormulario(User user) {
        txtFormNome.setText(user.getName());
        txtFormEmail.setText(user.getEmail());
        txtFormSenha.setText(user.getPassword());
        txtFormRegiao.setText(user.getRegion() != null ? user.getRegion().getCode() : "");
        dpFormNascimento.setValue(user.getBirthDate());
    }

    /**
     * Desmarca qualquer seleção ativa realizada na tabela gráfica e redefine as caixas de
     * entrada de texto e de datas do formulário para o estado vazio.
     */
    private void limparFormulario() {
        tblUsers.getSelectionModel().clearSelection();
        txtFormNome.clear();
        txtFormEmail.clear();
        txtFormSenha.clear();
        txtFormRegiao.clear();
        dpFormNascimento.setValue(null);
    }

    /**
     * Cria e retorna um instantâneo (snapshot) em formato de lista simples contendo todos
     * os utilizadores registados na memória do repositório Symbol Table.
     *
     * @return Uma lista de objetos {@link User} contendo os utilizadores do sistema.
     */
    public List<User> getUsersSnapshot() {
        if (userST == null) {
            return new ArrayList<>();
        }
        return userST.listAll();
    }

    /**
     * Reinicializa o repositório da tabela de símbolos e reconstrói as referências adjacentes
     * de vértices no grafo relacional partilhado com base em uma lista estruturada de instantâneo.
     *
     * @param users Lista de objetos {@link User} para carregamento massivo.
     */
    public void loadUsersSnapshot(List<User> users) {
        this.userST = new UserST();
        this.streamingGraph = this.streamingGraph == null ? new StreamingGraph() : this.streamingGraph;

        for (User user : users) {
            userST.insert(user);
            streamingGraph.addVertex(user.getId());
        }

        carregarDadosDoBackEnd();
        limparFormulario();
    }

    /**
     * Cria, configura e exibe de forma síncrona uma janela modal flutuante de alerta no ecrã.
     *
     * @param tipo    O nível estrutural de gravidade/tipo do alerta JavaFX.
     * @param titulo  O título a ser impresso na borda superior do componente modular.
     * @param msg     O corpo descritivo contendo a mensagem contextual de feedback.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}