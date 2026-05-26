package javafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.users.User;
import model.utilities.Region;
import service.bst.UserBST;
import service.st.UserST;
import tests.DataInitializer;

import java.time.LocalDate;
import java.util.List;

public class UserController {

    @FXML private TextField txtFiltroNome, txtFiltroRegiao, txtFormNome, txtFormEmail, txtFormRegiao;
    @FXML private PasswordField txtFormSenha;
    @FXML private DatePicker dpInicio, dpFim, dpFormNascimento;
    @FXML private TableView<User> tblUsers; // Primeiro, garante que a tabela é de <User>
    @FXML private TableColumn<User, String> colId;
    @FXML private TableColumn<User, String> colNome;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, Region> colRegiao;
    @FXML private TableColumn<User, LocalDate> colRegisto;

    // As tuas estruturas lógicas
    private UserST userST;
    private UserBST userBST;
    private ObservableList<User> obsUsers;

    @FXML
    public void initialize() {
        // 1. Inicializar as tuas tabelas/árvores (reutilizando o teu DataInitializer)
        userST = DataInitializer.user_buildST();
        userBST = DataInitializer.user_buildBST();

        // 2. Configurar as colunas da Tabela para lerem os atributos do User
        colId.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getId()));
        colNome.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        colEmail.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
        colRegiao.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getRegion()));
        colRegisto.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getRegistrationDate()));

        atualizarTabela(userST.listAll());

        tblUsers.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                preencherFormulario(newSelection);
            }
        });
    }

    private void atualizarTabela(List<User> lista) {
        obsUsers = FXCollections.observableArrayList(lista);
        tblUsers.setItems(obsUsers);
    }

    @FXML
    private void handleFiltrar() {
        String substring = txtFiltroNome.getText().trim();
        String regiao = txtFiltroRegiao.getText().trim().toUpperCase();
        LocalDate inicio = dpInicio.getValue();
        LocalDate fM = dpFim.getValue();

        if (inicio == null) inicio = LocalDate.of(2000, 1, 1);
        if (fM == null) fM = LocalDate.now().plusYears(10); // cobre o futuro (2026+)

        List<User> resultados;

        if (!substring.isEmpty() && !regiao.isEmpty()) {
            resultados = userBST.findByNameSubstringRegionAndDateRange(substring, regiao, inicio, fM);
        } else if (!regiao.isEmpty()) {
            resultados = userBST.findByRegionAndDateRange(regiao, inicio, fM);
        } else {
            resultados = userBST.findByRegistrationRange(inicio, fM);
        }

        atualizarTabela(resultados);
    }

    @FXML
    private void handleLimparFiltros() {
        txtFiltroNome.clear();
        txtFiltroRegiao.clear();
        dpInicio.setValue(null);
        dpFim.setValue(null);
        atualizarTabela(userST.listAll()); // Volta a mostrar tudo
    }

    @FXML
    private void handleSalvar() {
        try {
            String nome = txtFormNome.getText();
            String email = txtFormEmail.getText();
            String senha = txtFormSenha.getText();
            String codRegiao = txtFormRegiao.getText().toUpperCase();
            LocalDate nascimento = dpFormNascimento.getValue();

            // Validação simples de UI
            if (nome.isEmpty() || email.isEmpty() || codRegiao.isEmpty() || nascimento == null) {
                mostrarAlerta("Erro", "Campos Obrigatórios", "Por favor, preencha todos os campos.", Alert.AlertType.WARNING);
                return;
            }

            Region regiao = new Region(codRegiao, codRegiao.equals("PT") ? "Portugal" : "Outro");
            User selecionado = tblUsers.getSelectionModel().getSelectedItem();

            if (selecionado == null) {
                // Modo: INSERIR NOVO
                User novo = new User(nome, email, senha, LocalDate.now(), regiao, nascimento);
                userST.insert(novo);
                userBST.insert(novo);
                mostrarAlerta("Sucesso", "Utilizador Criado", "Inserido com sucesso!", Alert.AlertType.INFORMATION);
            } else {
                // Modo: EDITAR EXISTENTE
                User editado = new User(nome, email, senha, selecionado.getRegistrationDate(), regiao, nascimento);
                userST.edit(selecionado.getId(), editado);

                userBST.remove(selecionado);
                userBST.insert(editado);
                mostrarAlerta("Sucesso", "Utilizador Atualizado", "Dados editados com sucesso!", Alert.AlertType.INFORMATION);
            }

            limparFormulario();
            atualizarTabela(userST.listAll()); // Atualiza UI

        } catch (IllegalArgumentException e) {
            mostrarAlerta("Erro Lógico", "Falha na Operação", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleRemover() {
        User selecionado = tblUsers.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta("Aviso", "Nenhum Utilizador Selecionado", "Selecione alguém na tabela para remover.", Alert.AlertType.WARNING);
            return;
        }

        try {
            userST.remove(selecionado.getId());
            userBST.remove(selecionado);

            mostrarAlerta("Sucesso", "Utilizador Removido", "Removido de ambas as estruturas.", Alert.AlertType.INFORMATION);
            limparFormulario();
            atualizarTabela(userST.listAll());
        } catch (IllegalArgumentException e) {
            mostrarAlerta("Erro", "Não foi possível remover", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Métodos Auxiliares de UI
    private void preencherFormulario(User u) {
        txtFormNome.setText(u.getName());
        txtFormEmail.setText(u.getEmail());
        txtFormSenha.setText(u.getPassword());
        txtFormRegiao.setText(u.getRegion().getCode());
        dpFormNascimento.setValue(u.getBirthDate());
    }

    private void limparFormulario() {
        txtFormNome.clear();
        txtFormEmail.clear();
        txtFormSenha.clear();
        txtFormRegiao.clear();
        dpFormNascimento.setValue(null);
        tblUsers.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String cabecalho, String conteudo, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecalho);
        alert.setContentText(conteudo);
        alert.showAndWait();
    }
}