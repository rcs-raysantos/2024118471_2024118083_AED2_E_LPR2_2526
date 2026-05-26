package javafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.users.User;
import model.utilities.Region;
import service.bst.UserBST;
import service.st.UserST;
import tests.DataInitializer;

import java.time.LocalDate;
import java.util.List;

public class UserController {

    // Componentes da Interface (Injetados pelo FXML)
    @FXML private TextField txtFiltroNome, txtFiltroRegiao, txtFormNome, txtFormEmail, txtFormRegiao;
    @FXML private PasswordField txtFormSenha;
    @FXML private DatePicker dpInicio, dpFim, dpFormNascimento;
    @FXML private TableView<User> tblUsers;
    @FXML private TableColumn<String, User> colId, colNome, colEmail, colRegiao, colRegisto;

    // As tuas estruturas lógicas
    private UserST userST;
    private UserBST userBST;
    private ObservableList<User> obsUsers;

    /**
     * Método executado automaticamente quando a tela é carregada.
     */
    @FXML
    public void initialize() {
        // 1. Inicializar as tuas tabelas/árvores (reutilizando o teu DataInitializer)
        userST = DataInitializer.user_buildST();
        userBST = DataInitializer.user_buildBST();

        // 2. Configurar as colunas da Tabela para lerem os atributos do User
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRegiao.setCellValueFactory(new PropertyValueFactory<>("region")); // O JavaFX chamará o toString() da Região
        colRegisto.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));

        // 3. Carregar os dados iniciais na Tabela
        atualizarTabela(userST.listAll());

        // 4. Ouvinte de seleção: Quando o user clica numa linha da tabela, preenche o formulário
        tblUsers.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                preencherFormulario(newSelection);
            }
        });
    }

    /**
     * CENÁRIO 1: Atualiza a TableView com qualquer lista que venha do motor lógico.
     */
    private void atualizarTabela(List<User> lista) {
        obsUsers = FXCollections.observableArrayList(lista);
        tblUsers.setItems(obsUsers);
    }

    /**
     * CENÁRIO 2: Aplica os filtros avançados usando os métodos da tua UserBST.
     */
    @FXML
    private void handleFiltrar() {
        String substring = txtFiltroNome.getText().trim();
        String regiao = txtFiltroRegiao.getText().trim().toUpperCase();
        LocalDate inicio = dpInicio.getValue();
        LocalDate fM = dpFim.getValue();

        // Valores por defeito se as datas estiverem vazias
        if (inicio == null) inicio = LocalDate.of(2000, 1, 1);
        if (fM == null) fM = LocalDate.now().plusYears(10); // cobre o futuro (2026+)

        List<User> resultados;

        // Escolhe o método da tua BST com base nos filtros preenchidos
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

    /**
     * CENÁRIO 3: Operações CRUD (Salvar / Inserir / Editar)
     */
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

                // Nota: Numa BST real, para atualizar uma chave (data), remove-se e reinsere-se o nó
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