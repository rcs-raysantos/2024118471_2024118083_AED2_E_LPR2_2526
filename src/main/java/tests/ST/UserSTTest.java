package tests.ST;

import model.users.User;
import model.utilities.Region;
import service.st.UserST;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static tests.DataInitializer.*;

/**
 * @brief Classe de testes unitários para a UserST.
 * * Esta classe contém uma bateria de testes exaustiva para validar o comportamento
 * da Tabela de Símbolos de Utilizadores (UserST). São testados casos de sucesso,
 * inserções duplicadas, tratamento de nulos e integridade de dados após edições e remoções.
 */
public class UserSTTest {

    /**
     * @brief Executa todos os testes unitários da UserST.
     * * Organiza a execução sequencial de todos os métodos de teste para garantir
     * que a estrutura de dados mantém a consistência em diferentes cenários.
     */
    public static void runAll() {
        System.out.println("\n--------------------------- UserSTTest ---------------------------");
        testInsert();
        testInsertDuplicado();
        testInsertNull();
        testGet();
        testGetInexistente();
        testEdit();
        testEditInexistente();
        testRemove();
        testRemoveInexistente();
        testListAll();
        testListAllVazia();
    }

    // --------------------------- INSERT ---------------------------

    /**
     * @test Valida a inserção de utilizadores de diferentes regiões.
     * Verifica se o tamanho da ST aumenta corretamente e se os IDs gerados são armazenados.
     */
    private static void testInsert() {
        UserST st = new UserST();

        LocalDate res_user1 = LocalDate.of(2023, 1, 10);
        Region reg_user1 = new Region("PT", "Portugal");
        LocalDate birthday_user1 = LocalDate.of(1990, 6, 23);
        User user1 = new User("João Silva", "jjsilva@gmail.com", "1234", res_user1, reg_user1, birthday_user1);
        st.insert(user1);

        LocalDate res_user2 = LocalDate.of(2024, 2, 15);
        Region reg_user2 = new Region("BR", "Brasil");
        LocalDate birthday_user2 = LocalDate.of(1995, 11, 10);
        User user2 = new User("Maria Santos", "msantos@uol.com.br", "abcd987", res_user2, reg_user2, birthday_user2);
        st.insert(user2);

        LocalDate res_user3 = LocalDate.of(2025, 12, 1);
        Region reg_user3 = new Region("ES", "Espanha");
        LocalDate birthday_user3 = LocalDate.of(2002, 3, 5);
        User user3 = new User("Carlos Garcia", "carlos.garcia@outlook.es", "pass_es2024", res_user3, reg_user3, birthday_user3);
        st.insert(user3);

        assert st.size() == 3 : "Erro: size deveria ser 3";
        assert st.contains(user1.getId()) : "Erro: user1 não encontrado";
        assert st.contains(user2.getId()) : "Erro: user2 não encontrado";
        assert st.contains(user3.getId()) : "Erro: user3 não encontrado";

        System.out.println("testInsert() -> FEITO");
    }

    /**
     * @test Garante que o sistema impede a inserção do mesmo objeto utilizador duas vezes.
     */
    private static void testInsertDuplicado() {
        UserST st = new UserST();
        User dup = makeUser("João Silva", "jsilva@mail.com", "123", LocalDate.of(2023, 1, 10), REG_PT, LocalDate.of(1990, 6, 23));
        st.insert(dup);

        try {
            st.insert(dup);
            System.out.println("testInsertDuplicado() -> DEU ERRADO: devia lançar exceção");
        } catch (IllegalArgumentException e) {
            System.out.println("testInsertDuplicado() -> FEITO: " + e.getMessage());
        }
    }

    /**
     * @test Verifica se a inserção de um valor nulo lança IllegalArgumentException.
     */
    private static void testInsertNull() {
        UserST st = user_buildST();
        try {
            st.insert(null);
            System.out.println("testInsertNull() -> DEU ERRADO: devia lançar exceção");
        } catch (IllegalArgumentException e) {
            System.out.println("testInsertNull() -> FEITO: " + e.getMessage());
        }
    }

    // --------------------------- GET ---------------------------

    /**
     * @test Valida a recuperação de um utilizador existente através do seu ID.
     */
    private static void testGet() {
        UserST st = user_buildST();
        User found = st.get(user1.getId());
        assert found != null : "Erro: deveria encontrar o user";
        assert found.getName().equals(user1.getName()) : "Erro: o nome deveria ser = «João Silva»";
        System.out.println("testGet() -> FEITO");
    }

    /**
     * @test Verifica se a procura por um ID gerado aleatoriamente (inexistente) retorna null.
     */
    private static void testGetInexistente() {
        UserST st = user_buildST();
        String id_null = UUID.randomUUID().toString();
        assert st.get(id_null) == null : "Erro: devia devolver «null» para um UUID não registrado";
        System.out.println("testGetInexistente() -> FEITO");
    }

    // --------------------------- EDIT ---------------------------

    /**
     * @test Valida a edição (substituição) de um utilizador mantendo o ID original.
     */
    private static void testEdit() {
        UserST st = new UserST();
        User original = makeUser("João Silva", "blabla@mail.com", "1234", LocalDate.of(2026, 1, 10), REG_PT, LocalDate.of(2025, 6, 07));
        st.insert(original);

        User edited = makeUser("Guilherme Ferreira", user1.getEmail(), user1.getPassword(), user1.getRegistrationDate(), user1.getRegion(), user1.getBirthDate());
        st.edit(original.getId(), edited);

        assert st.get(original.getId()).getName().equals(edited.getName()) : "Erro: nome não foi atualizado";
        System.out.println("testEdit() -> FEITO");
    }

    /**
     * @test Verifica o comportamento defensivo do método edit com IDs falsos ou objetos nulos.
     */
    private static void testEditInexistente() {
        UserST st = new UserST();
        User user = makeUser("aaa", "aaa@mail.com", "aaa", LocalDate.of(2026, 1, 10), REG_PT, LocalDate.of(2025, 6, 07));
        st.insert(user);

        try {
            st.edit(user.getId(), null);
            System.out.println("testEditInexistente() -> DEU ERRADO: deveria dar uma exceção");
        } catch (IllegalArgumentException e) {
            System.out.println("testEditInexistente() -> FEITO com «null»: " + e.getMessage());
        }

        String fake_id = UUID.randomUUID().toString();
        try {
            st.edit(fake_id, user);
            System.out.println("testEditInexistente() -> DEU ERRADO: deveria dar uma exceção");
        } catch (IllegalArgumentException e) {
            System.out.println("testEditInexistente() -> FEITO com id falso: " + e.getMessage());
        }
    }

    // --------------------------- REMOVE ---------------------------

    /**
     * @test Valida a remoção de utilizadores e verifica se o tamanho da ST é atualizado corretamente.
     */
    private static void testRemove() {
        UserST st = new UserST();
        User u1 = new User("Maria Santos", "msantos@uol.com.br", "abcd987", LocalDate.of(2024, 2, 15), REG_BR, LocalDate.of(1995, 11, 10));
        st.insert(u1);
        User u2 = new User("Carlos Garcia", "carlos.garcia@outlook.es", "pass_es2024", LocalDate.of(2025, 12, 1), REG_ES, LocalDate.of(2002, 3, 5));
        st.insert(u2);

        st.remove(u1.getId());

        assert !st.contains(u1.getId()) : "Erro: utilizador devia ter sido removido";
        assert st.size() == 1 : "Erro: size devia ser 1"; // Note: size será 1 após remover 1 de 2

        System.out.println("testRemove() -> FEITO");
    }

    /**
     * @test Garante que a remoção de IDs nulos ou inexistentes lança exceções controladas.
     */
    private static void testRemoveInexistente() {
        UserST st = new UserST();
        try {
            st.remove(null);
            System.out.println("testRemoveInexistente() -> DEU ERRO: era para dar uma exceção");
        } catch (IllegalArgumentException e) {
            System.out.println("testRemoveInexistente() -> FEITO com «null»: " + e.getMessage());
        }

        String fake_id = UUID.randomUUID().toString();
        try {
            st.remove(fake_id);
            System.out.println("testRemoveInexistente() -> DEU ERRO: era para dar uma exceção");
        } catch (IllegalArgumentException e) {
            System.out.println("testRemoveInexistente() -> FEITO com id falso: " + e.getMessage());
        }
    }

    // --------------------------- LIST ---------------------------

    /**
     * @test Verifica se a listagem completa retorna todos os utilizadores inseridos e imprime os dados.
     */
    private static void testListAll() {
        UserST st = user_buildST();
        List<User> lista = st.listAll();
        assert lista.size() == 4 : "Erro: size deveria ser 4";

        lista.forEach(u -> System.out.println("ID: " + u.getId() + " | USER: " + u.getName() + " | REGIÃO: " + u.getRegion().getCode()));
        System.out.println("testListAll() -> FEITO");
    }

    /**
     * @test Garante que uma ST recém-criada retorna uma lista vazia.
     */
    private static void testListAllVazia() {
        UserST st = new UserST();
        List<User> lista = st.listAll();
        assert lista.isEmpty() : "Erro: a lista deve ser vazia";
        System.out.println("testListAllVazia() -> FEITO");
    }
}