package tests.BST;

import model.artists.Actor;
import model.artists.Artist;
import model.artists.Director;
import service.bst.ArtistBST;

import java.time.LocalDate;
import java.util.List;

/**
 * Casos de teste para a ArtistBST (R3 + R5).
 * Cobre alíneas c) e d) do enunciado.
 *
 * O UUID é gerado automaticamente em Person via UUID.randomUUID().
 * Guardamos sempre a referência ao objeto para usar .getId() no remove.  *
 * Correr com: java -ea tests.BST.ArtistBSTTest
 *
 * @version 1.0
 */
public class ArtistBSTTest {
    public static void runAll() {
        System.out.println("\n--------------------------- ArtistBSTTest ---------------------------");
        testInsert();
        testRemove();
        testFindByBirthDateRange();
        testFindByNationality();
        testFindByNationalityGenderAndBirthRange();
        testFindByNameSubstring();
        testFindByNameSubstringNationalityGenderAndBirthRange();
        testMinMax();
        testSemResultados();
    }
    // ── INSERT / REMOVE ───────────────────────────────────────────────

    /**
     * Verifica que os 4 artistas do buildBST são inseridos corretamente.      * Cada artista tem data de nascimento diferente → 4 chaves na BST.
     */
    public static void testInsert() {
        ArtistBST bst = buildBST();
        assert bst.size() == 4 : "Erro: devia ter 4 chaves";
        System.out.println("testInsert()                                       -> FEITO | " + bst.size() + " chaves");
    }

    /**
     * Verifica que remove elimina o artista da BST pelo UUID.
     * Insere um artista extra, remove-o e verifica que a lista diminuiu.
     */
    public static void testRemove() {
        ArtistBST bst = new ArtistBST();
        Actor a = new Actor(
                "Teste Remover", "M",
                LocalDate.of(1974, 11, 11),
                "US", "CAA", List.of()
        );
        bst.insert(a);
        int before = bst.findByBirthDateRange(
                LocalDate.of(1974, 1, 1),
                LocalDate.of(1974, 12, 31)).size();
        bst.remove(a);
        int after = bst.findByBirthDateRange(
                LocalDate.of(1974, 1, 1),
                LocalDate.of(1974, 12, 31)).size();
        assert after == before - 1 : "Erro: devia ter 1 a menos";
        System.out.println("testRemove()                                       -> FEITO | " + "antes=" + before + " depois=" + after);
    }
    // ── PESQUISAS alínea c) ───────────────────────────────────────────

    /**
     * [c] Verifica pesquisa por faixa etária (intervalo de datas de nascimento).      * DiCaprio (1974) e Nolan (1970) estão entre 1965 e 1980.
     */
    public static void testFindByBirthDateRange() {
        ArtistBST bst = buildBST();
        List<Artist> r = bst.findByBirthDateRange(LocalDate.of(1965, 1, 1), LocalDate.of(1980, 12, 31)
        );
        assert r.size() == 2 : "Erro: devia encontrar 2 entre 1965-1980";
        System.out.println("testFindByBirthDateRange()                         -> FEITO | " + r.size() + " entre 1965-1980:");
        r.forEach(a -> System.out.println(
                "  " + a.getName() + " (" + a.getBirthDate() + ")"));
    }

    /**
     * [c] Verifica pesquisa por nacionalidade.
     * DiCaprio (US) e Gerwig (US) → 2 resultados de US.
     */
    public static void testFindByNationality() {
        ArtistBST bst = buildBST();
        List<Artist> r = bst.findByNationality("US");
        assert r.size() == 2 : "Erro: devia encontrar 2 de US";
        System.out.println("testFindByNationality()                            -> FEITO | " + r.size() + " de US:");
        r.forEach(a -> System.out.println(
                "  " + a.getName() + " [" + a.getNationality() + "]"));
    }

    /**
     * [c] Verifica pesquisa por nacionalidade + género + faixa etária.
     * US + M + nascidos entre 1960-1980 → só DiCaprio.
     */
    public static void testFindByNationalityGenderAndBirthRange() {
        ArtistBST bst = buildBST();
        List<Artist> r = bst.findByNationalityGenderAndBirthRange(
                "US", "M",
                LocalDate.of(1960, 1, 1),
                LocalDate.of(1980, 12, 31)
        );
        assert r.size() == 1 : "Erro: devia encontrar 1 (US/M entre 1960-1980)";
        System.out.println("testFindByNationalityGenderAndBirthRange()         -> FEITO | " + r.size() + " US/M entre 1960-1980:");
        r.forEach(a -> System.out.println(
                "  " + a.getName()
                        + " [" + a.getNationality() + "/" + a.getGender() + "]"));
    }
    // ── PESQUISAS alínea d) ───────────────────────────────────────────

    /**
     * [d] Verifica pesquisa por substring no nome (case-insensitive).
     * "nolan" → Christopher Nolan.
     */
    public static void testFindByNameSubstring() {
        ArtistBST bst = buildBST();
        List<Artist> r = bst.findByNameSubstring("nolan");
        assert r.size() == 1 : "Erro: devia encontrar 1 com 'nolan'";
        System.out.println("testFindByNameSubstring()                          -> FEITO | " + r.size() + " com 'nolan':");
        r.forEach(a -> System.out.println("  " + a.getName()));
    }

    /**
     * [d] Verifica pesquisa por substring + nacionalidade + género + faixa etária.
     * "e" + US + F + 1980-2000 → Greta Gerwig.
     */
    public static void testFindByNameSubstringNationalityGenderAndBirthRange() {
        ArtistBST bst = buildBST();
        List<Artist> r = bst.findByNameSubstringNationalityGenderAndBirthRange(
                "e", "US", "F",
                LocalDate.of(1980, 1, 1),
                LocalDate.of(2000, 12, 31)
        );
        assert r.size() == 1 : "Erro: devia encontrar 1";
        System.out.println("testFindByNameSubstringNationalityGenderAndBirthRange -> FEITO | " + r.size() + " resultado:");
        r.forEach(a -> System.out.println(
                "  " + a.getName()
                        + " [" + a.getNationality() + "/" + a.getGender() + "]"));
    }
    // ── UTILITÁRIOS ───────────────────────────────────────────────────

    /**
     * Verifica que min() e max() devolvem as datas corretas.
     */
    public static void testMinMax() {
        ArtistBST bst = buildBST();
        System.out.println("testMinMax()                                       -> FEITO | min=" + bst.min() + " max=" + bst.max());
    }

    /**
     * Verifica que pesquisa num intervalo sem dados devolve lista vazia.
     */
    public static void testSemResultados() {
        ArtistBST bst = buildBST();
        List<Artist> r = bst.findByBirthDateRange(
                LocalDate.of(1800, 1, 1),
                LocalDate.of(1900, 12, 31)
        );
        assert r.isEmpty() : "Erro: devia devolver lista vazia";
        System.out.println("testSemResultados()                                -> FEITO | lista vazia");
    }
    // ── AUXILIARES ────────────────────────────────────────────────────

    /**
     * Constrói e devolve uma ArtistBST com 4 artistas (2 atores + 2 diretores).      * Cada chamada cria objetos novos com UUIDs novos.
     *
     * @return ArtistBST populada com 4 artistas
     */
    static ArtistBST buildBST() {
        ArtistBST bst = new ArtistBST();
        bst.insert(new Actor(
                "Leonardo DiCaprio", "M", LocalDate.of(1974, 11, 11), "US", "CAA", List.of()
        ));
        bst.insert(new Actor(
                "Cate Blanchett", "F", LocalDate.of(1969, 5, 14), "AU", "WME", List.of()
        ));
        bst.insert(new Director(
                "Christopher Nolan", "M", LocalDate.of(1970, 7, 30), "UK", "Sci-Fi/Thriller", List.of()));
        bst.insert(new Director("Greta Gerwig", "F", LocalDate.of(1983, 8, 4), "US", "Drama/Comedy", List.of()));
        return bst;
    }

    // ── MAIN ──────────────────────────────────────────────────────────
    public static void main(String[] args) {
        runAll();
    }
}