package tests.ST;

import model.content.Genre;
import service.st.GenreST;

import java.util.List;
import java.util.UUID;

public class GenreSTTest {
    public static void runAll() {
        System.out.println("\n--------------------------- GenreSTTest ---------------------------");
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

    private static void testInsert() {
        GenreST st = new GenreST();

        Genre scifi = new Genre("Sci-Fi", List.of(), List.of());
        st.insert(scifi);

        Genre drama = new Genre("Drama", List.of(), List.of());
        st.insert(drama);

        Genre action = new Genre("Action", List.of(), List.of());
        st.insert(action);

        assert st.size() == 3 : "Erro: size deveria ser 3";
        assert st.contains(scifi.getId()) : "Erro: Sci-Fi não encontrado";
        assert st.contains(drama.getId()) : "Erro: Drama não encontrado";
        assert st.contains(action.getId()) : "Erro: Action não encontrado";

        System.out.println("testInsert() -> FEITO");
    }

    private static void testInsertDuplicado() {
        GenreST st = new GenreST();

        Genre dup = new Genre("Comedy", List.of(), List.of());
        st.insert(dup);

        try {
            st.insert(dup); // mesmo objeto = mesmo UUID = duplicado
            System.out.println("testInsertDuplicado() -> DEU ERRADO: devia lançar exceção");
        } catch (IllegalArgumentException e) {
            System.out.println("testInsertDuplicado() -> FEITO: " + e.getMessage());
        }
    }

    private static void testInsertNull() {
        GenreST st = buildST();

        try {
            st.insert(null);
            System.out.println("testInsertNull() -> DEU ERRADO: devia lançar exceção");
        } catch (IllegalArgumentException e) {
            System.out.println("testInsertNull() -> FEITO: " + e.getMessage());
        }
    }

    // --------------------------- GET ---------------------------

    private static void testGet() {
        GenreST st = buildST();
        
        List<Genre> genres = st.listAll();
        Genre first = genres.get(0);

        Genre found = st.get(first.getId());
        assert found != null : "Erro: deveria encontrar o gênero";
        assert found.getName().equals(first.getName()) : "Erro: o nome deveria ser igual";

        System.out.println("testGet() -> FEITO");
    }

    private static void testGetInexistente() {
        GenreST st = buildST();
        String id_null = UUID.randomUUID().toString(); // um id que não existe na lista

        assert st.get(id_null) == null : "Erro: devia devolver «null» para um UUID não registrado";

        System.out.println("testGetInexistente() -> FEITO");
    }

    // --------------------------- EDIT ---------------------------

    private static void testEdit() {
        GenreST st = new GenreST();

        Genre original = new Genre("Horror", List.of(), List.of());
        st.insert(original);

        Genre edited = new Genre("Thriller", List.of(), List.of());
        st.edit(original.getId(), edited);

        assert st.get(original.getId()).getName().equals(edited.getName()) : "Erro: nome não foi atualizado";

        System.out.println("testEdit() -> FEITO");
    }

    private static void testEditInexistente() {
        GenreST st = new GenreST();
        Genre genre = new Genre("Fantasy", List.of(), List.of());
        st.insert(genre);

        try {
            st.edit(genre.getId(), null);
            System.out.println("testEditInexistente() -> DEU ERRADO: deveria dar uma exceção");
        }
        catch (IllegalArgumentException e) {
            System.out.println("testEditInexistente() -> FEITO com «null»: " + e.getMessage());
        }

        String fake_id = UUID.randomUUID().toString();

        try {
            st.edit(fake_id, genre);
            System.out.println("testEditInexistente() -> DEU ERRADO: deveria dar uma exceção");
        }
        catch (IllegalArgumentException e) {
            System.out.println("testEditInexistente() -> FEITO com id falso: " + e.getMessage());
        }
    }

    // --------------------------- REMOVE ---------------------------

    private static void testRemove() {
        GenreST st = new GenreST();

        Genre genre1 = new Genre("Romance", List.of(), List.of());
        st.insert(genre1);

        Genre genre2 = new Genre("Adventure", List.of(), List.of());
        st.insert(genre2);

        st.remove(genre1.getId());

        assert !st.contains(genre1.getId()) : "Erro: gênero devia ter sido removido";
        assert st.size() == 2 : "Erro: size devia ser 2";

        System.out.println("testRemove() -> FEITO");
    }

    private static void testRemoveInexistente() {
        GenreST st = new GenreST();

        try {
            st.remove(null);
            System.out.println("testRemoveInexistente() -> DEU ERRO: era para dar uma exceção");
        } catch (IllegalArgumentException e) {
            System.out.println("testRemoveInexistente() -> FEITO com «null»: " + e.getMessage());
        }

        String fake_id = UUID.randomUUID().toString(); // id que não existe

        try {
            st.remove(fake_id);
            System.out.println("testRemoveInexistente() -> DEU ERRO: era para dar uma exceção");
        } catch (IllegalArgumentException e) {
            System.out.println("testRemoveInexistente() -> FEITO com id falso: " + e.getMessage());
        }
    }

    // --------------------------- LIST ---------------------------

    private static void testListAll() {
        GenreST st = buildST();

        List<Genre> lista = st.listAll();
        assert lista.size() == 4 : "Erro: size deveria ser 4";

        lista.forEach(g -> System.out.println("ID: " + g.getId() + " | GENRE: " + g.getName()));
        System.out.println("testListAll() -> FEITO");
    }

    private static void testListAllVazia() {
        GenreST st = new GenreST();

        List<Genre> lista = st.listAll();

        assert lista.isEmpty() : "Erro: a lista deve ser vazia";
        System.out.println("testListAllVazia() -> FEITO");
    }

    // --------------------------- AUXILIARES ---------------------------

    static GenreST buildST() {
        GenreST st = new GenreST();

        Genre genre1 = new Genre("Sci-Fi", List.of(), List.of());
        st.insert(genre1);

        Genre genre2 = new Genre("Drama", List.of(), List.of());
        st.insert(genre2);

        Genre genre3 = new Genre("Action", List.of(), List.of());
        st.insert(genre3);

        Genre genre4 = new Genre("Comedy", List.of(), List.of());
        st.insert(genre4);

        return st;
    }
}
