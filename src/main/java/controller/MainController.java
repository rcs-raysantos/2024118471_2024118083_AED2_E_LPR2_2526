package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;
import model.artists.Artist;
import model.users.User;
import model.graph.StreamingGraph;
import service.serialization.BinaryPersistence;
import service.serialization.ContentRecord;
import service.serialization.GraphSnapshot;
import service.serialization.SystemState;

import java.io.File;
import java.util.List;

/**
 * Controlador principal (Master Controller) da interface JavaFX.
 * Atua como o orquestrador central da aplicação, coordenando a persistência polimórfica
 * (texto estruturado e binário) e delegando as operações específicas aos sub-controladores
 * injetados de cada aba (Utilizadores, Conteúdos, Artistas e Grafos).
 */
public class MainController {
    private static final String FORMATO_TEXTO = "Texto (.txt)";
    private static final String FORMATO_BINARIO = "Binario (.bin)";

    @FXML private TabPane mainTabs;
    @FXML private ComboBox<String> cmbFormatoPersistencia;
    @FXML private UserController userViewController;
    @FXML private ContentController contentViewController;
    @FXML private ArtistController artistViewController;
    @FXML private GraphController graphViewController;
    @FXML private FollowersController followersViewController; // New controller for followers tab
    @FXML private UserPreferencesController userPreferencesViewController; // New tab

    private final BinaryPersistence binaryPersistence = new BinaryPersistence();

    /**
     * Inicializa os controlos globais da barra de ferramentas principal.
     * Configura as opções do seletor de formato de persistência e define o formato
     * de texto como a opção padrão de inicialização.
     */
    @FXML
    public void initialize() {
        cmbFormatoPersistencia.getItems().addAll(FORMATO_TEXTO, FORMATO_BINARIO);
        cmbFormatoPersistencia.setValue(FORMATO_TEXTO);

        // Sincroniza o Grafo entre os controladores assim que o programa inicia.
        // Garantimos que ambos os controladores partilham a mesma INSTÂNCIA do grafo para que as alterações sejam imediatas.
        StreamingGraph liveGraph = graphViewController.getStreamingGraphSnapshot();
        userViewController.setStreamingGraph(liveGraph);
        
        // Sincroniza a UserST com o GraphController para exibir nomes no grafo
        graphViewController.setUserST(userViewController.getUserST());

        if (followersViewController != null) {
            followersViewController.setStreamingGraph(liveGraph);
            followersViewController.setUserST(userViewController.getUserST());
        }

        if (userPreferencesViewController != null) {
            userPreferencesViewController.setDependencies(liveGraph, userViewController.getUserST(), contentViewController.getContentRecordsSnapshot());
        }

        // Adiciona um listener para atualizar as abas dependentes (Followers e Graphs) quando selecionadas
        mainTabs.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null) {
                String tabText = newTab.getText();
                if (tabText.equals("Followers") && followersViewController != null) {
                    followersViewController.refreshFollowersList();
                } else if (tabText.equals("Preferências & Recs") && userPreferencesViewController != null) {
                    userPreferencesViewController.updateContentList(contentViewController.getContentRecordsSnapshot());
                    userPreferencesViewController.updateUserList();
                    userPreferencesViewController.refreshData();
                } else if (tabText.equals("Graphs") && graphViewController != null) {
                    // Força o redesenho do grafo para refletir remoções ou adições de utilizadores
                    graphViewController.handleDesenhar();
                }
            }
        });
    }

    /**
     * Trata o evento de exportação de dados baseado no formato selecionado no ComboBox.
     * Desvia o fluxo para a persistência binária localizada ou aciona os métodos nativos
     * de gravação em texto da aba que se encontra atualmente em primeiro plano.
     */
    @FXML
    public void handleExportarSelecionado() {
        if (FORMATO_BINARIO.equals(cmbFormatoPersistencia.getValue())) {
            exportarBinarioDaAbaAtual();
        } else {
            exportarTextoDaAbaAtual();
        }
    }

    /**
     * Trata o evento de importação de dados baseado no formato selecionado no ComboBox.
     * Desvia o fluxo para a leitura e desserialização binária ou delega a leitura de
     * ficheiros de texto estruturados para o sub-controlador ativo.
     */
    @FXML
    public void handleImportarSelecionado() {
        if (FORMATO_BINARIO.equals(cmbFormatoPersistencia.getValue())) {
            importarBinarioDaAbaAtual();
        } else {
            importarTextoDaAbaAtual();
        }
    }

    /**
     * Captura o índice da aba ativa, abre um diálogo para salvar o arquivo binário e,
     * através de reflexão lógica simples por switch-case, extrai o instantâneo (snapshot)
     * apropriado dos sub-controladores para serialização em disco.
     */
    private void exportarBinarioDaAbaAtual() {
        int selectedIndex = mainTabs.getSelectionModel().getSelectedIndex();
        File file = escolherFicheiroBinarioParaGuardar(nomeBinarioDaAba(selectedIndex));
        if (file == null) {
            return;
        }

        try {
            switch (selectedIndex) {
                case 0:
                    binaryPersistence.saveObject(userViewController.getUsersSnapshot(), file);
                    break;
                case 1:
                    binaryPersistence.saveObject(contentViewController.getContentRecordsSnapshot(), file);
                    break;
                case 2:
                    binaryPersistence.saveObject(artistViewController.getArtistsSnapshot(), file);
                    break;
                case 3:
                    binaryPersistence.saveObject(new GraphSnapshot(
                            graphViewController.getGraphVerticesSnapshot(),
                            graphViewController.getGraphEdgesSnapshot()
                    ), file);
                    break;
                default:
                    mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione uma aba para exportar.");
                    return;
            }

            mostrarAlerta(Alert.AlertType.INFORMATION, "Exportacao Binaria", "Dados guardados em " + file.getName());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Nao foi possivel exportar o ficheiro binario:\n" + e.getMessage());
        }
    }

    /**
     * Solicita a abertura de um ficheiro binário serializado, faz a leitura genérica do objeto
     * e realiza o cast explícito seguro para a estrutura de dados esperada pela aba ativa,
     * atualizando também as dependências cruzadas entre o Grafo e o controlador de Utilizadores.
     */
    @SuppressWarnings("unchecked")
    private void importarBinarioDaAbaAtual() {
        int selectedIndex = mainTabs.getSelectionModel().getSelectedIndex();
        File file = escolherFicheiroBinarioParaAbrir(nomeBinarioDaAba(selectedIndex));
        if (file == null) {
            return;
        }

        try {
            Object object = binaryPersistence.loadObject(file);

            switch (selectedIndex) {
                case 0:
                    userViewController.loadUsersSnapshot((List<User>) object);
                    // Re-sincroniza a ST de utilizadores com as abas dependentes após o carregamento
                    if (followersViewController != null) {
                        followersViewController.setUserST(userViewController.getUserST());
                    }
                    graphViewController.setUserST(userViewController.getUserST());
                    break;
                case 1:
                    contentViewController.loadContentRecordsSnapshot((List<ContentRecord>) object);
                    break;
                case 2:
                    artistViewController.loadArtistsSnapshot((List<Artist>) object);
                    break;
                case 3:
                    GraphSnapshot graphSnapshot = (GraphSnapshot) object;
                    graphViewController.loadGraphSnapshot(graphSnapshot.getVertices(), graphSnapshot.getEdges());
                    StreamingGraph importedGraph = graphViewController.getStreamingGraphSnapshot();
                    userViewController.setStreamingGraph(importedGraph);
                    if (followersViewController != null) {
                        followersViewController.setStreamingGraph(importedGraph);
                        followersViewController.setUserST(userViewController.getUserST());
                    }
                    graphViewController.setUserST(userViewController.getUserST());
                    break;
                default:
                    mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione uma aba para importar.");
                    return;
            }

            mostrarAlerta(Alert.AlertType.INFORMATION, "Importacao Binaria", "Dados carregados de " + file.getName());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Nao foi possivel importar o ficheiro binario:\n" + e.getMessage());
        }
    }

    /**
     * Redireciona o fluxo de exportação em modo de texto plano delimitado para o
     * sub-controlador associado à aba atualmente visível na interface do utilizador.
     */
    private void exportarTextoDaAbaAtual() {
        int selectedIndex = mainTabs.getSelectionModel().getSelectedIndex();
        switch (selectedIndex) {
            case 0:
                userViewController.handleExportarDados();
                break;
            case 1:
                contentViewController.handleExportarDados();
                break;
            case 2:
                artistViewController.handleExportarDados();
                break;
            case 3:
                graphViewController.handleExportarDados();
                break;
            default:
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione uma aba para exportar.");
        }
    }

    /**
     * Redireciona o fluxo de importação e parsing de arquivos de texto legíveis para o
     * sub-controlador correspondente à aba em foco.
     */
    private void importarTextoDaAbaAtual() {
        int selectedIndex = mainTabs.getSelectionModel().getSelectedIndex();
        switch (selectedIndex) {
            case 0:
                userViewController.handleImportarDados();
                break;
            case 1:
                contentViewController.handleImportarDados();
                break;
            case 2:
                artistViewController.handleImportarDados();
                break;
            case 3:
                graphViewController.handleImportarDados();
                break;
            default:
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selecione uma aba para importar.");
        }
    }

    /**
     * Configura e exibe um seletor de arquivos de salvamento modal mapeado com a extensão
     * nativa de arquivos binários (.bin).
     *
     * @param initialFileName Sugestão de nome padrão atribuído ao arquivo de saída.
     * @return O objeto {@link File} com o caminho de destino determinado, ou null se cancelado.
     */
    private File escolherFicheiroBinarioParaGuardar(String initialFileName) {
        FileChooser chooser = criarFileChooserBinario(initialFileName);
        return chooser.showSaveDialog(mainTabs.getScene().getWindow());
    }

    /**
     * Configura e abre um seletor de arquivos modal focado na varredura e abertura
     * de arquivos binários compatíveis.
     *
     * @param initialFileName O nome de arquivo sugerido para pré-carregamento visual na busca.
     * @return O ponteiro do arquivo {@link File} selecionado, ou null se a operação for abortada.
     */
    private File escolherFicheiroBinarioParaAbrir(String initialFileName) {
        FileChooser chooser = criarFileChooserBinario(initialFileName);
        return chooser.showOpenDialog(mainTabs.getScene().getWindow());
    }

    /**
     * Constrói uma instância parametrizada de {@link FileChooser}. Filtra o escopo de visualização
     * para arquivos binários e tenta redefinir o diretório de inicialização para a pasta local "data",
     * caso ela exista.
     *
     * @param initialFileName Nome inicial que será exibido por defeito no seletor.
     * @return Uma instância de {@link FileChooser} devidamente configurada.
     */
    private FileChooser criarFileChooserBinario(String initialFileName) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ficheiros Binarios (*.bin)", "*.bin"));
        chooser.setInitialFileName(initialFileName);

        File dataDir = new File("data");
        if (dataDir.exists() && dataDir.isDirectory()) {
            chooser.setInitialDirectory(dataDir);
        }

        return chooser;
    }

    /**
     * Retorna a string do nome do arquivo binário padrão de testes baseado no índice
     * da aba que se encontra selecionada.
     *
     * @param selectedIndex O índice numérico da aba ativa no TabPane.
     * @return String contendo o nome fixado do arquivo .bin equivalente.
     */
    private String nomeBinarioDaAba(int selectedIndex) {
        switch (selectedIndex) {
            case 0:
                return "users_test.bin";
            case 1:
                return "contents_test.bin";
            case 2:
                return "artists_test.bin";
            case 3:
                return "graphs_test.bin";
            default:
                return "data.bin";
        }
    }

    /**
     * Constrói e exibe de forma bloqueante (síncrona) um pop-up/alerta na interface gráfica.
     *
     * @param tipo     O tipo ou nível de severidade (Erro, Aviso, Informação) da janela modal.
     * @param titulo   O texto de cabeçalho da janela do alerta.
     * @param mensagem O corpo principal da mensagem de texto informativa ou de erro.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}