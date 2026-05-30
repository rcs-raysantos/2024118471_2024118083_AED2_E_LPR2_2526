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

    private void carregarDadosIniciais() {
        // Criando instâncias de teste com os teus construtores exatos
        // (Nome, Género, DataNascimento, Nacionalidade, Agência/Estilo, ListaDeConteúdos)
        baseDadosArtistas.add(new Actor("Cillian Murphy", "Masculino", LocalDate.of(1976, 5, 25), "IE", "Universal Agents", new ArrayList<>()));
        baseDadosArtistas.add(new Director("Christopher Nolan", "Masculino", LocalDate.of(1970, 7, 30), "UK", "Não-Linear", new ArrayList<>()));
        baseDadosArtistas.add(new Actor("Bryan Cranston", "Masculino", LocalDate.of(1956, 3, 7), "US", "UTA", new ArrayList<>()));

        atualizarTabela(baseDadosArtistas);
    }

    private void atualizarTabela(List<Artist> lista) {
        obsArtists.setAll(lista);
    }

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
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Dados atualizados com sucesso!");
        }

        atualizarTabela(baseDadosArtistas);
        limparFormulario();
    }

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

    @FXML
    public void handleLimparFiltros() {
        txtFiltroArtistNome.clear();
        txtFiltroArtistPais.clear();
        atualizarTabela(baseDadosArtistas);
    }

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

    private void preencherFormulario(Artist art) {
        txtFormArtistNome.setText(art.getName());
        txtFormArtistPais.setText(art.getNationality());

        if (art instanceof Actor) {
            cmbArtistTipo.setValue("Ator / Atriz");
        } else {
            cmbArtistTipo.setValue("Realizador(a)");
        }
    }

    private void limparFormulario() {
        tblArtists.getSelectionModel().clearSelection();
        txtFormArtistNome.clear();
        txtFormArtistPais.clear();
        cmbArtistTipo.setValue("Ator / Atriz");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}