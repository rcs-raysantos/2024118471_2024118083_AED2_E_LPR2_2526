package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import service.serialization.BinaryPersistence;
import service.serialization.SystemState;

public class MainController {
    @FXML private UserController userViewController;
    @FXML private ContentController contentViewController;
    @FXML private ArtistController artistViewController;
    @FXML private GraphController graphViewController;

    private final BinaryPersistence binaryPersistence = new BinaryPersistence();

    @FXML
    public void handleExportarBinario() {
        try {
            SystemState state = new SystemState(
                    userViewController.getUsersSnapshot(),
                    contentViewController.getContentRecordsSnapshot(),
                    artistViewController.getArtistsSnapshot(),
                    graphViewController.getGraphVerticesSnapshot(),
                    graphViewController.getGraphEdgesSnapshot()
            );

            binaryPersistence.save(state);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Exportacao Binaria", "Estado completo guardado em data/system_state.bin");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Nao foi possivel exportar o estado binario:\n" + e.getMessage());
        }
    }

    @FXML
    public void handleImportarBinario() {
        try {
            SystemState state = binaryPersistence.load();

            userViewController.loadUsersSnapshot(state.getUsers());
            contentViewController.loadContentRecordsSnapshot(state.getContents());
            artistViewController.loadArtistsSnapshot(state.getArtists());
            graphViewController.loadGraphSnapshot(state.getGraphVertices(), state.getGraphEdges());

            mostrarAlerta(Alert.AlertType.INFORMATION, "Importacao Binaria", "Estado completo carregado de data/system_state.bin");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Nao foi possivel importar o estado binario:\n" + e.getMessage());
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
