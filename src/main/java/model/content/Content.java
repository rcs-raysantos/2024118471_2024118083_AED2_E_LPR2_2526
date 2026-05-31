package model.content;

import model.artists.Actor;
import model.artists.Director;
import model.support.Ratable;
import model.support.Searchable;
import model.support.Streamable;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Classe abstrata que representa um conteúdo generico no sistema (filme, série, documentário).
 * Um conteúdo é avaliável, pesquisável e streamável.
 *
 * @version 1.0
 */
public abstract class Content implements Ratable, Searchable, Streamable, Serializable {
  private static final long serialVersionUID = 1L;

  /** Identificador único do conteúdo */
  private final String id;
  /** Título do conteúdo */
  private String title;
  /** Data de lançamento do conteúdo */
  private LocalDate releaseDate;
  /** Duração em minutos do conteúdo */
  private int duration;
  /** Sinopse ou descrição do conteúdo */
  private String synopsis;
  /** Classificação/nota do conteúdo (1-5) */
  private int rating;
  /** Lista de gêneros do conteúdo */
  private List<Genre> genres;
  /** Lista de atores do conteúdo */
  private List<Actor> actors;
  /** Diretor do conteúdo */
  private Director director;

  /**
   * Construtor do conteúdo com todas as informações.
   * 
   * @param title título do conteúdo
   * @param releaseDate data de lançamento
   * @param duration duração em minutos
   * @param synopsis sinopse do conteúdo
   * @param rating classificação inicial
   * @param genres lista de gêneros
   * @param actors lista de atores
   * @param director diretor do conteúdo
   */
  public Content(String title, LocalDate releaseDate, int duration, String synopsis, int rating, List<Genre> genres, List<Actor> actors, Director director) {
    this.id = UUID.randomUUID().toString();
    this.title = title;
    this.releaseDate = releaseDate;
    this.duration = duration;
    this.synopsis = synopsis;
    this.rating = rating;
    this.genres = genres;
    this.actors = actors;
    this.director = director;
  }

    public Content(String title, LocalDate releaseDate, int duration, String synopsis) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.synopsis = synopsis;
    }

    /**
   * Retorna o identificador do conteúdo.
   * 
   * @return o ID
   */
  public String getId() {
    return id;
  }

  /**
   * Retorna o título do conteúdo.
   * 
   * @return o título
   */
  public String getTitle() {
    return title;
  }
  
  /**
   * Define o título do conteúdo.
   * 
   * @param title o novo título
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Retorna a data de lançamento do conteúdo.
   * 
   * @return a data de lançamento
   */
  public LocalDate getReleaseDate() {
    return releaseDate;
  }
  
  /**
   * Define a data de lançamento do conteúdo.
   * 
   * @param releaseDate a nova data de lançamento
   */
  public void setReleaseDate(LocalDate releaseDate) {
    this.releaseDate = releaseDate;
  }

  /**
   * Retorna a duração do conteúdo em minutos.
   * 
   * @return a duração
   */
  public int getDuration() {
    return duration;
  }
  
  /**
   * Define a duração do conteúdo em minutos.
   * 
   * @param duration a nova duração
   */
  public void setDuration(int duration) {
    this.duration = duration;
  }

  /**
   * Retorna a sinopse do conteúdo.
   * 
   * @return a sinopse
   */
  public String getSynopsis() {
    return synopsis;
  }
  
  /**
   * Define a sinopse do conteúdo.
   * 
   * @param synopsis a nova sinopse
   */
  public void setSynopsis(String synopsis) {
    this.synopsis = synopsis;
  }

  /**
   * Retorna a classificação atual do conteúdo.
   * 
   * @return a classificação (1-5)
   */
  public int getRating() {
    return rating;
  }
  
  /**
   * Define a classificação do conteúdo.
   * 
   * @param rating a nova classificação
   */
  public void setRating(int rating) {
    this.rating = rating;
  }

  /**
   * Retorna a lista de gêneros do conteúdo.
   * 
   * @return lista de gêneros
   */
  public List<Genre> getGenres() {
    return genres;
  }
  
  /**
   * Define a lista de gêneros do conteúdo.
   * 
   * @param genres a nova lista de gêneros
   */
  public void setGenres(List<Genre> genres) {
    this.genres = genres;
  }

  /**
   * Retorna a lista de atores do conteúdo.
   * 
   * @return lista de atores
   */
  public List<Actor> getActors() {
    return actors;
  }
  
  /**
   * Define a lista de atores do conteúdo.
   * 
   * @param actors a nova lista de atores
   */
  public void setActors(List<Actor> actors) {
    this.actors = actors;
  }

  /**
   * Retorna o diretor do conteúdo.
   * 
   * @return o diretor
   */
  public Director getDirector() {
    return director;
  }
  
  /**
   * Define o diretor do conteúdo.
   * 
   * @param director o novo diretor
   */
  public void setDirector(Director director) {
    this.director = director;
  }

  /**
   * Avalia o conteúdo com uma pontução de 1 a 5 estrelas.
   * 
   * @param score a pontução da avaliação (1-5)
   * @throws IllegalArgumentException se o score estiver fora do intervalo 1-5
   */
  @Override
  public void rate(int score) {
    if (score < 1 || score > 5) {
      throw new IllegalArgumentException("Nota inválida");
    }
    setRating(score);
  }

  /**
   * Procura por uma query no título do conteúdo.
   * Se encontrar, imprime o título.
   * 
   * @param q a query de busca
   */
  @Override
  public void search(String q) {
    if (q == null) return;

    String query = q.toLowerCase();

    if (getTitle().toLowerCase().contains(query)) {
      System.out.println(getTitle());
    }
  }
}
