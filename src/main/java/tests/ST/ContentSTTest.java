package tests.ST;

import model.content.Content;
import model.content.Documentary;
import model.content.Movie;
import model.content.Series;
import service.st.ContentST;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * @brief Classe de testes unitários para a ContentST.
 * * Esta classe contém os testes necessários para garantir que a Tabela de Símbolos
 * de conteúdos (Movies, Series, Documentaries) funciona corretamente, validando
 * a persistência, edição e remoção polimórfica dos objetos.
 * * @author O Teu Nome
 * @version 1.0
 * @date 2026
 */
public class ContentSTTest {

    /**
     * @brief Executa a suite completa de testes da ContentST.
     * * Invoca todos os métodos de teste para validar cenários de sucesso e erro
     * na gestão de conteúdos audiovisuais.
     */
    public static void runAll() {
        System.out.println("\n--------------------------- ContentSTTest ---------------------------");
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

    /**
     * @test Valida a inserção de diferentes tipos de conteúdo (Movie, Series, Documentary).
     * Garante que a estrutura aceita a hierarquia de classes e mantém o tamanho correto.
     */
    private static void testInsert() {
        ContentST st = new ContentST();

        Movie m = new Movie("Inception", LocalDate.of(2010, 7, 16), 148, "Um ladrão que rouba segredos através dos sonhos.", 1000000, 10);
        st.insert(m);

        Series s = new Series("Breaking Bad", LocalDate.of(2008, 1, 20), 60, "Um professor de química torna-se traficante de droga.", 7, 52);
        st.insert(s);

        Documentary d = new Documentary("Planet Earth II", LocalDate.of(2016, 11, 6), 60, "Série documental sobre a vida selvagem.", "Nature", "Goularte");
        st.insert(d);

        assert st.size() == 3 : "Erro: size deveria ser 3";
        assert st.contains(m.getId()) : "Erro: filme não encontrado";
        assert st.contains(s.getId()) : "Erro: série não encontrada";
        assert st.contains(d.getId()) : "Erro: documentário não encontrado";

        System.out.println("testInsert() -> FEITO");
    }

    /**
     * @test Verifica se a ST impede a inserção de conteúdos com IDs duplicados.
     */
    private static void testInsertDuplicado() {
        ContentST st = new ContentST();
        Movie m = new Movie("Inception", LocalDate.of(2010, 7, 16), 148, "Sinopse", 1000, 10);
        st.insert(m);

        try {
            st.insert(m);
            System.out.println("testInsertDuplicado() -> DEU ERRADO: deveria dar uma exceção");
        } catch(IllegalArgumentException e) {
            System.out.println("testInsertDuplicado() -> FEITO: " + e.getMessage());
        }
    }

    /**
     * @test Garante que a tentativa de inserir um conteúdo nulo resulta em exceção.
     */
    private static void testInsertNull() {
        ContentST st = new ContentST();
        try {
            st.insert(null);
            System.out.println("testInsertNull() -> DEU ERRADO: deveria dar uma exceção");
        } catch(IllegalArgumentException e) {
            System.out.println("testInsertNull() -> FEITO: " + e.getMessage());
        }
    }

    /**
     * @test Valida a recuperação de um conteúdo e a integridade dos seus atributos específicos.
     */
    private static void testGet() {
        ContentST st = new ContentST();
        Movie m = new Movie("Inception", LocalDate.of(2010, 7, 16), 148, "Sinopse", 1000, 10);
        st.insert(m);

        Content found = st.get(m.getId());
        assert found != null : "Erro: deveria encontrar o conteúdo";
        assert found.getTitle().equals(m.getTitle()) : "Erro: Título incorreto";
        assert found.getDuration() == 148 : "Erro: Duração incorreta";

        System.out.println("testGet() -> FEITO");
    }

    /**
     * @test Verifica se a pesquisa por um ID aleatório devolve null.
     */
    private static void testGetInexistente() {
        ContentST st = new ContentST();
        String id = UUID.randomUUID().toString();
        assert st.get(id) == null : "Erro: Deveria ser «null»";
        System.out.println("testGetInexistente() -> FEITO");
    }

    /**
     * @test Valida a atualização de um conteúdo existente através do método edit.
     */
    private static void testEdit() {
        ContentST st = new ContentST();
        Movie m = new Movie("Inception", LocalDate.of(2010, 7, 16), 148, "Sinopse", 1000, 10);
        st.insert(m);

        m.setTitle("Inception - Director's cut");
        m.setDuration(160);
        st.edit(m.getId(), m);

        assert st.get(m.getId()).getTitle().equals(m.getTitle()) : "Erro: título não foi atualizado";
        assert st.get(m.getId()).getDuration() == 160 : "Erro: duração não foi atualizada";

        System.out.println("testEdit() -> FEITO");
    }

    /**
     * @test Verifica se editar um conteúdo com ID inexistente lança IllegalArgumentException.
     */
    private static void testEditInexistente() {
        ContentST st = buildST();
        String id = UUID.randomUUID().toString();
        Movie qualquer = new Movie("Qualquer", LocalDate.of(2020, 1, 1), 90, "Sinopse.", 0, 0);

        try {
            st.edit(id, qualquer);
            System.out.println("testEditInexistente() -> DEU ERRADO: devia lançar exceção");
        } catch (IllegalArgumentException e) {
            System.out.println("testEditInexistente() -> FEITO: " + e.getMessage());
        }
    }

    /**
     * @test Valida a remoção física de um conteúdo da tabela de símbolos.
     */
    private static void testRemove() {
        ContentST st = new ContentST();
        Movie m = new Movie("Inception", LocalDate.of(2010, 7, 16), 148, "Sinopse", 1000, 10);
        st.insert(m);
        st.remove(m.getId());

        assert !st.contains(m.getId()) : "Erro: conteúdo devia ter sido removido";
        assert st.size() == 0 : "Erro: size devia ser 0";

        System.out.println("testRemove() -> FEITO");
    }

    /**
     * @test Garante que a remoção de um conteúdo inexistente lança exceção.
     */
    private static void testRemoveInexistente() {
        ContentST st = buildST();
        String id = UUID.randomUUID().toString();
        try {
            st.remove(id);
            System.out.println("testRemoveInexistente() -> DEU ERRADO: devia lançar exceção");
        } catch (IllegalArgumentException e) {
            System.out.println("testRemoveInexistente() -> FEITO: " + e.getMessage());
        }
    }

    /**
     * @test Valida a listagem total de conteúdos e utiliza iteração para log de dados.
     */
    private static void testListAll() {
        ContentST st = buildST();
        List<Content> lista = st.listAll();
        assert lista.size() == 4 : "Erro: devia ter 4 conteúdos";

        System.out.println("testListAll() -> FEITO");
        lista.forEach(c -> System.out.println("  - [" + c.getClass().getSimpleName() + "] " + c.getTitle()));
    }

    /**
     * @test Verifica se uma ST vazia retorna uma lista sem elementos.
     */
    private static void testListAllVazia() {
        ContentST st = new ContentST();
        List<Content> lista = st.listAll();
        assert lista.isEmpty() : "Erro: ST vazia devia devolver lista vazia";
        System.out.println("testListAllVazia() -> FEITO");
    }

    /**
     * @brief Método auxiliar para criar uma ContentST pré-populada.
     * @return ContentST com 4 conteúdos (2 Filmes, 1 Série, 1 Documentário).
     */
    static ContentST buildST() {
        ContentST st = new ContentST();
        st.insert(new Movie("Inception", LocalDate.of(2010, 7, 16), 148, "Sinopse", 5215, 456487897));
        st.insert(new Movie("Interstellar", LocalDate.of(2014, 11, 7), 169, "Sinopse", 10000, 12135640));
        st.insert(new Series("Breaking Bad", LocalDate.of(2008, 1, 20), 60, "Sinopse", 5, 35));
        st.insert(new Documentary("Planet Earth II", LocalDate.of(2016, 11, 6), 60, "Sinopse", "Natureza", "Gilberto Gil"));
        return st;
    }
}