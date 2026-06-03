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

public class FollowersController {
    @FXML private TextField txtSearchEmail; // This is the user whose followers we want to list
    @FXML private TableView<User> tblFollowers;
    @FXML private TableColumn<User, String> colFollowerId;
    @FXML private TableColumn<User, String> colFollowerName;

    private StreamingGraph streamingGraph;
    private UserST userST;
    private ObservableList<User> followerItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colFollowerId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
        colFollowerName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        tblFollowers.setItems(followerItems);
    }

    /**
     * Método público para ser chamado externamente (ex: pelo MainController)
     * para forçar a atualização da lista de seguidores.
     */
    public void refreshFollowersList() {
        handleListFollowers();
    }
    public void setStreamingGraph(StreamingGraph graph) {
        this.streamingGraph = graph;
    }

    public void setUserST(UserST userST) {
        this.userST = userST;
    }

    @FXML
    public void handleListFollowers() {
        String input = txtSearchEmail.getText() != null ? txtSearchEmail.getText().trim() : "";
        if (input.isEmpty() || streamingGraph == null || userST == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo Vazio", "Por favor, introduza o Nome ou Email do utilizador.");
            return;
        }

        followerItems.clear();

        // Resolve Nome para ID (Email) caso o input não seja um ID direto
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
            mostrarAlerta(Alert.AlertType.ERROR, "Não Encontrado", "O utilizador '" + input + "' não existe no sistema.");
            return;
        }

        List<GraphEdge> edges = streamingGraph.getIncomingEdges(userId); // Obtém arestas de entrada (seguidores)
        for (GraphEdge edge : edges) {
            if (edge.getMetadata().getType() == RelationType.USER_FOLLOWS) {
                User u = userST.get(edge.getFrom()); // The 'from' vertex is the follower
                if (u != null) followerItems.add(u);
            }
        }
        if (followerItems.isEmpty()) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sem Resultados", "Não foram encontrados seguidores para o utilizador '" + input + "'.");
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