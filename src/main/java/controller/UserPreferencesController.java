package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import model.graph.GraphEdge;
import model.graph.RelationType;
import model.graph.StreamingGraph;
import model.graph.EdgeMetadata;
import model.users.User;
import service.st.UserST;
import service.serialization.ContentRecord;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserPreferencesController {

    @FXML private ComboBox<User> cmbUserSelector;
    
    // Tabela de Atividade (Excel Style)
    @FXML private TableView<ActivityRow> tblActivity;
    @FXML private TableColumn<ActivityRow, String> colContent;
    @FXML private TableColumn<ActivityRow, String> colType;
    @FXML private TableColumn<ActivityRow, String> colRating;
    @FXML private TableColumn<ActivityRow, String> colRecommended;
    @FXML private TableColumn<ActivityRow, String> colDate;

    // Tabela de Recomendações
    @FXML private TableView<ContentRecord> tblRecommendations;
    @FXML private TableColumn<ContentRecord, String> colRecTitle;
    @FXML private TableColumn<ContentRecord, String> colRecType;

    // Formulário para Adicionar Atividade
    @FXML private ComboBox<ContentRecord> cmbContentSelector;
    @FXML private Spinner<Double> spnRating;
    @FXML private DatePicker dpDate;
    @FXML private CheckBox chkRecommend;

    private StreamingGraph streamingGraph;
    private UserST userST;
    private List<ContentRecord> allContent;

    @FXML
    public void initialize() {
        colContent.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().title));
        colType.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().type));
        colRating.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().rating));
        colRecommended.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().recommended));
        colDate.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().date));

        colRecTitle.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTitle()));
        colRecType.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getType()));

        // Configurar a Data padrão para hoje
        dpDate.setValue(LocalDate.now());

        // Configurar o Spinner de Nota (0.0 a 5.0 com passo de 0.5)
        spnRating.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 5.0, 4.0, 0.5));

        // Configurar conversores para mostrar nomes amigáveis nos ComboBoxes
        setupComboBoxConverters();

        cmbUserSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) refreshData();
        });
    }

    private void setupComboBoxConverters() {
        cmbUserSelector.setConverter(new StringConverter<>() {
            @Override public String toString(User u) { return u == null ? "" : u.getName() + " (" + u.getEmail() + ")"; }
            @Override public User fromString(String s) { return null; }
        });

        cmbContentSelector.setConverter(new StringConverter<>() {
            @Override public String toString(ContentRecord c) { return c == null ? "" : c.getTitle() + " [" + c.getType() + "]"; }
            @Override public ContentRecord fromString(String s) { return null; }
        });
    }

    public void setDependencies(StreamingGraph graph, UserST st, List<ContentRecord> content) {
        this.streamingGraph = graph;
        this.userST = st;
        this.allContent = content;
        updateUserList();
        if (allContent != null) {
            cmbContentSelector.setItems(FXCollections.observableArrayList(allContent));
        }
    }

    public void updateUserList() {
        if (userST != null) {
            cmbUserSelector.setItems(FXCollections.observableArrayList(userST.listAll()));
        }
    }

    @FXML
    public void refreshData() {
        User selected = cmbUserSelector.getValue();
        if (selected == null || streamingGraph == null) return;

        // 1. Carregar Atividade (Watched e Rated)
        List<ActivityRow> activities = new ArrayList<>();
        List<GraphEdge> outgoing = streamingGraph.getOutgoingEdges(selected.getId());

        for (GraphEdge edge : outgoing) {
            if (edge.getMetadata().getType() == RelationType.USER_WATCHED) {
                
                String contentId = edge.getTo();
                String type = "Visto";
                
                Object ratingVal = edge.getMetadata().getExtraData("rating");
                String score = (ratingVal != null) ? String.valueOf(ratingVal) : "-";
                
                Boolean isRec = (Boolean) edge.getMetadata().getExtraData("recommended");
                String recommendedText = isRec != null && isRec ? "Sim" : "Não";
                
                String date = edge.getMetadata().getTimestamp().toLocalDate().toString();
                
                activities.add(new ActivityRow(contentId, type, score, recommendedText, date));
            }
        }
        tblActivity.setItems(FXCollections.observableArrayList(activities));

        // 2. Motor de Recomendações Simples
        // Busca conteúdos de gêneros que o user prefere e que ainda não viu
        List<String> preferredGenres = outgoing.stream()
                .filter(e -> e.getMetadata().getType() == RelationType.USER_PREFERS_GENRE)
                .map(GraphEdge::getTo)
                .collect(Collectors.toList());

        List<String> watchedContent = activities.stream().map(a -> a.title).collect(Collectors.toList());

        List<ContentRecord> recommendations = allContent.stream()
                .filter(c -> !watchedContent.contains(c.getTitle()))
                // Aqui poderíamos cruzar com os gêneros no Grafo, simplificamos para mostrar lógica
                .limit(5)
                .collect(Collectors.toList());

        tblRecommendations.setItems(FXCollections.observableArrayList(recommendations));
    }

    @FXML
    public void handleAddActivity() {
        User selected = cmbUserSelector.getValue();
        ContentRecord content = cmbContentSelector.getValue();
        Double rating = spnRating.getValue();
        LocalDateTime activityDate = dpDate.getValue().atStartOfDay();

        if (selected == null || content == null) return;

        // Adiciona "Visto" ao Grafo
        Map<String, Object> extra = new HashMap<>();
        extra.put("rating", rating);
        extra.put("recommended", chkRecommend.isSelected());
        
        streamingGraph.addEdge(selected.getId(), content.getTitle(), 
                new EdgeMetadata(RelationType.USER_WATCHED, 1.0, activityDate, extra));

        chkRecommend.setSelected(false);
        refreshData();
    }

    // Classe auxiliar para a grelha de estilo Excel
    public static class ActivityRow {
        public String title, type, rating, recommended, date;
        public ActivityRow(String title, String type, String rating, String recommended, String date) {
            this.title = title; this.type = type; this.rating = rating; 
            this.recommended = recommended; this.date = date;
        }
    }
}