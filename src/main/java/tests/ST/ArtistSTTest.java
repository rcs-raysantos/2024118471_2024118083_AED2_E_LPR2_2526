package tests.ST;

import model.artists.Actor;
import model.artists.Artist;
import model.artists.Director;
import service.st.ArtistST;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * @brief Classe de testes unitários para a ArtistST.
 * * Esta classe contém uma bateria de testes para validar as operações de
 * inserção, remoção, edição e consulta na Tabela de Símbolos de Artistas.
 * Utiliza asserções para garantir a integridade dos dados e o comportamento correto da estrutura.
 * * @author Rayssa Santos
 * @version 1.0
 */
public class ArtistSTTest {

    /**
     * @brief Executa todos os testes da classe ArtistSTTest.
     * * Centraliza a execução da suite de testes para facilitar o debug e a automação.
     */
    public static void runAll() {
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
     * @test Valida a inserção de múltiplos artistas (Atores e Diretores) e verifica o tamanho da ST.
     */
    public static void testInsert() {
        ArtistST st = new ArtistST();

        Actor a1 = new Actor("Leonardo DiCaprio", "M", LocalDate.of(1974, 11, 11), "US", "CAA", List.of());
        Actor a2 = new Actor("Cate Blanchett", "F", LocalDate.of(1969, 5, 14), "AU", "WME", List.of());
        Director d1 = new Director("Christopher Nolan", "M", LocalDate.of(1970, 7, 30), "UK", "Sci-Fi/Thriller", List.of());

        st.insert(a1);
        st.insert(a2);
        st.insert(d1);

        assert st.size() == 3 : "Erro: size deveria ser 3";
        assert st.contains(a1.getId()) : "Erro: a1 não encontrado";
        assert st.contains(a2.getId()) : "Erro: a2 não encontrado";
        assert st.contains(d1.getId()) : "Erro: d1 não encontrado";

        System.out.println("testInsert() -> FEITO");
        System.out.println("size = " + st.size()); // DEBUG
    }

    /**
     * @test Verifica se o sistema impede a inserção de um artista com ID duplicado.
     */
    public static void testInsertDuplicado() {
        ArtistST st = new ArtistST();

        Actor a = new Actor("Leonardo DiCaprio", "M", LocalDate.of(1974, 11, 11), "US", "CAA", List.of());
        st.insert(a);

        try {
            st.insert(a); // mesmo objeto = mesmo UUID = duplicado
            System.out.println("testInsertDuplicado() -> DEU ERRADO: devia lançar exceção");
        } catch (IllegalArgumentException e) {
            System.out.println("testInsertDuplicado() -> FEITO: " + e.getMessage());
        }
    }

    /**
     * @test Garante que a inserção de objetos nulos lança a exceção apropriada.
     */
    public static void testInsertNull() {
        ArtistST st = buildST();

        try {
            st.insert(null);
            System.out.println("testInsertNull() -> DEU ERRADO: devia lançar exceção");
        } catch (IllegalArgumentException e) {
            System.out.println("testInsertNull() -> FEITO: " + e.getMessage());
        }
    }

    /**
     * @test Valida a recuperação de um artista pelo seu identificador único.
     */
    public static void testGet() {
        ArtistST st = new ArtistST();

        Actor a = new Actor("Leonardo DiCaprio", "M", LocalDate.of(1974, 11, 11), "US", "CAA", List.of());
        st.insert(a);

        Artist found = st.get(a.getId());
        assert found != null : "Erro: devia encontrar o artista";
        assert found.getName().equals("Leonardo DiCaprio") : "Erro: nome incorreto";
        assert found.getNationality().equals("US") : "Erro: nacionalidade incorreta";

        System.out.println("testGet() -> FEITO");
        System.out.println("Artista: " + found.getName() + " | Nacionalidade: [" + found.getNationality() + "]");
    }

    /**
     * @test Verifica se a pesquisa por um ID inexistente retorna null.
     */
    public static void testGetInexistente() {
        ArtistST st = buildST();
        String idFalso = UUID.randomUUID().toString();

        assert st.get(idFalso) == null : "Erro: devia devolver null";

        System.out.println("testGetInexistente() -> FEITO");
    }

    /**
     * @test Valida a atualização de dados de um artista existente na ST.
     */
    public static void testEdit(){
        ArtistST st = new ArtistST();

        Actor a = new Actor("Leonardo DiCaprio", "M", LocalDate.of(1974, 11, 11), "US", "CAA", List.of());
        st.insert(a);

        a.setNationality("IT");
        a.setAgency("UTA");
        st.edit(a.getId(), a);

        assert st.get(a.getId()).getNationality().equals("IT") : "Erro: nacionalidade não atualizada";
        assert ((Actor) st.get(a.getId())).getAgency().equals("UTA") : "Erro: agência não atualizada";

        System.out.println("testEdit() -> FEITO");
        System.out.println("Artistas: " + st.get(a.getId()).getName() + " | Nacionalidade: [" + st.get(a.getId()).getNationality() + "]");
    }

    /**
     * @test Verifica se a edição de um artista inexistente lança uma exceção.
     */
    public static void testEditInexistente(){
        ArtistST st = buildST();
        String idFalso = UUID.randomUUID().toString();

        Actor qualquer = new Actor("Ninguém", "M", LocalDate.of(1990, 1, 1), "PT", "N/A", List.of());

        try {
            st.edit(idFalso, qualquer);
            System.out.println("testEditInexistente() -> DEU ERRADO: devia lançar exceção");
        } catch (IllegalArgumentException e) {
            System.out.println("testEditInexistente() -> FEITO: " + e.getMessage());
        }
    }

    /**
     * @test Valida a remoção de um artista e a subsequente atualização do tamanho da ST.
     */
    public static void testRemove(){
        ArtistST st = new ArtistST();

        Actor a = new Actor("Leonardo DiCaprio", "M", LocalDate.of(1974, 11, 11), "US", "CAA", List.of());
        st.insert(a);

        st.remove(a.getId());

        assert !st.contains(a.getId()) : "Erro: artista devia ter sido removido";
        assert st.size() == 0 : "Erro: size devia ser 0";

        System.out.println("testRemove() -> FEITO");
        System.out.println("size = " + st.size()); // DEBUG
    }

    /**
     * @test Verifica se a tentativa de remover um ID inexistente lança uma exceção.
     */
    public static void testRemoveInexistente(){
        ArtistST st = buildST();
        String idFalso = UUID.randomUUID().toString();

        try {
            st.remove(idFalso);
            System.out.println("testRemoveInexistente() -> DEU ERRADO: devia lançar exceção");
        } catch (IllegalArgumentException e) {
            System.out.println("testRemoveInexistente() -> FEITO: " + e.getMessage());
        }
    }

    /**
     * @test Valida a listagem completa de todos os artistas armazenados.
     */
    public static void testListAll() {
        ArtistST st = buildST();

        List<Artist> lista = st.listAll();
        assert lista.size() == 4 : "Erro: devia ter 4 artistas";

        System.out.println("testListAll() -> FEITO");
        System.out.println("size = " + lista.size()); // DEBUG
        lista.forEach(a -> System.out.println("Id: " + a.getId() + " | Artista: " + a.getName() + " | Classe: " + a.getClass().getSimpleName() + " | Nacionalidade: [" + a.getNationality() + "]"));
    }

    /**
     * @test Verifica se a listagem de uma ST vazia retorna uma lista também vazia.
     */
    public static void testListAllVazia() {
        ArtistST st = new ArtistST();

        List<Artist> lista = st.listAll();
        assert lista.isEmpty() : "Erro: ST vazia devia devolver lista vazia";

        System.out.println("testListAllVazia() -> FEITO");
    }

    /**
     * @brief Método auxiliar para popular uma ArtistST com dados de teste.
     * @return ArtistST preenchida com 4 artistas fictícios.
     */
    static ArtistST buildST() {
        ArtistST st = new ArtistST();

        st.insert(new Actor("Leonardo DiCaprio", "M", LocalDate.of(1974, 11, 11), "US", "CAA", List.of()));
        st.insert(new Actor("Cate Blanchett", "F", LocalDate.of(1969, 5, 14), "AU", "WME", List.of()));
        st.insert(new Director("Christopher Nolan", "M", LocalDate.of(1970, 7, 30), "UK", "Sci-Fi/Thriller", List.of()));
        st.insert(new Director("Greta Gerwig", "F", LocalDate.of(1983, 8, 4), "US", "Drama/Comedy", List.of()));

        return st;
    }
}