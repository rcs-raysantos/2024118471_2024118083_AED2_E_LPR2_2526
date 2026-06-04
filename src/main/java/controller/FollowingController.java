package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import model.graph.GraphEdge;
import model.graph.RelationType;
import model.graph.StreamingGraph;
import model.users.User;
import service.st.UserST;
import java.util.List;

public class FollowingController {
    @FXML private TextField txtSearchEmail;
    @FXML private TableView<User> tblFollowing;
    @FXML private TableColumn<User, String> colFollowingId;
    @FXML private TableColumn<User, String> colFollowingNome;

    private StreamingGraph streamingGraph;
    private UserST userST;
    private ObservableList<User> followingItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colFollowingId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
        colFollowingNome.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        tblFollowing.setItems(followingItems);
    }

    public void setStreamingGraph(StreamingGraph graph) {
        this.streamingGraph = graph;
    }

    public void setUserST(UserST userST) {
        this.userST = userST;
    }

    @FXML
    public void handleListFollowing() {
        String input = txtSearchEmail.getText() != null ? txtSearchEmail.getText().trim() : "";
        if (input.isEmpty() || streamingGraph == null || userST == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo Vazio", "Introduza o Nome ou Email do utilizador.");
            return;
        }

        followingItems.clear();
        
        // Tenta encontrar o ID (Email) se o utilizador tiver digitado um Nome
        String userId = input;
        if (!userST.contains(input)) {
            for (User u : userST.listAll()) {
                if (u.getName().equalsIgnoreCase(input)) {
                    userId = u.getId();
                    break;
                }
            }
        }

        if (!streamingGraph.containsVertex(userId)) {
            mostrarAlerta(Alert.AlertType.ERROR, "Não Encontrado", "O utilizador '" + input + "' não existe no grafo.");
            return;
        }

        List<GraphEdge> edges = streamingGraph.getOutgoingEdges(userId);
        for (GraphEdge edge : edges) {
            if (edge.getMetadata().getType() == RelationType.USER_FOLLOWS) {
                User u = userST.get(edge.getTo());
                if (u != null) followingItems.add(u);
            }
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