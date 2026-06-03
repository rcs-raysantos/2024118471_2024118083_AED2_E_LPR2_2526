package controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import service.serialization.ContentRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controlador JavaFX responsável pela gestão visual e lógica do catálogo de conteúdos.
 * Coordena operações de criação, remoção, filtragem estrutural de cartões em um {@link TilePane}
 * e a persistência de dados em arquivos de texto no formato CSV/tokenizado.
 */
public class ContentController {
    @FXML private TextField txtTituloContent;
    @FXML private TextField txtTituloRemoverContent;
    @FXML private TextField txtAnoContent;
    @FXML private TextField txtDuracaoContent;
    @FXML private ComboBox<String> cmbTipoContent;
    @FXML private TilePane tileContent;

    /**
     * Inicializa os controlos de interface associados ao FXML.
     * Configura os valores de preenchimento do ComboBox de categorias e efetua o carregamento
     * dos componentes visuais do catálogo inicial.
     */
    @FXML
    public void initialize() {
        cmbTipoContent.getItems().addAll("Filme", "Série", "Documentário");
        cmbTipoContent.setValue("Filme");
        carregarConteudosIniciais();
    }

    /**
     * Popula o contentor gráfico {@link TilePane} com um conjunto predefinido de cartões de conteúdo
     * demonstrativos contendo filmes, séries e documentários.
     */
    private void carregarConteudosIniciais() {
        tileContent.getChildren().addAll(
                criarCartaoContent("The Shawshank Redemption", 1994, 142, "Filme"),
                criarCartaoContent("Breaking Bad", 2008, 5, "Série"),
                criarCartaoContent("Planet Earth", 2006, 11, "Documentário"),
                criarCartaoContent("The Dark Knight", 2008, 152, "Filme"),
                criarCartaoContent("Avatar", 2009, 162, "Filme"),
                criarCartaoContent("Inception", 2010, 148, "Filme"),
                criarCartaoContent("Interstellar", 2014, 169, "Filme"),
                criarCartaoContent("The Last of Us", 2023, 1, "Série"),
                criarCartaoContent("Top Gun: Maverick", 2022, 130, "Filme"),
                criarCartaoContent("Oppenheimer", 2023, 180, "Filme")
        );
    }

    /**
     * Trata a ação de inserção de um novo elemento no catálogo a partir do formulário FXML.
     * Valida o preenchimento de strings vazias e analisa a coerência numérica do ano e da duração
     * antes de anexar dinamicamente o novo cartão gráfico ao contentor.
     */
    @FXML
    public void handleAdicionarContent() {
        String titulo = txtTituloContent.getText() != null ? txtTituloContent.getText().trim() : "";
        String anoTexto = txtAnoContent.getText() != null ? txtAnoContent.getText().trim() : "";
        String duracaoTexto = txtDuracaoContent.getText() != null ? txtDuracaoContent.getText().trim() : "";
        String tipo = cmbTipoContent.getValue();

        if (titulo.isEmpty() || anoTexto.isEmpty() || duracaoTexto.isEmpty() || tipo == null || tipo.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Vazios", "Preencha o título, o ano, a duração e o tipo do conteúdo.");
            return;
        }

        try {
            int ano = Integer.parseInt(anoTexto);
            int duracao = Integer.parseInt(duracaoTexto);

            tileContent.getChildren().add(criarCartaoContent(titulo, ano, duracao, tipo));

            txtTituloContent.clear();
            txtAnoContent.clear();
            txtDuracaoContent.clear();
            cmbTipoContent.setValue("Filme");
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Formato Inválido", "O ano e a duração devem ser números válidos.");
        }
    }

    /**
     * Trata o pedido de remoção de um conteúdo por meio do título textual informado.
     * Realiza uma varredura linear reflexiva nos nós do {@link TilePane}, extrai o identificador interno
     * via lookup e, mediante confirmação do utilizador via janela modal, desanexa o componente da árvore visual.
     */
    @FXML
    public void handleRemoverContent() {
        String tituloRemover = txtTituloRemoverContent.getText() != null ? txtTituloRemoverContent.getText().trim() : "";

        if (tituloRemover.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo Vazio", "Escreva o titulo do conteudo que pretende remover.");
            return;
        }

        VBox cartaoEncontrado = null;
        String tituloEncontrado = null;

        for (Node node : tileContent.getChildren()) {
            if (node instanceof VBox) {
                VBox cartao = (VBox) node;
                Label lblTitulo = (Label) cartao.lookup("#idTitulo");

                if (lblTitulo != null && lblTitulo.getText().equalsIgnoreCase(tituloRemover)) {
                    cartaoEncontrado = cartao;
                    tituloEncontrado = lblTitulo.getText();
                    break;
                }
            }
        }

        if (cartaoEncontrado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Conteudo Nao Encontrado", "Nao existe nenhum conteudo com o titulo \"" + tituloRemover + "\".");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Remover Conteudo");
        confirmacao.setHeaderText(null);
        confirmacao.setContentText("Tem a certeza que pretende remover \"" + tituloEncontrado + "\"?");

        Optional<ButtonType> resposta = confirmacao.showAndWait();
        if (resposta.isPresent() && resposta.get() == ButtonType.OK) {
            tileContent.getChildren().remove(cartaoEncontrado);
            txtTituloRemoverContent.clear();
        }
    }

    /**
     * Realiza a leitura e parsing estruturado de dados oriundos de um ficheiro de texto externo (.txt)
     * selecionado pelo utilizador. Limpa o catálogo gráfico corrente e reconstrói a matriz visual de cartões.
     */
    @FXML
    public void handleImportarDados() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importar Catálogo de Conteúdos");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ficheiros de Texto (*.txt)", "*.txt"));
        File file = fileChooser.showOpenDialog(tileContent.getScene().getWindow());

        if (file != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String linha;
                int contador = 0;

                tileContent.getChildren().clear();

                while ((linha = br.readLine()) != null) {
                    linha = linha.trim();
                    if (linha.isEmpty()) continue;

                    // Formato: Título;Ano;Duração;Tipo
                    String[] dados = linha.split(";");
                    if (dados.length >= 4) {
                        String titulo = dados[0].trim();
                        int ano = Integer.parseInt(dados[1].trim());
                        int duracao = Integer.parseInt(dados[2].trim());
                        String tipo = dados[3].trim();

                        tileContent.getChildren().add(criarCartaoContent(titulo, ano, duracao, tipo));
                        contador++;
                    }
                }
                mostrarAlerta(Alert.AlertType.INFORMATION, "Importação Concluída",
                        "Sucesso! Foram importados " + contador + " conteúdos para o catálogo.");
            } catch (IOException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro de Leitura", "Não foi possível ler o ficheiro: " + e.getMessage());
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro de Formato", "O ficheiro contém valores numéricos inválidos.");
            }
        }
    }

    /**
     * Captura os dados textuais embutidos nas sub-labels de cada cartão visível no {@link TilePane},
     * serializa os atributos ordenadamente delimitados por ponto e vírgula e grava-os em formato de arquivo texto (.txt).
     */
    @FXML
    public void handleExportarDados() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exportar Catálogo de Conteúdos");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ficheiros de Texto (*.txt)", "*.txt"));
        fileChooser.setInitialFileName("catalogo_conteudos.txt");
        File file = fileChooser.showSaveDialog(tileContent.getScene().getWindow());

        if (file != null) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                for (Node node : tileContent.getChildren()) {
                    if (node instanceof VBox) {
                        VBox cartao = (VBox) node;

                        Label lblTitulo = (Label) cartao.lookup("#idTitulo");
                        Label lblTipo = (Label) cartao.lookup("#idTipo");
                        Label lblAno = (Label) cartao.lookup("#idAno");
                        Label lblDuracao = (Label) cartao.lookup("#idDuracao"); // <-- Busca a duração no cartão

                        if (lblTitulo != null && lblTipo != null && lblAno != null && lblDuracao != null) {
                            String titulo = lblTitulo.getText();
                            String tipo = lblTipo.getText();
                            String anoStr = lblAno.getText().replace("Ano: ", "").trim();

                            // Extrai apenas o número da duração (remove " min" ou " Temp")
                            String duracaoStr = lblDuracao.getText().replaceAll("[^0-9]", "");

                            bw.write(titulo + ";" + anoStr + ";" + duracaoStr + ";" + tipo);
                            bw.newLine();
                        }
                    }
                }
                mostrarAlerta(Alert.AlertType.INFORMATION, "Exportação Concluída", "Catálogo guardado com sucesso em: " + file.getName());
            } catch (IOException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro de Escrita", "Não foi possível gravar o ficheiro: " + e.getMessage());
            }
        }
    }

    /**
     * Fábrica de componentes gráficos interna encarregada de estruturar um cartão visual customizado
     * baseado no container {@link VBox}. Ajusta dinamicamente ícones, sufixos e folhas de estilo inline (CSS)
     * com comportamento de efeito de realce (hover neon) conforme a categoria informada.
     *
     * @param titulo  O título textual descritivo do conteúdo.
     * @param ano     O ano correspondente ao lançamento do registo.
     * @param duracao A quantidade inteira representativa da duração de tempo/temporadas.
     * @param tipo    O rótulo identificador do tipo ("Filme", "Série", "Documentário").
     * @return Um container {@link VBox} completamente formatado e preparado para exibição em grelha.
     */
    private VBox criarCartaoContent(String titulo, int ano, int duracao, String tipo) {
        Label lblIcone = new Label();
        lblIcone.setFont(Font.font("System", FontWeight.BOLD, 28));

        String corNeon;
        String sufixoDuracao; // Define se vai usar "min" ou "Temp"

        if (tipo.equalsIgnoreCase("Filme")) {
            lblIcone.setText("🎬");
            corNeon = "#e74c3c"; // Vermelho Alizarina
            tipo = "Filme";
            sufixoDuracao = " min";
        } else if (tipo.equalsIgnoreCase("Série")) {
            lblIcone.setText("📺");
            corNeon = "#c0392b"; // Vermelho Romã
            tipo = "Série";
            sufixoDuracao = " Temp";
        } else {
            lblIcone.setText("🌿");
            corNeon = "#ff7675"; // Vermelho suave / Coral
            tipo = "Documentário";
            sufixoDuracao = " min";
        }

        StackPane areaImagem = new StackPane(lblIcone);
        areaImagem.setPrefSize(160, 110);
        areaImagem.setStyle("-fx-background-color: #1a242f; "
                + "-fx-border-color: " + corNeon + "; "
                + "-fx-border-radius: 6; "
                + "-fx-background-radius: 6; "
                + "-fx-border-width: 1.5;");

        Label lblTitulo = new Label(titulo);
        lblTitulo.setId("idTitulo");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblTitulo.setStyle("-fx-text-fill: #ffffff;");
        lblTitulo.setWrapText(true);
        lblTitulo.setMaxWidth(160);
        lblTitulo.setMinHeight(40);
        lblTitulo.setAlignment(Pos.TOP_LEFT);

        Label lblTipo = new Label(tipo);
        lblTipo.setId("idTipo");
        lblTipo.setFont(Font.font("System", FontWeight.BOLD, 9));
        lblTipo.setPadding(new Insets(2, 6, 2, 6));
        lblTipo.setStyle("-fx-background-color: " + corNeon + "; -fx-text-fill: #ffffff; -fx-background-radius: 3;");

        Label lblAno = new Label("Ano: " + ano);
        lblAno.setId("idAno");
        lblAno.setFont(Font.font("System", FontWeight.NORMAL, 11));
        lblAno.setStyle("-fx-text-fill: #8197a4;");

        // NOVO: Label da duração/temporadas formatada dinamicamente
        Label lblDuracao = new Label(duracao + sufixoDuracao);
        lblDuracao.setId("idDuracao");
        lblDuracao.setFont(Font.font("System", FontWeight.NORMAL, 11));
        lblDuracao.setStyle("-fx-text-fill: #e2e8f0;"); // Uma cor clarinha para destacar do ano

        HBox metadataBox = new HBox(8, lblTipo, lblAno, lblDuracao);
        metadataBox.setAlignment(Pos.CENTER_LEFT);

        VBox cartao = new VBox(10, areaImagem, lblTitulo, metadataBox);
        cartao.setPrefSize(180, 210);
        cartao.setPadding(new Insets(10));
        cartao.setStyle("-fx-background-color: #151e27; "
                + "-fx-background-radius: 8; "
                + "-fx-border-radius: 8; "
                + "-fx-border-color: #232f3e; "
                + "-fx-border-width: 1;");

        final String corDestaque = corNeon;
        cartao.setOnMouseEntered(e -> cartao.setStyle("-fx-background-color: #1f2d3d; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: " + corDestaque + "; -fx-border-width: 1;"));
        cartao.setOnMouseExited(e -> cartao.setStyle("-fx-background-color: #151e27; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #232f3e; -fx-border-width: 1;"));

        return cartao;
    }

    /**
     * Varre a árvore de nós visuais e constrói uma lista contendo objetos de transferência leves
     * {@link ContentRecord} que representam o instantâneo (snapshot) atual do catálogo em memória.
     *
     * @return Uma lista de objetos {@link ContentRecord} correspondentes ao estado atual da interface.
     */
    public List<ContentRecord> getContentRecordsSnapshot() {
        List<ContentRecord> records = new ArrayList<>();

        for (Node node : tileContent.getChildren()) {
            if (node instanceof VBox) {
                VBox cartao = (VBox) node;
                Label lblTitulo = (Label) cartao.lookup("#idTitulo");
                Label lblTipo = (Label) cartao.lookup("#idTipo");
                Label lblAno = (Label) cartao.lookup("#idAno");
                Label lblDuracao = (Label) cartao.lookup("#idDuracao");

                if (lblTitulo != null && lblTipo != null && lblAno != null && lblDuracao != null) {
                    String titulo = lblTitulo.getText();
                    String tipo = lblTipo.getText();
                    int ano = Integer.parseInt(lblAno.getText().replace("Ano: ", "").trim());
                    int duracao = Integer.parseInt(lblDuracao.getText().replaceAll("[^0-9]", ""));
                    records.add(new ContentRecord(titulo, ano, duracao, tipo));
                }
            }
        }

        return records;
    }

    /**
     * Limpa o container visual principal e renderiza um novo conjunto de cartões com base na lista
     * de instantâneos (snapshots) do modelo de persistência fornecida por parâmetro.
     *
     * @param records Uma lista de {@link ContentRecord} para reconstrução completa da visualização.
     */
    public void loadContentRecordsSnapshot(List<ContentRecord> records) {
        tileContent.getChildren().clear();
        for (ContentRecord record : records) {
            tileContent.getChildren().add(criarCartaoContent(
                    record.getTitle(),
                    record.getYear(),
                    record.getDuration(),
                    record.getType()
            ));
        }
    }

    /**
     * Inicializa, configura e exibe de forma síncrona uma janela modal ou caixa de diálogo de alerta no ecrã.
     *
     * @param tipo     O tipo estrutural de severidade do alerta gráfico.
     * @param titulo   O título que será impresso na borda superior da janela de diálogo.
     * @param mensagem O texto detalhado com o corpo principal da informação contextual.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}