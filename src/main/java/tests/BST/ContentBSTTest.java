package tests.BST;

import model.content.*;
import service.bst.ContentBST;

import java.time.LocalDate;
import java.util.List;

/**
 * Casos de teste para a ContentBST (R3 + R5).
 * Cobre alíneas e), f) e g) do enunciado.
 *
 * O UUID é gerado automaticamente no construtor de Content.
 * Guardamos sempre a referência ao objeto para usar .getId() no remove.  * Usa getDuration() (não getDurationMinutes()) conforme a tua implementação.  *
 * Correr com: java -ea tests.BST.ContentBSTTest
 *
 * @version 1.0
 */

public class ContentBSTTest {
    public static void runAll() {
        System.out.println("\n── ContentBSTTest ──────────────────────");         testInsert();
        testInsertNull();
        testRemove();
        testFindByDateRange();
        testFindByType();
        testFindByGenre();
        testFindByTypeGenreAndDateRange();
        testFindByTitleSubstring();
        testFindByTitleSubstringTypeGenreAndDateRange();
        testFindByDurationRange();
        testMinMax();
        testSemResultados();
    }
    // ── INSERT / REMOVE ───────────────────────────────────────────────
    /**
     * Verifica que os 4 conteúdos do buildBST são inseridos corretamente.
     * Cada conteúdo tem data diferente → 4 chaves na BST.
     */
    public static void testInsert() {
        ContentBST bst = buildBST();
        assert bst.size() == 4 : "Erro: devia ter 4 chaves";
        System.out.println("testInsert()                                       -> FEITO | "
            + bst.size() + " chaves");
    }
    /**
     * Verifica que inserir null lança IllegalArgumentException.
     */
    public static void testInsertNull() {
        ContentBST bst = buildBST();
        try {
            bst.insert(null);
            System.out.println("testInsertNull()                                   -> DEU ERRADO: devia lançar exceção");         } catch (IllegalArgumentException e) {
            System.out.println("testInsertNull()                                   -> FEITO: " + e.getMessage());         }
    }
    /**
     * Verifica que remove elimina o conteúdo da BST.
     * Insere um conteúdo extra, remove-o e verifica que a lista diminuiu.      */
    public static void testRemove() {
        ContentBST bst = new ContentBST();
        Movie m = new Movie("Teste Remover", LocalDate.of(2010, 7, 16), 148, "Sinopse de teste.", 154, 2542);
        bst.insert(m);
        int before = bst.findByDateRange(LocalDate.of(2010, 1, 1), LocalDate.of(2010, 12, 31)).size();         bst.remove(m);
        int after = bst.findByDateRange(LocalDate.of(2010, 1, 1), LocalDate.of(2010, 12, 31)).size();
        assert after == before - 1 : "Erro: devia ter 1 a menos";
        System.out.println("testRemove() -> FEITO | " + "antes=" + before + " depois=" + after);
    }
    // ── PESQUISAS alínea e) ───────────────────────────────────────────
    /**
     * [e] Verifica pesquisa por intervalo de datas de lançamento.
     * Inception (2010) e Breaking Bad (2008) estão entre 2005 e 2015.
     */
    public static void testFindByDateRange() {
        ContentBST bst = buildBST();
        List<Content> r = bst.findByDateRange(
            LocalDate.of(2005, 1, 1),
            LocalDate.of(2015, 12, 31)
        );
        assert r.size() == 3 : "Erro: devia encontrar 3 entre 2005-2015";
        System.out.println("testFindByDateRange() -> FEITO | " + r.size() + " entre 2005-2015:");
        r.forEach(c -> System.out.println("  " + c.getTitle() + " (" + c.getReleaseDate() + ")"));
    }
    /**
     * [e] Verifica pesquisa por tipo de conteúdo.
     * buildBST tem 2 filmes, 1 série e 1 documentário.
     */
    public static void testFindByType() {
        ContentBST bst = buildBST();
        List<Content> movies = bst.findByType(Movie.class);
        List<Content> series = bst.findByType(Series.class);
        List<Content> docs   = bst.findByType(Documentary.class);
        assert movies.size() == 2 : "Erro: devia ter 2 filmes";
        assert series.size() == 1 : "Erro: devia ter 1 série";
        assert docs.size()   == 1 : "Erro: devia ter 1 documentário";
        System.out.println("testFindByType() -> FEITO | " + movies.size() + " filmes, " + series.size() + " séries, " + docs.size() + " documentários");
    }
    /**
     * [e] Verifica pesquisa por género.
     * Inception e Interstellar têm género "Sci-Fi" → 2 resultados.
     */
    public static void testFindByGenre() {
        ContentBST bst = buildBST();
        List<Content> r = bst.findByGenre("Sci-Fi");
        assert r.size() == 2 : "Erro: devia encontrar 2 Sci-Fi";
        System.out.println("testFindByGenre() -> FEITO | " + r.size() + " Sci-Fi:");
        r.forEach(c -> System.out.println("  " + c.getTitle()));
    }
    /**
     * [e] Verifica pesquisa combinada: tipo + género + intervalo de datas.
     * Movie + Sci-Fi + 2005-2020 → Inception (2010) e Interstellar (2014).
     */
    public static void testFindByTypeGenreAndDateRange() {
        ContentBST bst = buildBST();
        List<Content> r = bst.findByTypeGenreAndDateRange(
            Movie.class, "Sci-Fi",
            LocalDate.of(2005, 1, 1),
            LocalDate.of(2020, 12, 31)
        );
        assert r.size() == 2 : "Erro: devia encontrar 2 Movie/Sci-Fi entre 2005-2020";
        System.out.println("testFindByTypeGenreAndDateRange()  -> FEITO | " + r.size() + " Movie/Sci-Fi entre 2005-2020:");
        r.forEach(c -> System.out.println("  " + c.getTitle()));
    }
    // ── PESQUISAS alínea f) ───────────────────────────────────────────
    /**
     * [f] Verifica pesquisa por substring no título (case-insensitive).      * "inter" → Interstellar.
     */
    public static void testFindByTitleSubstring() {
        ContentBST bst = buildBST();
        List<Content> r = bst.findByTitleSubstring("inter");
        assert r.size() == 1 : "Erro: devia encontrar 1 com 'inter'";
        System.out.println("testFindByTitleSubstring() -> FEITO | " + r.size() + " com 'inter':");
        r.forEach(c -> System.out.println("  " + c.getTitle()));
    }
    /**
     * [f] Verifica pesquisa combinada: substring + tipo + género + datas.
     * "bad" + Series + Drama + 2000-2020 → Breaking Bad.
     */
    public static void testFindByTitleSubstringTypeGenreAndDateRange() {
        ContentBST bst = buildBST();
        List<Content> r = bst.findByTitleSubstringTypeGenreAndDateRange("bad", Series.class, "Drama", LocalDate.of(2000, 1, 1), LocalDate.of(2020, 12, 31));
        assert r.size() == 1 : "Erro: devia encontrar 1";
        System.out.println("testFindByTitleSubstringTypeGenreAndDateRange() -> FEITO | " + r.size() + " resultado:");
        r.forEach(c -> System.out.println("  " + c.getTitle() + " (" + c.getClass().getSimpleName() + ")"));
    }
    // ── PESQUISAS alínea g) ───────────────────────────────────────────
    /**
     * [g] Verifica pesquisa por intervalo de duração.
     * 140-170 min → Inception (148) e Interstellar (169).
     */
    public static void testFindByDurationRange() {
        ContentBST bst = buildBST();
        List<Content> r = bst.findByDurationRange(140, 170);
        assert r.size() == 2 : "Erro: devia encontrar 2 entre 140-170 min";
        System.out.println("testFindByDurationRange() -> FEITO | " + r.size() + " entre 140-170 min:");
        r.forEach(c -> System.out.println("  " + c.getTitle() + " (" + c.getDuration() + " min)"));
    }
    // ── UTILITÁRIOS ───────────────────────────────────────────────────
    /**
     * Verifica que min() e max() devolvem as datas corretas.
     */
    public static void testMinMax() {
        ContentBST bst = buildBST();
        System.out.println("testMinMax()                                       -> FEITO | min="             + bst.min() + " max=" + bst.max());
    }
    /**
     * Verifica que pesquisa num intervalo sem dados devolve lista vazia.
     */
    public static void testSemResultados() {
        ContentBST bst = buildBST();
        List<Content> r = bst.findByDateRange(
            LocalDate.of(1900, 1, 1),
            LocalDate.of(1950, 12, 31)
        );
        assert r.isEmpty() : "Erro: devia devolver lista vazia";
        System.out.println("testSemResultados() -> FEITO | lista vazia");
    }

    // ── AUXILIARES ────────────────────────────────────────────────────
    /**
     * Constrói e devolve uma ContentBST com 4 conteúdos de tipos e géneros variados.      * Cada chamada cria objetos novos com UUIDs novos.
     *
     * Conteúdos:
     *   Inception       (Movie,       Sci-Fi, 148 min, 2010)      *   Interstellar    (Movie,       Sci-Fi, 169 min, 2014)      *   Breaking Bad    (Series,      Drama,   60 min, 2008)      *   Planet Earth II (Documentary, Nature,  60 min, 2016)
     *
     * @return ContentBST populada com 4 conteúdos
     */
    static ContentBST buildBST() {
        ContentBST bst = new ContentBST();
        Movie inception = new Movie(
            "Inception",
            LocalDate.of(2010, 7, 16),
            148, "Um ladrão que rouba segredos através dos sonhos.",
                2323, 1
        );

        Movie interstellar = new Movie(
            "Interstellar",
            LocalDate.of(2014, 11, 7),
            169, "Uma equipa viaja por um buraco de minhoca.",
                6452, 67
        );

        Series breakingBad = new Series(
            "Breaking Bad",
            LocalDate.of(2008, 1, 20),
            60, "Um professor de química torna-se traficante.",
                6, 25
        );

        Documentary planetEarth = new Documentary(
            "Planet Earth II",
            LocalDate.of(2016, 11, 6),
            60, "Documentário sobre a vida selvagem.",
                "nature", "john cena"
        );

        bst.insert(inception);
        bst.insert(interstellar);
        bst.insert(breakingBad);
        bst.insert(planetEarth);
        return bst;
    }

    // ── MAIN ──────────────────────────────────────────────────────────
    public static void main(String[] args) {     
        runAll();
    } }