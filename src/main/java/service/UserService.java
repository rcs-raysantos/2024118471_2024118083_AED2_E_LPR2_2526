package service;

import model.artists.Artist;
import model.content.Content;
import model.content.Genre;
import model.users.User;
import service.archive.Archive;
import service.bst.ArtistBST;
import service.bst.ContentBST;
import service.bst.GenreBST;
import service.bst.UserBST;
import service.st.ArtistST;
import service.st.ContentST;
import service.st.GenreST;
import service.st.UserST;

import java.util.List;

/**
 * Classe de serviço que atua como uma Fachada (Facade) para a gestão de todas as entidades do sistema.
 * Centraliza as operações de CRUD (Create, Read, Update, Delete) para Utilizadores, Artistas e Conteúdos,
 * delegando a persistência de dados para as respetivas Symbol Tables (ST).
 */
public class UserService {

    /** Tabela de símbolos para gestão de utilizadores (Chave: ID). */
    private final UserST userST;

    /** Árvore binária balanceada para gestão de utilizadores (Chave: Data). */
    private final UserBST userBST;

    /** Arquivo para entidades removidas. */
    private final Archive archive;

    /** Tabela de símbolos para gestão de artistas (Chave: ID). */
    private final ArtistST artistST;

    /** Árvore binária balanceada para gestão de artistas (Chave: Data). */
    private final ArtistBST artistBST;

    /** Tabela de símbolos para gestão de conteúdos (Chave: ID). */
    private final ContentST contentST;

    /** Árvore binária balanceada para gestão de conteúdos (Chave: Data). */
    private final ContentBST contentBST;

    /** Tabela de símbolos para gestão de gêneros (Chave: ID). */
    private final GenreST genreST;

    /** Árvore binária balanceada para gestão de gêneros (Chave: Data). */
    private final GenreBST genreBST;

    /**
     * Inicializa o serviço e cria as instâncias das tabelas de símbolos necessárias.
     */
    public UserService() {
        userST = new UserST();
        userBST = new UserBST();
        archive = new Archive();
        artistST = new ArtistST();
        artistBST = new ArtistBST();
        contentST = new ContentST();
        contentBST = new ContentBST();
        genreST = new GenreST();
        genreBST = new GenreBST();
    }

    // --------------------------- USER ---------------------------

    /**
     * Regista um novo utilizador no sistema.
     * @param user O objeto User a ser inserido.
     */
    public void registerUser(User user){
        userST.insert(user);
        userBST.insert(user);
    }

    /**
     * Atualiza os dados de um utilizador existente.
     * @param id O identificador único do utilizador a editar.
     * @param user O objeto com os novos dados.
     */
    public void updateUser(String id, User user){
        // Para atualizar, precisamos remover da BST e inserir novamente com nova data
        User oldUser = userST.get(id);
        if (oldUser != null) {
            userBST.remove(oldUser);
        }
        userST.edit(id, user);
        userBST.insert(user);
    }

    /**
     * Remove um utilizador do sistema através do seu ID.
     * Arquiva o usuário e remove de todas as estruturas de dados.
     * @param id O ID do utilizador a remover.
     */
    public void removeUser(String id){
        User user = userST.get(id);
        if (user != null) {
            // Arquiva o usuário
            archive.archiveUser(user);
            // Remove de ST
            userST.remove(id);
            // Remove de BST
            userBST.remove(user);
            // Remove de ligações em gêneros
            for (Genre genre : genreST.listAll()) {
                genre.getUsers().remove(user);
            }
        }
    }

    /**
     * Lista todos os utilizadores registados.
     * @return Uma lista contendo todos os objetos User.
     */
    public List<User> listUsers(){
        return userST.listAll();
    }

    /**
     * Retorna o arquivo de entidades removidas.
     * @return O objeto Archive
     */
    public Archive getArchive() {
        return archive;
    }

    // --------------------------- ARTIST ---------------------------

    /**
     * Regista um novo artista no sistema.
     * @param artist O objeto Artist a ser inserido.
     */
    public void registerArtist(Artist artist){
        artistST.insert(artist);
        artistBST.insert(artist);
    }

    /**
     * Atualiza os dados de um artista existente.
     * @param id O identificador único do artista.
     * @param artist O objeto com os novos dados.
     */
    public void updateArtist(String id, Artist artist){
        Artist oldArtist = artistST.get(id);
        if (oldArtist != null) {
            artistBST.remove(oldArtist);
        }
        artistST.edit(id, artist);
        artistBST.insert(artist);
    }

    /**
     * Remove um artista do sistema.
     * @param id O ID do artista a remover.
     */
    public void removeArtist(String id){
        Artist artist = artistST.get(id);
        if (artist != null) {
            archive.archiveArtist(artist);
            artistST.remove(id);
            artistBST.remove(artist);
        }
    }

    /**
     * Lista todos os artistas registados.
     * @return Uma lista contendo todos os objetos Artist.
     */
    public List<Artist> listArtists(){
        return artistST.listAll();
    }

    // --------------------------- CONTENT ---------------------------

    /**
     * Regista um novo conteúdo (Filme, Série, etc.) no sistema.
     * @param content O objeto Content a ser inserido.
     */
    public void registerContent(Content content){
        contentST.insert(content);
        contentBST.insert(content);
    }

    /**
     * Atualiza os dados de um conteúdo existente.
     * @param id O identificador único do conteúdo.
     * @param content O objeto com os novos dados.
     */
    public void updateContent(String id, Content content){
        Content oldContent = contentST.get(id);
        if (oldContent != null) {
            contentBST.remove(oldContent);
        }
        contentST.edit(id, content);
        contentBST.insert(content);
    }

    /**
     * Remove um conteúdo do sistema.
     * @param id O ID do conteúdo a remover.
     */
    public void removeContent(String id){
        Content content = contentST.get(id);
        if (content != null) {
            archive.archiveContent(content);
            contentST.remove(id);
            contentBST.remove(content);
        }
    }

    /**
     * Lista todos os conteúdos registados.
     * @return Uma lista contendo todos os objetos Content.
     */
    public List<Content> listContents(){
        return contentST.listAll();
    }

    // --------------------------- GENRE ---------------------------

    /**
     * Regista um novo gênero no sistema.
     * @param genre O objeto Genre a ser inserido.
     */
    public void registerGenre(Genre genre){
        genreST.insert(genre);
        genreBST.insert(genre);
    }

    /**
     * Atualiza os dados de um gênero existente.
     * @param id O identificador único do gênero.
     * @param genre O objeto com os novos dados.
     */
    public void updateGenre(String id, Genre genre){
        Genre oldGenre = genreST.get(id);
        if (oldGenre != null) {
            genreBST.remove(oldGenre);
        }
        genreST.edit(id, genre);
        genreBST.insert(genre);
    }

    /**
     * Remove um gênero do sistema.
     * @param id O ID do gênero a remover.
     */
    public void removeGenre(String id){
        Genre genre = genreST.get(id);
        if (genre != null) {
            archive.archiveGenre(genre);  // Se quiser arquivar genres
            genreST.remove(id);
            genreBST.remove(genre);
        }
    }

    /**
     * Lista todos os gêneros registados.
     * @return Uma lista contendo todos os objetos Genre.
     */
    public List<Genre> listGenres(){
        return genreST.listAll();
    }

    // --------------------------- GETTERS ---------------------------

    public UserST getUserST() {
        return userST;
    }

    public UserBST getUserBST() {
        return userBST;
    }

    public ContentST getContentST() {
        return contentST;
    }

    public ContentBST getContentBST() {
        return contentBST;
    }

    public ArtistST getArtistST() {
        return artistST;
    }

    public ArtistBST getArtistBST() {
        return artistBST;
    }

    public GenreST getGenreST() {
        return genreST;
    }

    public GenreBST getGenreBST() {
        return genreBST;
    }
}