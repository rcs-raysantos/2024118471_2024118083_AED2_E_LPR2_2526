package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;
import model.artists.Artist;
import model.users.User;
import service.serialization.BinaryPersistence;
import service.serialization.ContentRecord;
import service.serialization.GraphSnapshot;
import service.serialization.SystemState;

import java.io.File;
import java.util.List;

public class MainController {
    private static final String FORMATO_TEXTO = "Texto (.txt)";
    private static final String FORMATO_BINARIO = "Binario (.bin)";

    @FXML private TabPane mainTabs;
    @FXML private ComboBox<String> cmbFormatoPersistencia;
    @FXML private UserController userViewController;
    @FXML private ContentController contentViewController;
    @FXML private ArtistController artistViewController;
    @FXML private GraphController graphViewController;

    private final BinaryPersistence binaryPersistence = new BinaryPersistence();

    @FXML
    public void initialize() {
        cmbFormatoPersistencia.getItems().addAll(FORMATO_TEXTO, FORMATO_BINARIO);
        cmbFormatoPersistencia.setValue(FORMATO_TEXTO);
    }

    @FXML
    public void handleExportarSelecionado() {
        if (FORMATO_BINARIO.equals(cmbFormatoPersistencia.getValue())) {
            exportarBinarioDaAbaAtual();
        } else {
            exportarTextoDaAbaAtual();
        }
    }

    @FXML
    public void handleImportarSelecionado() {
        if (FORMATO_BINARIO.equals(cmbFormatoPersistencia.getValue())) {
            importarBinarioDaAbaAtual();
        } else {
            importarTextoDaAbaAtual();
        }
    }

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
                    userViewController.setStreamingGraph(graphViewController.getStreamingGraphSnapshot());
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

    private File escolherFicheiroBinarioParaGuardar(String initialFileName) {
        FileChooser chooser = criarFileChooserBinario(initialFileName);
        return chooser.showSaveDialog(mainTabs.getScene().getWindow());
    }

    private File escolherFicheiroBinarioParaAbrir(String initialFileName) {
        FileChooser chooser = criarFileChooserBinario(initialFileName);
        return chooser.showOpenDialog(mainTabs.getScene().getWindow());
    }

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

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
