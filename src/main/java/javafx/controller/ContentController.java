package javafx.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

public class ContentController {
    @FXML private TextField txtTituloContent;
    @FXML private TextField txtAnoContent;
    @FXML private ComboBox<String> cmbTipoContent;
    @FXML private TilePane tileContent;

    @FXML
    public void initialize() {
        cmbTipoContent.getItems().addAll("Filme", "Série", "Documentário");
        cmbTipoContent.setValue("Filme");
        carregarConteudosIniciais();
    }

    private void carregarConteudosIniciais() {
        tileContent.getChildren().addAll(
                criarCartaoContent("The Shawshank Redemption", 1994, "Filme"),
                criarCartaoContent("Breaking Bad", 2008, "Série"),
                criarCartaoContent("Planet Earth", 2006, "Documentário"),
                criarCartaoContent("The Dark Knight", 2008, "Filme"),
                criarCartaoContent("Avatar", 2009, "Filme"),
                criarCartaoContent("Inception", 2010, "Filme"),
                criarCartaoContent("Interstellar", 2014, "Filme"),
                criarCartaoContent("The Last of Us", 2023, "Série"),
                criarCartaoContent("Top Gun: Maverick", 2022, "Filme"),
                criarCartaoContent("Oppenheimer", 2023, "Filme")
        );
    }

    @FXML
    public void handleAdicionarContent() {
        String titulo = txtTituloContent.getText() != null ? txtTituloContent.getText().trim() : "";
        String anoTexto = txtAnoContent.getText() != null ? txtAnoContent.getText().trim() : "";
        String tipo = cmbTipoContent.getValue();

        if (titulo.isEmpty() || anoTexto.isEmpty() || tipo == null || tipo.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Vazios", "Preencha o título, o ano e o tipo do conteúdo.");
            return;
        }

        try {
            int ano = Integer.parseInt(anoTexto);
            tileContent.getChildren().add(criarCartaoContent(titulo, ano, tipo));
            txtTituloContent.clear();
            txtAnoContent.clear();
            cmbTipoContent.setValue("Filme");
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Ano Inválido", "O ano deve ser um número. Exemplo: 2026.");
        }
    }

    private VBox criarCartaoContent(String titulo, int ano, String tipo) {
        Label lblImagem = new Label("Imagem");
        lblImagem.setStyle("-fx-text-fill: #1a242f;");

        StackPane imagem = new StackPane(lblImagem);
        imagem.setPrefSize(170, 210);
        imagem.setStyle("-fx-background-color: #d9dde5; -fx-border-color: #9aa3b2; -fx-border-radius: 4; -fx-background-radius: 4;");

        Label lblTitulo = new Label(titulo);
        lblTitulo.getStyleClass().add("label-header");
        lblTitulo.setStyle("-fx-text-fill: #ffffff;");

        Label lblTipo = new Label(tipo);
        lblTipo.setStyle("-fx-text-fill: #ffffff;");

        Label lblAno = new Label("Ano: " + ano);
        lblAno.setStyle("-fx-text-fill: #ffffff;");

        VBox cartao = new VBox(6, imagem, lblTitulo, lblTipo, lblAno);
        cartao.setPrefSize(203, 303);
        cartao.setPadding(new Insets(10));
        cartao.getStyleClass().add("content-card");
        cartao.setStyle("-fx-background-color: #000000;");

        return cartao;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
