package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.artists.Artist;
import model.artists.Actor;
import model.artists.Director;

/**
 * Controlador JavaFX responsável pela gestão da interface de artistas.
 * Permite realizar operações de CRUD (criar, ler, atualizar e remover),
 * filtragem dinâmica, e importação/exportação de dados em ficheiros de texto.
 */
public class ArtistController {

    private final List<Artist> baseDadosArtistas = new ArrayList<>();
    private ObservableList<Artist> obsArtists;

    @FXML private TextField txtFiltroArtistNome;
    @FXML private TextField txtFiltroArtistPais;

    @FXML private TableView<Artist> tblArtists;
    @FXML private TableColumn<Artist, String> colArtistId;
    @FXML private TableColumn<Artist, String> colArtistName;
    @FXML private TableColumn<Artist, String> colArtistCountry;

    @FXML private TextField txtFormArtistNome;
    @FXML private TextField txtFormArtistPais; // Mapeia para Nationality
    @FXML private ComboBox<String> cmbArtistTipo;

    /**
     * Inicializa os componentes da interface gráfica. Configura as opções do ComboBox,
     * vincula as colunas da TableView às propriedades do modelo de dados, carrega os dados
     * iniciais e adiciona um escutador de seleção para o preenchimento automático do formulário.
     */
    @FXML
    public void initialize() {
        obsArtists = FXCollections.observableArrayList();

        // Configurar ComboBox
        cmbArtistTipo.getItems().addAll("Ator / Atriz", "Realizador(a)");
        cmbArtistTipo.setValue("Ator / Atriz");

        // Configurar Colunas (Garante que os teus getters na classe abstrata batem certo)
        colArtistId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        colArtistName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        // Se na classe abstrata o método for getNationality(), mudamos aqui:
        colArtistCountry.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNationality()));

        tblArtists.setItems(obsArtists);
        carregarDadosIniciais();

        // Listener de seleção para edição
        tblArtists.getSelectionModel().selectedItemProperty().addListener((obs, antigo, selecionado) -> {
            if (selecionado != null) {
                preencherFormulario(selecionado);
            }
        });
    }

    /**
     * Alimenta a base de dados em memória com instâncias de teste contendo atores e realizadores,
     * promovendo a renderização inicial da tabela.
     */
    private void carregarDadosIniciais() {
        // Criando instâncias de teste com os teus construtores exatos
        // (Nome, Género, DataNascimento, Nacionalidade, Agência/Estilo, ListaDeConteúdos)
        baseDadosArtistas.add(new Actor("Cillian Murphy", "Masculino", LocalDate.of(1976, 5, 25), "IE", "Universal Agents", new ArrayList<>()));
        baseDadosArtistas.add(new Director("Christopher Nolan", "Masculino", LocalDate.of(1970, 7, 30), "UK", "Não-Linear", new ArrayList<>()));
        baseDadosArtistas.add(new Actor("Bryan Cranston", "Masculino", LocalDate.of(1956, 3, 7), "US", "UTA", new ArrayList<>()));

        atualizarTabela(baseDadosArtistas);
    }

    /**
     * Atualiza os elementos visuais da TableView substituindo o conteúdo atual
     * da lista observável pela lista fornecida por parâmetro.
     *
     * @param lista A nova lista de artistas a ser exibida na tabela.
     */
    private void atualizarTabela(List<Artist> lista) {
        obsArtists.setAll(lista);
    }

    /**
     * Trata o evento de salvamento dos dados do formulário. Caso nenhum artista esteja
     * selecionado na tabela, cria uma nova instância (Actor ou Director) com os construtores
     * padrões; caso contrário, atualiza os dados do registo selecionado.
     */
    @FXML
    public void handleSalvar() {
        String nome = txtFormArtistNome.getText() != null ? txtFormArtistNome.getText().trim() : "";
        String nacionalidade = txtFormArtistPais.getText() != null ? txtFormArtistPais.getText().trim().toUpperCase() : "";
        String tipoOpcao = cmbArtistTipo.getValue();

        if (nome.isEmpty() || nacionalidade.isEmpty() || tipoOpcao == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Vazios", "Preencha o nome, nacionalidade e o tipo de artista.");
            return;
        }

        Artist selecionado = tblArtists.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            // CRIAR NOVO - USANDO OS TEUS CONSTRUTORES EXATOS
            Artist novoArtista;

            // Valores padrão para os campos do construtor que não estão no formulário do FXML
            String generoPadrao = "Não Definido";
            LocalDate dataPadrao = LocalDate.now();

            if (tipoOpcao.equals("Ator / Atriz")) {
                novoArtista = new Actor(nome, generoPadrao, dataPadrao, nacionalidade, "Agência Padrão", new ArrayList<>());
            } else {
                novoArtista = new Director(nome, generoPadrao, dataPadrao, nacionalidade, "Estilo Padrão", new ArrayList<>());
            }

            baseDadosArtistas.add(novoArtista);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Artista adicionado com sucesso!");
        } else {
            // EDITAR ATRIBUTOS EXISTENTES
            selecionado.setName(nome);
            selecionado.setNationality(nacionalidade); // Ajustado para o teu set do modelo
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Dados updated com sucesso!");
        }

        atualizarTabela(baseDadosArtistas);
        limparFormulario();
    }

    /**
     * Trata o evento de remoção de um artista. Remove o objeto selecionado na TableView
     * da lista principal e atualiza a interface. Exibe um aviso se nenhuma linha estiver selecionada.
     */
    @FXML
    public void handleRemover() {
        Artist selecionado = tblArtists.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleção Vazia", "Selecione um artista para remover.");
            return;
        }
        baseDadosArtistas.remove(selecionado);
        atualizarTabela(baseDadosArtistas);
        limparFormulario();
    }

    /**
     * Filtra a coleção de artistas com base nos valores textuais inseridos nos campos de pesquisa
     * por nome e/ou país de origem, atualizando a visualização da tabela.
     */
    @FXML
    public void handleFiltrar() {
        String filtroNome = txtFiltroArtistNome.getText() != null ? txtFiltroArtistNome.getText().trim().toLowerCase() : "";
        String filtroPais = txtFiltroArtistPais.getText() != null ? txtFiltroArtistPais.getText().trim().toUpperCase() : "";

        List<Artist> filtrados = new ArrayList<>();
        for (Artist art : baseDadosArtistas) {
            boolean bateNome = filtroNome.isEmpty() || art.getName().toLowerCase().contains(filtroNome);
            boolean batePais = filtroPais.isEmpty() || art.getNationality().toUpperCase().contains(filtroPais);

            if (bateNome && batePais) {
                filtrados.add(art);
            }
        }
        atualizarTabela(filtrados);
    }

    /**
     * Limpa os campos de texto destinados aos filtros de pesquisa e restaura a listagem completa
     * dos artistas na interface.
     */
    @FXML
    public void handleLimparFiltros() {
        txtFiltroArtistNome.clear();
        txtFiltroArtistPais.clear();
        atualizarTabela(baseDadosArtistas);
    }

    /**
     * Abre uma janela de diálogo para seleção de um ficheiro de texto (.txt), limpa a coleção
     * em memória e faz a importação estruturada do elenco com base nas linhas tokenizadas do ficheiro.
     */
    @FXML
    public void handleImportarDados() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importar Elenco");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ficheiros TXT", "*.txt"));
        File file = fileChooser.showOpenDialog(tblArtists.getScene().getWindow());

        if (file != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String linha;
                baseDadosArtistas.clear();

                while ((linha = br.readLine()) != null) {
                    linha = linha.trim();
                    if (linha.isEmpty()) continue;

                    // Formato esperado no TXT: Nome;Género;Ano-Mes-Dia;Nacionalidade;Especifico;TIPO
                    String[] tokens = linha.split(";");
                    if (tokens.length >= 6) {
                        String name = tokens[0].trim();
                        String gender = tokens[1].trim();
                        LocalDate birthDate = LocalDate.parse(tokens[2].trim()); // Lê no padrão ISO YYYY-MM-DD
                        String nationality = tokens[3].trim().toUpperCase();
                        String especifico = tokens[4].trim(); // Agency ou Style
                        String tipoToken = tokens[5].trim().toUpperCase();

                        if (tipoToken.equals("ACTOR")) {
                            baseDadosArtistas.add(new Actor(name, gender, birthDate, nationality, especifico, new ArrayList<>()));
                        } else if (tipoToken.equals("DIRECTOR")) {
                            baseDadosArtistas.add(new Director(name, gender, birthDate, nationality, especifico, new ArrayList<>()));
                        }
                    }
                }
                atualizarTabela(baseDadosArtistas);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Elenco importado com sucesso!");
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao processar a estrutura do ficheiro.");
            }
        }
    }

    /**
     * Permite a exportação do elenco atual em memória para um ficheiro de texto,
     * gravando os atributos concatenados por ponto e vírgula de forma linear.
     */
    @FXML
    public void handleExportarDados() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exportar Elenco");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ficheiros TXT", "*.txt"));
        fileChooser.setInitialFileName("elenco.txt");
        File file = fileChooser.showSaveDialog(tblArtists.getScene().getWindow());

        if (file != null) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                for (Artist art : baseDadosArtistas) {
                    String tipoSalvar;
                    String campoEspecifico;
                    String genero = (art.getGender() != null) ? art.getGender() : "Não Definido";
                    String dataNasc = (art.getBirthDate() != null) ? art.getBirthDate().toString() : LocalDate.now().toString();

                    if (art instanceof Actor) {
                        tipoSalvar = "ACTOR";
                        campoEspecifico = ((Actor) art).getAgency();
                    } else {
                        tipoSalvar = "DIRECTOR";
                        campoEspecifico = ((Director) art).getStyle();
                    }

                    // escreve seguindo a ordem exata de leitura
                    bw.write(art.getName() + ";" + genero + ";" + dataNasc + ";" + art.getNationality() + ";" + campoEspecifico + ";" + tipoSalvar);
                    bw.newLine();
                }
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Elenco guardado!");
            } catch (IOException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Falha ao gravar o ficheiro.");
            }
        }
    }

    /**
     * Preenche os inputs textuais e seleções de tipo do formulário de detalhe com os
     * atributos de um artista selecionado para modificação.
     *
     * @param art Instância do artista que preencherá os campos do formulário.
     */
    private void preencherFormulario(Artist art) {
        txtFormArtistNome.setText(art.getName());
        txtFormArtistPais.setText(art.getNationality());

        if (art instanceof Actor) {
            cmbArtistTipo.setValue("Ator / Atriz");
        } else {
            cmbArtistTipo.setValue("Realizador(a)");
        }
    }

    /**
     * Restaura os controlos visuais de registo e edição do formulário para o estado vazio/padrão
     * e limpa a seleção em destaque da tabela de visualização.
     */
    private void limparFormulario() {
        tblArtists.getSelectionModel().clearSelection();
        txtFormArtistNome.clear();
        txtFormArtistPais.clear();
        cmbArtistTipo.setValue("Ator / Atriz");
    }

    /**
     * Cria e retorna uma cópia de segurança instantânea (snapshot) da lista de artistas armazenada em memória.
     *
     * @return Uma nova instância de {@link List} contendo os objetos de {@link Artist}.
     */
    public List<Artist> getArtistsSnapshot() {
        return new ArrayList<>(baseDadosArtistas);
    }

    /**
     * Substitui integralmente a base de dados interna por uma lista externa fornecida,
     * reatualizando a visualização de grelha associada e reiniciando o formulário.
     *
     * @param artists Nova lista completa de artistas para carregamento definitivo.
     */
    public void loadArtistsSnapshot(List<Artist> artists) {
        baseDadosArtistas.clear();
        baseDadosArtistas.addAll(artists);
        atualizarTabela(baseDadosArtistas);
        limparFormulario();
    }

    /**
     * Cria, configura e exibe de forma síncrona uma caixa de diálogo de alerta no ecrã.
     *
     * @param tipo   O tipo/severidade do alerta gráfico.
     * @param titulo O título que será exibido no topo da janela do alerta.
     * @param msg    A mensagem descritiva principal exibida no interior do alerta.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}