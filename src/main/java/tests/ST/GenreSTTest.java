package tests.ST;

import model.content.Genre;
import service.st.GenreST;

import java.util.List;
import java.util.UUID;

/**
 * Classe de testes para o serviço {@link GenreST}.
 * Contém baterias de testes unitários que validam as operações de inserção,
 * consulta, edição, remoção e listagem de géneros cinematográficos/conteúdo.
 */
public class GenreSTTest {

    /**
     * Executa sequencialmente toda a suite de testes da classe,
     * imprimindo os resultados e mensagens de sucesso/erro no console.
     */
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

    /**
     * Valida o cenário padrão de inserção de registos válidos.
     * Verifica se o tamanho do repositório aumenta corretamente e se os elementos
     * inseridos podem ser encontrados através dos seus respetivos identificadores.
     */
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

    /**
     * Garante que a tentativa de inserção de um objeto duplicado (com o mesmo ID)
     * seja intercetada pelo sistema através do lançamento de uma exceção.
     */
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

    /**
     * Testa o comportamento do sistema ao tentar inserir uma referência nula,
     * esperando que uma exceção apropriada seja lançada.
     */
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

    /**
     * Verifica se a recuperação de um género existente através do seu identificador único
     * funciona corretamente, validando a integridade dos dados retornados.
     */
    private static void testGet() {
        GenreST st = buildST();

        List<Genre> genres = st.listAll();
        Genre first = genres.get(0);

        Genre found = st.get(first.getId());
        assert found != null : "Erro: deveria encontrar o gênero";
        assert found.getName().equals(first.getName()) : "Erro: o nome deveria ser igual";

        System.out.println("testGet() -> FEITO");
    }

    /**
     * Testa a busca por um identificador que não consta no repositório,
     * confirmando que o retorno esperado para esta situação é nulo.
     */
    private static void testGetInexistente() {
        GenreST st = buildST();
        String id_null = UUID.randomUUID().toString(); // um id que não existe na lista

        assert st.get(id_null) == null : "Erro: devia devolver «null» para um UUID não registrado";

        System.out.println("testGetInexistente() -> FEITO");
    }

    // --------------------------- EDIT ---------------------------

    /**
     * Valida a alteração dos dados de um género previamente inserido, certificando-se
     * de que os novos atributos foram guardados com sucesso na estrutura.
     */
    private static void testEdit() {
        GenreST st = new GenreST();

        Genre original = new Genre("Horror", List.of(), List.of());
        st.insert(original);

        Genre edited = new Genre("Thriller", List.of(), List.of());
        st.edit(original.getId(), edited);

        assert st.get(original.getId()).getName().equals(edited.getName()) : "Erro: nome não foi atualizado";

        System.out.println("testEdit() -> FEITO");
    }

    /**
     * Verifica o comportamento robusto do método de edição diante de cenários inválidos,
     * como a passagem de objetos nulos ou modificações atreladas a identificadores fictícios.
     */
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

    /**
     * Valida o fluxo de remoção de um elemento por ID, assegurando que o elemento correspondente
     * de facto deixa de existir na estrutura e que o tamanho geral é reduzido.
     */
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

    /**
     * Testa as restrições e tratamentos de erro ao tentar remover elementos nulos
     * ou baseados em chaves de identificação que não foram previamente registadas.
     */
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

    /**
     * Verifica o método de listagem geral de dados num cenário preenchido, certificando-se
     * de que o número total de itens devolvido condiz exatamento com o esperado.
     */
    private static void testListAll() {
        GenreST st = buildST();

        List<Genre> lista = st.listAll();
        assert lista.size() == 4 : "Erro: size deveria ser 4";

        lista.forEach(g -> System.out.println("ID: " + g.getId() + " | GENRE: " + g.getName()));
        System.out.println("testListAll() -> FEITO");
    }

    /**
     * Valida se a listagem de uma estrutura recém-instanciada e sem dados
     * é devolvida de forma segura como uma lista vazia.
     */
    private static void testListAllVazia() {
        GenreST st = new GenreST();

        List<Genre> lista = st.listAll();

        assert lista.isEmpty() : "Erro: a lista deve ser vazia";
        System.out.println("testListAllVazia() -> FEITO");
    }

    // --------------------------- AUXILIARES ---------------------------

    /**
     * Método utilitário auxiliar configurado para gerar e pré-popular uma instância
     * de {@link GenreST} com dados fictícios controlados para suporte aos testes de leitura.
     * * @return Uma instância de {@link GenreST} carregada com quatro géneros base.
     */
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