package model.users;

import model.content.Genre;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe que armazena as preferências de um usuário.
 * As preferências incluem gêneros favoritos, idioma, classificação de maturidade e notificações.
 */
public class UserPreferences {
    /** Lista de gêneros preferidos pelo usuário */
    private List<Genre> preferredGenres;
    /** Idioma preferido do usuário */
    private String language;
    /** Classificação de maturidade permitida (ex: 0 = todas as idades, 12 = PG-12, 18 = adulto) */
    private int maturityRating;
    /** Se notificações estão habilitadas */
    private boolean notificationsEnabled;

    /**
     * Construtor das preferências do usuário.
     * 
     * @param language idioma preferido
     * @param maturityRating classificação de maturidade permitida
     * @param notificationsEnabled se notificações estão habilitadas
     */
    public UserPreferences(String language, int maturityRating, boolean notificationsEnabled) {
        this.preferredGenres = new ArrayList<>();
        this.language = language;
        this.maturityRating = maturityRating;
        this.notificationsEnabled = notificationsEnabled;
    }

    /**
     * Retorna a lista de gêneros preferidos.
     * 
     * @return lista de gêneros
     */
    public List<Genre> getPreferredGenres() {
        return preferredGenres;
    }

    /**
     * Define a lista de gêneros preferidos.
     * 
     * @param preferredGenres a nova lista de gêneros
     */
    public void setPreferredGenres(List<Genre> preferredGenres) {
        this.preferredGenres = preferredGenres;
    }

    /**
     * Adiciona um gênero à lista de preferidos.
     * 
     * @param genre o gênero a adicionar
     */
    public void addGenre(Genre genre) {
        if (!preferredGenres.contains(genre)) {
            preferredGenres.add(genre);
        }
    }
    
    /**
     * Remove um gênero da lista de preferidos.
     * 
     * @param genre o gênero a remover
     */
    public void removeGenre(Genre genre) {
        preferredGenres.remove(genre);
    }

    /**
     * Verifica se notificações estão habilitadas.
     * 
     * @return true se notificações estão habilitadas, false caso contrário
     */
    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    /**
     * Define se notificações estão habilitadas.
     * 
     * @param notificationsEnabled true para habilitar, false para desabilitar
     */
    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    /**
     * Retorna a classificação de maturidade permitida.
     * 
     * @return a classificação
     */
    public int getMaturityRating() {
        return maturityRating;
    }

    /**
     * Define a classificação de maturidade permitida.
     * 
     * @param maturityRating a nova classificação
     */
    public void setMaturityRating(int maturityRating) {
        this.maturityRating = maturityRating;
    }

    /**
     * Retorna o idioma preferido do usuário.
     * 
     * @return o idioma
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Define o idioma preferido do usuário.
     * 
     * @param language o novo idioma
     */
    public void setLanguage(String language) {
        this.language = language;
    }
}
