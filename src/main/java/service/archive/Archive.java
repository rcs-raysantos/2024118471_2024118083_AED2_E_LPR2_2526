package service.archive;

import model.artists.Artist;
import model.content.Content;
import model.content.Genre;
import model.users.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável pelo arquivamento de entidades removidas do sistema.
 * Mantém um histórico de usuários, conteúdos e artistas removidos para auditoria
 * e possível recuperação futura.
 */
public class Archive {
    /** Lista de artistas arquivados */
    private List<Artist> archivedArtists;

    /** Lista de usuários arquivados */
    private List<User> archivedUsers;

    /** Lista de conteúdos arquivados */
    private List<Content> archivedContents;

    /** Lista de gêneros arquivados */
    private List<Genre> archivedGenres;

    /**
     * Inicializa um novo arquivo vazio.
     */
    public Archive() {
        this.archivedUsers = new ArrayList<>();
        this.archivedContents = new ArrayList<>();
        this.archivedArtists = new ArrayList<>();
        this.archivedGenres = new ArrayList<>();
    }

    /**
     * Arquiva um usuário removido.
     * @param user O usuário a ser arquivado
     * @throws IllegalArgumentException se o usuário for nulo
     */
    public void archiveUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        archivedUsers.add(user);
    }

    /**
     * Arquiva um conteúdo removido.
     * @param content O conteúdo a ser arquivado
     * @throws IllegalArgumentException se o conteúdo for nulo
     */
    public void archiveContent(Content content) {
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }
        archivedContents.add(content);
    }

    /**
     * Arquiva um artista removido.
     * @param artist O artista a ser arquivado
     * @throws IllegalArgumentException se o artista for nulo
     */
    public void archiveArtist(Artist artist) {
        if (artist == null) {
            throw new IllegalArgumentException("Artist cannot be null");
        }
        archivedArtists.add(artist);
    }

    /**
     * Arquiva um gênero removido.
     * @param genre O gênero a ser arquivado
     * @throws IllegalArgumentException se o gênero for nulo
     */
    public void archiveGenre(Genre genre) {
        if (genre == null) {
            throw new IllegalArgumentException("Genre cannot be null");
        }
        archivedGenres.add(genre);
    }

    /**
     * Retorna a lista de usuários arquivados.
     * @return Lista de usuários arquivados
     */
    public List<User> getArchivedUsers() {
        return new ArrayList<>(archivedUsers); // Retorna cópia para evitar modificações externas
    }

    /**
     * Retorna a lista de conteúdos arquivados.
     * @return Lista de conteúdos arquivados
     */
    public List<Content> getArchivedContents() {
        return new ArrayList<>(archivedContents);
    }

    /**
     * Retorna a lista de artistas arquivados.
     * @return Lista de artistas arquivados
     */
    public List<Artist> getArchivedArtists() {
        return new ArrayList<>(archivedArtists);
    }

    /**
     * Retorna a lista de gêneros arquivados.
     * @return Lista de gêneros arquivados
     */
    public List<Genre> getArchivedGenres() {
        return new ArrayList<>(archivedGenres);
    }

    /**
     * Verifica se um usuário está arquivado.
     * @param userId O ID do usuário
     * @return true se o usuário estiver arquivado, false caso contrário
     */
    public boolean isUserArchived(String userId) {
        return archivedUsers.stream().anyMatch(u -> u.getId().equals(userId));
    }

    /**
     * Verifica se um conteúdo está arquivado.
     * @param contentId O ID do conteúdo
     * @return true se o conteúdo estiver arquivado, false caso contrário
     */
    public boolean isContentArchived(String contentId) {
        return archivedContents.stream().anyMatch(c -> c.getId().equals(contentId));
    }

    /**
     * Verifica se um artista está arquivado.
     * @param artistId O ID do artista
     * @return true se o artista estiver arquivado, false caso contrário
     */
    public boolean isArtistArchived(String artistId) {
        return archivedArtists.stream().anyMatch(a -> a.getId().equals(artistId));
    }

    /**
     * Retorna o número total de entidades arquivadas.
     * @return Número total de entidades arquivadas
     */
    public int getTotalArchived() {
        return archivedUsers.size() + archivedContents.size() + archivedArtists.size() + archivedGenres.size();
    }
}