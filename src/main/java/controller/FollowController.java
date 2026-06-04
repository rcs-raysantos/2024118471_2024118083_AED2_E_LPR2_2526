package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import model.graph.GraphEdge;
import model.graph.RelationType;
import model.graph.StreamingGraph;
import model.users.User;
import service.st.UserST;
import java.util.List;

public class FollowController {
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
        String email = txtSearchEmail.getText() != null ? txtSearchEmail.getText().trim() : "";
        if (email.isEmpty() || streamingGraph == null || userST == null) return;

        followingItems.clear();
        List<GraphEdge> edges = streamingGraph.getOutgoingEdges(email);
        for (GraphEdge edge : edges) {
            if (edge.getMetadata().getType() == RelationType.USER_FOLLOWS) {
                User u = userST.get(edge.getTo());
                if (u != null) followingItems.add(u);
            }
        }
    }
}