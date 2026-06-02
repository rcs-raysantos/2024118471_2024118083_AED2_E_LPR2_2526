package model.graph;

/**
 * Enumerador que define os tipos de relacionamentos semânticos possíveis no grafo da aplicação.
 * Estabelece a taxonomia das ligações (arestas) direcionadas que conectam utilizadores,
 * produções multimédia, artistas e géneros cinematográficos.
 */
public enum RelationType {

    /**
     * Conexão que indica que um determinado utilizador assistiu a um conteúdo multimédia.
     * Tipicamente liga um vértice de Utilizador a um vértice de Conteúdo (Filme/Série).
     */
    USER_WATCHED,

    /**
     * Conexão que representa a avaliação numérica ou crítica atribuída por um utilizador a um conteúdo.
     * Permite mapear notas ou pontuações de preferência no peso da aresta.
     */
    USER_RATED,

    /**
     * Conexão que mapeia a participação de um profissional da indústria como ator em uma obra.
     * Conecta um vértice de Artista a um vértice de Conteúdo.
     */
    ACTOR_IN,

    /**
     * Conexão que identifica a autoria de realização de um realizador sobre uma produção.
     * Conecta um vértice de Artista (Realizador) a um vértice de Conteúdo.
     */
    DIRECTED_BY,

    /**
     * Conexão de catalogação taxonómica que classifica a pertença de uma obra a uma categoria.
     * Conecta um vértice de Conteúdo a um vértice de Género.
     */
    CONTENT_HAS_GENRE,

    /**
     * Conexão que expressa o interesse, afinidade declarada ou predileção explícita de um
     * utilizador por uma categoria temática específica.
     * Conecta um vértice de Utilizador a um vértice de Género.
     */
    USER_PREFERS_GENRE
}