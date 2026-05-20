package tests;

import model.users.User;
import model.utilities.Region;
import service.bst.UserBST;
import service.st.UserST;

import java.time.LocalDate;

/**
 * Inicializador de dados partilhados entre todos os testes (R5).
 * Todos os campos e métodos são static para serem importados diretamente.
 *
 * Usar com:
 *   import static tests.DataInitializer.*;
 *
 * @version 1.0
 */
public class DataInitializer {

    // ── REGIÕES ───────────────────────────────────────────────────────

    /** Região Portugal */
    public static final Region REG_PT = new Region("PT", "Portugal");
    /** Região Brasil */
    public static final Region REG_BR = new Region("BR", "Brasil");
    /** Região Espanha */
    public static final Region REG_ES = new Region("ES", "Espanha");
    /** Região Estados Unidos */
    public static final Region REG_US = new Region("US", "Estados Unidos");

    // ── UTILIZADORES GLOBAIS ──────────────────────────────────────────
    // Criados uma vez; o UUID é gerado no arranque da JVM.
    // Usar .getId() sempre que precisares da chave — nunca hardcodes.

    /** Utilizador 1 — João Silva, PT, registado em 2023-01-10 */
    public static final User user1 = new User(
            "João Silva",
            "jsilva@gmail.com", "pass123",
            LocalDate.of(2023, 1, 10),
            REG_PT,
            LocalDate.of(1990, 6, 23)
    );

    /** Utilizador 2 — Maria Santos, BR, registada em 2023-06-15 */
    public static final User user2 = new User(
            "Maria Santos",
            "msantos@uol.com.br", "abcd987",
            LocalDate.of(2023, 6, 15),
            REG_BR,
            LocalDate.of(1995, 11, 10)
    );

    /** Utilizador 3 — Carlos Garcia, ES, registado em 2024-03-20 */
    public static final User user3 = new User(
            "Carlos Garcia",
            "carlos.garcia@outlook.es", "pass_es2024",
            LocalDate.of(2024, 3, 20),
            REG_ES,
            LocalDate.of(2002, 3, 5)
    );

    /** Utilizador 4 — Ana Rodrigues, PT, registada em 2024-09-05 */
    public static final User user4 = new User(
            "Ana Rodrigues",
            "arodrigues@sapo.pt", "ana2024",
            LocalDate.of(2024, 9, 5),
            REG_PT,
            LocalDate.of(1988, 4, 12)
    );

    // ── FÁBRICAS ──────────────────────────────────────────────────────

    /**
     * Cria um User novo com UUID gerado automaticamente.
     * Usa este método nos testes em vez de escrever o construtor completo.
     *
     * @param name             nome do utilizador
     * @param email            email
     * @param password         password
     * @param registrationDate data de registo
     * @param region           região
     * @param birthDate        data de nascimento
     * @return novo User com UUID único
     */
    public static User makeUser(String name, String email, String password,
                                LocalDate registrationDate, Region region,
                                LocalDate birthDate) {
        return new User(name, email, password, registrationDate, region, birthDate);
    }

    // ── BUILDERS DE ESTRUTURAS ────────────────────────────────────────

    /**
     * Constrói e devolve uma UserST populada com user1, user2, user3 e user4.
     * Cada chamada cria uma ST nova e independente.
     *
     * @return UserST com 4 utilizadores
     */
    public static UserST user_buildST() {
        UserST st = new UserST();
        // Inserimos cópias para que os testes não partilhem estado
        st.insert(makeUser(
                user1.getName(), user1.getEmail(), user1.getPassword(),
                user1.getRegistrationDate(), user1.getRegion(), user1.getBirthDate()));
        st.insert(makeUser(
                user2.getName(), user2.getEmail(), user2.getPassword(),
                user2.getRegistrationDate(), user2.getRegion(), user2.getBirthDate()));
        st.insert(makeUser(
                user3.getName(), user3.getEmail(), user3.getPassword(),
                user3.getRegistrationDate(), user3.getRegion(), user3.getBirthDate()));
        st.insert(makeUser(
                user4.getName(), user4.getEmail(), user4.getPassword(),
                user4.getRegistrationDate(), user4.getRegion(), user4.getBirthDate()));
        return st;
    }

    /**
     * Constrói e devolve uma UserBST populada com user1, user2, user3 e user4.
     * Cada chamada cria uma BST nova e independente.
     *
     * @return UserBST com 4 utilizadores
     */
    public static UserBST user_buildBST() {
        UserBST bst = new UserBST();
        bst.insert(makeUser(
                user1.getName(), user1.getEmail(), user1.getPassword(),
                user1.getRegistrationDate(), user1.getRegion(), user1.getBirthDate()));
        bst.insert(makeUser(
                user2.getName(), user2.getEmail(), user2.getPassword(),
                user2.getRegistrationDate(), user2.getRegion(), user2.getBirthDate()));
        bst.insert(makeUser(
                user3.getName(), user3.getEmail(), user3.getPassword(),
                user3.getRegistrationDate(), user3.getRegion(), user3.getBirthDate()));
        bst.insert(makeUser(
                user4.getName(), user4.getEmail(), user4.getPassword(),
                user4.getRegistrationDate(), user4.getRegion(), user4.getBirthDate()));
        return bst;
    }
}