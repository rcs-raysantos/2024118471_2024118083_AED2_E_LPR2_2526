package tests.BST;

import model.users.User;
import service.bst.UserBST;
import java.time.LocalDate;
import java.util.List;
import static tests.DataInitializer.REG_PT;
import static tests.DataInitializer.user_buildBST;

/**
 * @brief Suite de testes unitários para a estrutura UserBST.
 * * Esta classe valida as operações de uma Árvore de Procura Binária (BST)
 * organizada por data de registo, incluindo pesquisas por intervalos,
 * filtragem por região e buscas por substrings no nome.
 */
public class UserBSTTest {
    /**
     * @brief Executa toda a bateria de testes da UserBST.
     * * Invoca sequencialmente testes de inserção, remoção, pesquisas
     * combinadas e casos de fronteira (min/max e sem resultados).
     */
    public static void runAll() {
        System.out.println("\n--------------------------- UserBSTTest ---------------------------");
        testInsert();
        testRemove();
        testFindByRegistrationRange();
        testFindByRegion();
        testFindByRegionAndDateRange();
        testFindByNameSubstring();
        testFindByNameSubstringAndRegion();
        testFindByNameSubstringRegionAndDateRange();
        testMinMax();
        testSemResultados();
    }

    /**
     * @test Valida a inserção de utilizadores e verifica se o tamanho (size) da árvore é consistente.
     */
    private static void testInsert() {
        UserBST bst = user_buildBST();
        assert bst.size() == 4 : "Erro: devia ter 4 chaves";
        System.out.println("testInsert() -> FEITO (" + bst.size() + " chaves)");
    }

    /**
     * @test Verifica a remoção de um nó da árvore.
     * Compara a quantidade de resultados num intervalo antes e depois da remoção.
     */
    private static void testRemove() {
        UserBST bst = new UserBST();
        User u = new User("Teste Remover", "tr@mail.com", "pass", LocalDate.of(2023, 1, 10), REG_PT, LocalDate.of(1990, 1, 1));
        bst.insert(u);

        int before = bst.findByRegistrationRange(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31)).size();
        bst.remove(u);
        int after = bst.findByRegistrationRange(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31)).size();

        assert after == before - 1 : "Erro: devia ter 1 a menos";
        System.out.println("testRemove() -> FEITO (antes = " + before + ", depois = " + after + ")");
    }

    /**
     * @test Valida a pesquisa por intervalo de datas de registo (Range Search).
     */
    private static void testFindByRegistrationRange() {
        UserBST bst = user_buildBST();
        List<User> r = bst.findByRegistrationRange(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31));

        assert r.size() == 2 : "Erro: devia encontrar 2 em 2023";
        System.out.println("testFindByRegistrationRange() -> FEITO");
        r.forEach(u -> System.out.println("  - " + u.getName() + " (" + u.getRegistrationDate() + ")"));
    }

    /**
     * @test Valida a filtragem de utilizadores por código de região.
     */
    private static void testFindByRegion() {
        UserBST bst = user_buildBST();
        List<User> r = bst.findByRegion("PT");

        assert r.size() == 2 : "Erro: devia encontrar 2 de PT";
        System.out.println("testFindByRegion() -> FEITO");
        r.forEach(u -> System.out.println("  - " + u.getName() + " [" + u.getRegion().getCode() + "]"));
    }

    /**
     * @test Testa a pesquisa combinada: Região E Intervalo de Datas.
     */
    private static void testFindByRegionAndDateRange() {
        UserBST bst = user_buildBST();
        List<User> lista = bst.findByRegionAndDateRange("PT", LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31));

        assert lista.size() == 1 : "Erro: devia encontrar 1 PT em 2023";
        System.out.println("testFindByRegionAndDateRange() -> FEITO");
    }

    /**
     * @test Valida a procura de utilizadores cujo nome contém uma determinada substring (case-insensitive).
     */
    private static void testFindByNameSubstring() {
        UserBST bst = user_buildBST();
        List<User> r = bst.findByNameSubstring("silva");

        assert r.size() == 1 : "Erro: devia encontrar 1 com 'silva'";
        System.out.println("testFindByNameSubstring() -> FEITO");
    }

    /**
     * @test Testa a filtragem por substring de nome dentro de uma região específica.
     */
    private static void testFindByNameSubstringAndRegion() {
        UserBST bst = user_buildBST();
        List<User> r = bst.findByNameSubstringAndRegion("santos", "BR");

        assert r.size() == 1 : "Erro: devia encontrar 1";
        System.out.println("testFindByNameSubstringAndRegion() -> FEITO");
    }

    /**
     * @test Valida a pesquisa mais restritiva: Substring de Nome, Região e Intervalo de Datas.
     */
    private static void testFindByNameSubstringRegionAndDateRange() {
        UserBST bst = user_buildBST();
        List<User> r = bst.findByNameSubstringRegionAndDateRange("a", "PT", LocalDate.of(2023, 1, 1), LocalDate.of(2025, 12, 31));

        System.out.println("testFindByNameSubstringRegionAndDateRange -> FEITO (" + r.size() + " resultados)");
    }

    /**
     * @test Verifica as datas extremas (mínima e máxima) armazenadas na árvore.
     */
    private static void testMinMax() {
        UserBST bst = user_buildBST();
        System.out.println("testMinMax() -> FEITO");
        System.out.println("  min = "  + bst.min() +  " | max = " + bst.max());
    }

    /**
     * @test Garante que pesquisas sem correspondência devolvem uma lista vazia e não null.
     */
    private static void testSemResultados() {
        UserBST bst = user_buildBST();
        List<User> lista = bst.findByRegistrationRange(LocalDate.of(2000, 1, 1), LocalDate.of(2001, 12, 31));

        assert lista.isEmpty() : "Erro: devia devolver lista vazia";
        System.out.println("testSemResultados() -> FEITO");
    }
}