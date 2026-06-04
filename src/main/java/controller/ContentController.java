package controller;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import model.content.Content;
import model.content.Documentary;
import model.content.Movie;
import model.content.Series;
import service.UserService;
import service.serialization.ContentRecord;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controlador JavaFX adaptado para a estrutura de TableView com ID, filtros superiores,
 * painel de registo/edição lateral e suporte a Rating (Nota) em Float.
 * <p>
 * Atua como intermediário entre a interface gráfica do utilizador (camada de apresentação)
 * e os serviços de negócio que gerem as tabelas de símbolos e árvores binárias.
 */
public class ContentController {

    /** Campo de texto para filtrar os conteúdos da tabela pelo título. */
    @FXML private TextField txtFiltroTitulo;

    /** Caixa de seleção para filtrar os conteúdos da tabela pelo tipo (Filme, Série, etc.). */
    @FXML private ComboBox<String> cmbFiltroTipo;

    /** Campo de texto para filtrar os conteúdos da tabela pelo ano de lançamento. */
    @FXML private TextField txtFiltroAno;

    /** Tabela visual para exibição dos objetos do tipo {@link Content}. */
    @FXML private TableView<Content> tblContents;

    /** Coluna da tabela que exibe o identificador único do conteúdo. */
    @FXML private TableColumn<Content, String> colId;

    /** Coluna da tabela que exibe o título do conteúdo. */
    @FXML private TableColumn<Content, String> colTitulo;

    /** Coluna da tabela que exibe a tipologia da obra de forma amigável. */
    @FXML private TableColumn<Content, String> colTipo;

    /** Coluna da tabela que exibe o ano de lançamento do conteúdo. */
    @FXML private TableColumn<Content, Integer> colAno;

    /** Coluna da tabela que exibe a duração em minutos ou o número de temporadas. */
    @FXML private TableColumn<Content, Integer> colDuracao;

    /** Coluna da tabela que exibe a classificação/avaliação (rating) do conteúdo em formato float. */
    @FXML private TableColumn<Content, Float> colRating;

    /** Campo de entrada lateral para introduzir ou editar o título do conteúdo. */
    @FXML private TextField txtTituloContent;

    /** Caixa de seleção lateral para definir o tipo de conteúdo a registar ou editar. */
    @FXML private ComboBox<String> cmbTipoContent;

    /** Campo de entrada lateral para introduzir ou editar o ano do conteúdo. */
    @FXML private TextField txtAnoContent;

    /** Campo de entrada lateral para introduzir ou editar a duração ou temporadas do conteúdo. */
    @FXML private TextField txtDuracaoContent;

    /** Campo de entrada lateral para introduzir ou editar a nota de avaliação do conteúdo. */
    @FXML private TextField txtRatingContent;

    /**
     * Lista observável que alimenta diretamente a TableView.
     * Sincroniza automaticamente as mutações de dados na lista com os elementos visuais da tabela.
     */
    private final ObservableList<Content> obsContents = FXCollections.observableArrayList();

    /**
     * Serviço central que gere as estruturas de dados (ST + BST).
     */
    private UserService userService;

    /**
     * Define o serviço de gestão de utilizadores e conteúdos e atualiza de imediato a tabela visual.
     *
     * @param userService A instância do serviço de retaguarda {@link UserService}.
     */
    public void setUserService(UserService userService) {
        this.userService = userService;
        atualizarCatalogoVisual();
    }

    /**
     * Método de ciclo de vida do JavaFX, executado automaticamente após o carregamento do ficheiro FXML.
     * Configura as fábricas de células das colunas da tabela, adiciona ouvintes (listeners)
     * para seleção de linhas e povoa os elementos iniciais do catálogo.
     */
    @FXML
    public void initialize() {
        cmbTipoContent.getItems().addAll("Filme", "Série", "Documentário");
        cmbTipoContent.setValue("Filme");

        cmbFiltroTipo.getItems().addAll("Todos", "Filme", "Série", "Documentário");
        cmbFiltroTipo.setValue("Todos");

        colId.setCellValueFactory(cellData -> {
            Content c = cellData.getValue();
            String id = (c != null && c.getId() != null) ? c.getId() : "";
            return new SimpleStringProperty(id);
        });

        colTitulo.setCellValueFactory(cellData -> {
            Content c = cellData.getValue();
            String titulo = (c != null && c.getTitle() != null) ? c.getTitle() : "";
            return new SimpleStringProperty(titulo);
        });

        colTipo.setCellValueFactory(cellData -> {
            Content c = cellData.getValue();
            String tipo = "Filme";
            if (c instanceof Series) tipo = "Série";
            else if (c instanceof Documentary) tipo = "Documentário";
            return new SimpleStringProperty(tipo);
        });

        colAno.setCellValueFactory(cellData -> {
            Content c = cellData.getValue();
            int ano = (c != null && c.getReleaseDate() != null) ? c.getReleaseDate().getYear() : 2000;
            return new SimpleIntegerProperty(ano).asObject();
        });

        colDuracao.setCellValueFactory(cellData -> {
            Content c = cellData.getValue();
            if (c == null) return new SimpleIntegerProperty(0).asObject();

            int duracaoExibicao = c.getDuration();
            if (c instanceof Series) {
                duracaoExibicao = ((Series) c).getSeasons();
            }
            return new SimpleIntegerProperty(duracaoExibicao).asObject();
        });

        if (colRating != null) {
            colRating.setCellValueFactory(cellData -> {
                Content c = cellData.getValue();
                float nota = (c != null) ? (float) c.getRating() : 0.0f;
                return new SimpleFloatProperty(nota).asObject();
            });
        }

        tblContents.setItems(obsContents);

        tblContents.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                txtTituloContent.setText(newValue.getTitle());
                txtAnoContent.setText(String.valueOf(newValue.getReleaseDate() != null ? newValue.getReleaseDate().getYear() : 2000));
                txtRatingContent.setText(String.valueOf((float) newValue.getRating()));

                if (newValue instanceof Series) {
                    cmbTipoContent.setValue("Série");
                    txtDuracaoContent.setText(String.valueOf(((Series) newValue).getSeasons()));
                } else if (newValue instanceof Documentary) {
                    cmbTipoContent.setValue("Documentário");
                    txtDuracaoContent.setText(String.valueOf(newValue.getDuration()));
                } else {
                    cmbTipoContent.setValue("Filme");
                    txtDuracaoContent.setText(String.valueOf(newValue.getDuration()));
                }
            }
        });

        carregarConteudosIniciais();
    }

    /**
     * Carrega conteúdos de teste já associando um valor de Rating inicial (em float/int).
     */
    private void carregarConteudosIniciais() {
        adicionarConteudo("The Shawshank Redemption", LocalDate.of(1994, 1, 1), 142, "Filme", 5.0f);
        adicionarConteudo("Breaking Bad", LocalDate.of(2008, 1, 1),   5, "Série", 4.8f);
        adicionarConteudo("Planet Earth", LocalDate.of(2006, 1, 1),  11, "Documentário", 4.7f);
        adicionarConteudo("The Dark Knight", LocalDate.of(2008, 1, 1), 152, "Filme", 4.6f);
        adicionarConteudo("Avatar", LocalDate.of(2009, 1, 1), 162, "Filme", 3.9f);
        adicionarConteudo("Inception", LocalDate.of(2010, 1, 1), 148, "Filme", 4.5f);
        adicionarConteudo("Interstellar", LocalDate.of(2014, 1, 1), 169, "Filme", 4.6f);
        adicionarConteudo("The Last of Us", LocalDate.of(2023, 1, 1),   1, "Série", 4.4f);
        adicionarConteudo("Top Gun: Maverick", LocalDate.of(2022, 1, 1), 130, "Filme", 4.1f);
        adicionarConteudo("Oppenheimer", LocalDate.of(2023, 1, 1), 180, "Filme", 4.5f);
    }

    /**
     * Trata o evento de ação do botão de filtragem. Obtém os critérios informados nos campos
     * superiores e atualiza a tabela com os conteúdos que respeitam simultaneamente os filtros aplicados.
     */
    @FXML
    public void handleFiltrar() {
        if (userService == null || userService.getContentST() == null) return;

        String filtroTitulo = txtFiltroTitulo.getText() != null ? txtFiltroTitulo.getText().trim().toLowerCase() : "";
        String filtroTipo = cmbFiltroTipo.getValue();
        String filtroAnoTexto = txtFiltroAno.getText() != null ? txtFiltroAno.getText().trim() : "";

        List<Content> filtrados = new ArrayList<>();

        for (Content c : userService.getContentST().listAll()) {
            if (!filtroTitulo.isEmpty() && !c.getTitle().toLowerCase().contains(filtroTitulo)) {
                continue;
            }

            if (filtroTipo != null && !filtroTipo.equals("Todos")) {
                if (filtroTipo.equals("Filme") && (c instanceof Series || c instanceof Documentary)) continue;
                if (filtroTipo.equals("Série") && !(c instanceof Series)) continue;
                if (filtroTipo.equals("Documentário") && !(c instanceof Documentary)) continue;
            }

            if (!filtroAnoTexto.isEmpty()) {
                try {
                    int anoFiltro = Integer.parseInt(filtroAnoTexto);
                    int anoConteudo = c.getReleaseDate() != null ? c.getReleaseDate().getYear() : 2000;
                    if (anoConteudo != anoFiltro) {
                        continue;
                    }
                } catch (NumberFormatException e) {
                    // Ignora filtro de ano inválido
                }
            }
            filtrados.add(c);
        }
        obsContents.setAll(filtrados);
    }

    /**
     * Trata o evento de ação de limpeza de filtros. Remove o texto dos campos de pesquisa,
     * redefine as caixas de seleção e restaura a visualização integral do catálogo.
     */
    @FXML
    public void handleLimpar() {
        txtFiltroTitulo.clear();
        cmbFiltroTipo.setValue("Todos");
        txtFiltroAno.clear();
        atualizarCatalogoVisual();
    }

    /**
     * Trata a ação de inserção ou atualização de um conteúdo. Valida as entradas lidas
     * no formulário lateral, remove um possível registo homónimo pré-existente e insere
     * a nova instância polimórfica com a avaliação especificada.
     */
    @FXML
    public void handleAdicionarContent() {
        String titulo = txtTituloContent.getText() != null ? txtTituloContent.getText().trim() : "";
        String anoTexto = txtAnoContent.getText() != null ? txtAnoContent.getText().trim() : "";
        String duracaoTexto = txtDuracaoContent.getText() != null ? txtDuracaoContent.getText().trim() : "";
        String ratingTexto = txtRatingContent != null && txtRatingContent.getText() != null ? txtRatingContent.getText().trim() : "0.0";
        String tipo = cmbTipoContent.getValue();

        if (titulo.isEmpty() || anoTexto.isEmpty() || duracaoTexto.isEmpty() || tipo == null || tipo.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Vazios", "Preencha todos os campos do formulário lateral.");
            return;
        }

        try {
            int ano = Integer.parseInt(anoTexto);
            int duracao = Integer.parseInt(duracaoTexto);
            float rating = Float.parseFloat(ratingTexto); // Captura o rating float digitado pelo user
            LocalDate dataLancamento = LocalDate.of(ano, 1, 1);

            Content selecionado = tblContents.getSelectionModel().getSelectedItem();
            if (selecionado != null && selecionado.getTitle().equalsIgnoreCase(titulo)) {
                userService.removeContent(selecionado.getId());
            }

            adicionarConteudo(titulo, dataLancamento, duracao, tipo, rating);
            limparFormularioLateral();
            atualizarCatalogoVisual();

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Formato Inválido", "O ano, duração e avaliação devem ser números válidos.");
        }
    }

    /**
     * Trata a ação de remoção do conteúdo atualmente selecionado na tabela.
     * Solicita confirmação explícita ao utilizador por intermédio de um alerta modal antes
     * de proceder com a exclusão definitiva no serviço de dados.
     */
    @FXML
    public void handleRemoverContent() {
        Content selecionado = tblContents.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Nenhum Conteúdo Selecionado",
                    "Selecione um conteúdo na tabela para o poder remover.");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Remover Conteúdo");
        confirmacao.setHeaderText(null);
        confirmacao.setContentText("Tem a certeza que pretende remover \"" + selecionado.getTitle() + "\"?");

        Optional<ButtonType> resposta = confirmacao.showAndWait();
        if (resposta.isPresent() && resposta.get() == ButtonType.OK) {
            if (userService != null) {
                userService.removeContent(selecionado.getId());
            }
            limparFormularioLateral();
            atualizarCatalogoVisual();
        }
    }

    /**
     * Executa o fluxo de importação em lote de conteúdos a partir de um arquivo de texto.
     * Limpa o catálogo prévio e processa sequencialmente as linhas delimitadas por ponto e vírgula,
     * incluindo a leitura opcional do rating na 5ª posição do vetor tokenizado.
     */
    @FXML
    public void handleImportarDados() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importar Catálogo");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ficheiros de Texto (*.txt)", "*.txt"));
        File file = fileChooser.showOpenDialog(tblContents.getScene().getWindow());

        if (file != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String linha;
                int contador = 0;

                limparConteudosDoServico();
                obsContents.clear();

                while ((linha = br.readLine()) != null) {
                    linha = linha.trim();
                    if (linha.isEmpty()) continue;

                    String[] dados = linha.split(";");
                    if (dados.length >= 4) {
                        String titulo = dados[0].trim();
                        int ano = Integer.parseInt(dados[1].trim());
                        int duracao = Integer.parseInt(dados[2].trim());
                        String tipo = dados[3].trim();
                        float rating = (dados.length >= 5) ? Float.parseFloat(dados[4].trim()) : 0.0f;

                        LocalDate dataLancamento = LocalDate.of(ano, 1, 1);
                        adicionarConteudoTabela(titulo, dataLancamento, duracao, tipo, rating);
                        contador++;
                    }
                }

                if (userService != null && userService.getContentST() != null) {
                    obsContents.addAll(userService.getContentST().listAll());
                }
                tblContents.refresh();

                mostrarAlerta(Alert.AlertType.INFORMATION, "Importação Concluída", "Foram importados " + contador + " conteúdos.");

            } catch (IOException | NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Falha ao importar ficheiro: " + e.getMessage());
            }
        }
    }

    /**
     * Exporta a totalidade de conteúdos persistidos na Symbol Table para um arquivo de texto.
     * Grava os atributos de cada item de forma linear estruturada por delimitadores (';'),
     * salvaguardando o título, ano, tempo/temporadas, tipo e rating em formato decimal.
     */
    @FXML
    public void handleExportarDados() {
        if (userService == null || userService.getContentST() == null || userService.getContentST().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Não existem conteúdos para exportar.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exportar Catálogo");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ficheiros de Texto (*.txt)", "*.txt"));
        fileChooser.setInitialFileName("catalogo_conteudos.txt");
        File file = fileChooser.showSaveDialog(tblContents.getScene().getWindow());

        if (file != null) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                for (Content c : userService.getContentST().listAll()) {
                    String tipoStr = "Filme";
                    int duracaoExibicao = c.getDuration();
                    if (c instanceof Series) {
                        tipoStr = "Série";
                        duracaoExibicao = ((Series) c).getSeasons();
                    } else if (c instanceof Documentary) {
                        tipoStr = "Documentário";
                    }

                    int ano = c.getReleaseDate() != null ? c.getReleaseDate().getYear() : 2000;
                    bw.write(c.getTitle() + ";" + ano + ";" + duracaoExibicao + ";" + tipoStr + ";" + (float)c.getRating());
                    bw.newLine();
                }
                mostrarAlerta(Alert.AlertType.INFORMATION, "Exportação Concluída", "Catálogo guardado com sucesso.");
            } catch (IOException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao gravar o ficheiro.");
            }
        }
    }

    /**
     * Sincroniza e recarrega os dados do repositório lógico interno para a lista observável da tabela.
     */
    private void atualizarCatalogoVisual() {
        obsContents.clear();
        if (userService != null && userService.getContentST() != null) {
            obsContents.addAll(userService.getContentST().listAll());
        }
        tblContents.refresh();
    }

    /**
     * Cria a instância apropriada da subclasse de conteúdo, atribui-lhe a classificação
     * fornecida, submete-a para registo no serviço e adiciona-a ao contentor observável local.
     */
    private void adicionarConteudo(String titulo, LocalDate releaseDate, int duracao, String tipo, float rating) {
        Content content = criarModeloContent(titulo, releaseDate, duracao, tipo);
        try {
            content.rate((int) rating);
        } catch (IllegalArgumentException ignored) {}

        if (userService != null) {
            try {
                userService.registerContent(content);
            } catch (IllegalArgumentException ignored) {}
        }
        obsContents.add(content);
    }

    /**
     * Cria e adiciona uma nova instância de conteúdo diretamente nas estruturas de dados
     * lógicas do serviço de negócio, sem interagir com a lista da tabela gráfica.
     */
    private void adicionarConteudoTabela(String titulo, LocalDate releaseDate, int duracao, String tipo, float rating) {
        Content content = criarModeloContent(titulo, releaseDate, duracao, tipo);
        try {
            int score = Math.round(rating);
            if (score < 1) score = 1;
            if (score > 5) score = 5;
            content.rate(score);
        } catch (Exception ignored) {}

        // Grava no UserService (que por sua vez insere na Symbol Table)
        if (userService != null) {
            try {
                userService.registerContent(content);
            } catch (Exception e) {
                System.out.println("Aviso: Falha ao registar o conteúdo '" + titulo + "' no UserService: " + e.getMessage());
            }
        }
    }

    /**
     * Instancia de forma polimórfica e parametrizada uma variante concreta de {@link Content}
     * correspondente ao tipo textual selecionado.
     */
    private Content criarModeloContent(String titulo, LocalDate releaseDate, int duracao, String tipo) {
        switch (tipo) {
            case "Série":
                return new Series(titulo, releaseDate, duracao, "", duracao, 0);
            case "Documentário":
                return new Documentary(titulo, releaseDate, duracao, "", "", "");
            default:
                return new Movie(titulo, releaseDate, duracao, "", 0, 0);
        }
    }

    /**
     * Efetua a limpeza de todos os campos de texto do formulário de inserção e desmarca
     * qualquer linha selecionada na tabela visual.
     */
    private void limparFormularioLateral() {
        txtTituloContent.clear();
        txtAnoContent.clear();
        txtDuracaoContent.clear();
        if (txtRatingContent != null) txtRatingContent.clear();
        cmbTipoContent.setValue("Filme");
        tblContents.getSelectionModel().clearSelection();
    }

    /**
     * Remove todos os conteúdos mapeados na Symbol Table do serviço, de modo a preparar
     * a estrutura interna para operações de sobreescrita ou importação integral de dados.
     */
    private void limparConteudosDoServico() {
        if (userService == null || userService.getContentST() == null) return;
        List<String> ids = new ArrayList<>();
        for (String id : userService.getContentST().keys()) {
            ids.add(id);
        }
        for (String id : ids) {
            userService.removeContent(id);
        }
    }

    /**
     * Helper interno para configurar e exibir uma caixa de diálogo informativa ou de aviso síncrona.
     *
     * @param tipo     O tipo de alerta do diálogo (Alerta, Aviso, Erro, Informação).
     * @param titulo   O texto de cabeçalho da janela modal.
     * @param mensagem O corpo textual descritivo detalhado exibido ao utilizador.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    /**
     * Mapeia os dados das instâncias ativas no ecossistema e compõe uma listagem leve de objetos
     * de transferência {@link ContentRecord}, gerando um snapshot parcial do catálogo.
     *
     * @return Uma {@link List} contendo os objetos simplificados obtidos na captura.
     */
    public List<ContentRecord> getContentRecordsSnapshot() {
        List<ContentRecord> records = new ArrayList<>();
        if (userService != null && userService.getContentST() != null) {
            for (Content c : userService.getContentST().listAll()) {
                String tipoStr = c instanceof Series ? "Série" : c instanceof Documentary ? "Documentário" : "Filme";
                int ano = c.getReleaseDate() != null ? c.getReleaseDate().getYear() : 2000;
                records.add(new ContentRecord(c.getTitle(), ano, c.getDuration(), tipoStr));
            }
        }
        return records;
    }

    /**
     * Restaura a sessão e reconfigura o estado de persistência do catálogo visual do controlador
     * a partir de uma coleção de registos históricos de instantâneo fornecidos.
     *
     * @param records A listagem de registos {@link ContentRecord} para restauro de sessão.
     */
    public void loadContentRecordsSnapshot(List<ContentRecord> records) {
        limparConteudosDoServico();
        for (ContentRecord record : records) {
            LocalDate dataSnapshot = LocalDate.of(record.getYear(), 1, 1);
            adicionarConteudoTabela(record.getTitle(), dataSnapshot, record.getDuration(), record.getType(), 0.0f);
        }
        atualizarCatalogoVisual();
    }
}