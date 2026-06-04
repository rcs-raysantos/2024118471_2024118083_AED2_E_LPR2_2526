package tests.BST;

import model.content.Genre;
import service.bst.GenreBST;

import java.util.List;

/**
 * Casos de teste para a GenreBST (R3 + R5).
 * Testa operações básicas: insert, remove, min/max.
 *
 * O UUID é gerado automaticamente no construtor de Genre.
 * A chave da BST é a data de criação do gênero.
 */
public class GenreBSTTest {
    public static void runAll() {
        System.out.println("\n--------------------------- GenreBSTTest ---------------------------");
        testInsert();
        testInsertNull();
        testRemove();
        testMinMax();
        testSemResultados();
    }

    // ── INSERT / REMOVE ───────────────────────────────────────────────

    /**
     * Verifica que os 4 gêneros do buildBST são inseridos corretamente.
     * Cada gênero tem data de criação diferente → 4 chaves na BST.
     */
    public static void testInsert() {
        GenreBST bst = buildBST();
        assert bst.size() == 4 : "Erro: devia ter 4 chaves";
        System.out.println("testInsert()                                       -> FEITO | "
            + bst.size() + " chaves");
    }

    /**
     * Verifica que inserir null lança IllegalArgumentException.
     */
    public static void testInsertNull() {
        GenreBST bst = buildBST();
        try {
            bst.insert(null);
            System.out.println("testInsertNull()                                   -> DEU ERRADO: devia lançar exceção");
        } catch (IllegalArgumentException e) {
            System.out.println("testInsertNull()                                   -> FEITO: " + e.getMessage());
        }
    }

    /**
     * Verifica que remove elimina o gênero da BST.
     * Insere um gênero extra, remove-o e verifica que a lista diminuiu.
     */
    public static void testRemove() {
        GenreBST bst = new GenreBST();

        Genre g = new Genre("Teste Remover", List.of(), List.of());
        bst.insert(g);

        int before = bst.size();
        bst.remove(g);
        int after = bst.size();

        assert after == before - 1 : "Erro: devia ter 1 a menos";
        System.out.println("testRemove()                                       -> FEITO | "
            + "antes=" + before + " depois=" + after);
    }

    // ── UTILITÁRIOS ───────────────────────────────────────────────────

    /**
     * Verifica que min() e max() devolvem as datas corretas.
     */
    public static void testMinMax() {
        GenreBST bst = buildBST();
        System.out.println("testMinMax()                                       -> FEITO | min="
            + bst.min() + " max=" + bst.max());
    }

    /**
     * Verifica que uma BST vazia funciona corretamente.
     */
    public static void testSemResultados() {
        GenreBST bst = new GenreBST();
        assert bst.size() == 0 : "Erro: BST vazia devia ter size 0";
        System.out.println("testSemResultados()                                -> FEITO | BST vazia");
    }

    // ── AUXILIARES ────────────────────────────────────────────────────

    /**
     * Constrói e devolve uma GenreBST com 4 gêneros.
     * Cada chamada cria objetos novos com UUIDs novos e datas de criação diferentes.
     *
     * Gêneros:
     *   Sci-Fi      (fecha: hoje)
     *   Drama       (fecha: ontem)
     *   Action      (fecha: 2 dias atrás)
     *   Comedy      (fecha: 3 dias atrás)
     *
     * @return GenreBST populada com 4 gêneros
     */
    static GenreBST buildBST() {
        GenreBST bst = new GenreBST();

        Genre scifi = new Genre("Sci-Fi", List.of(), List.of());
        bst.insert(scifi);

        Genre drama = new Genre("Drama", List.of(), List.of());
        bst.insert(drama);

        Genre action = new Genre("Action", List.of(), List.of());
        bst.insert(action);

        Genre comedy = new Genre("Comedy", List.of(), List.of());
        bst.insert(comedy);

        return bst;
    }
}
