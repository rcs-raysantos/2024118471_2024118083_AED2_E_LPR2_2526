package model.content;

import java.time.LocalDate;

/**
 * Classe que representa uma série no sistema.
 * Uma série é um tipo de conteúdo com estéias e episódios.
 */
public class Series extends Content {

  /** Número de estéias da série */
  private int seasons;
  /** Número total de episódios da série */
  private int episodes;

  /**
   * Construtor da série com todas as informações.
   *
   * @param title título da série
   * @param releaseDate data de lançamento
   * @param duration duração de cada episódio em minutos
   * @param synopsis sinopse da série
   * @param seasons número de estéias
   * @param episodes número total de episódios
   */
  public Series(String title, LocalDate releaseDate, int duration, String synopsis, int seasons, int episodes) {
    super(title, releaseDate, duration, synopsis);
    this.seasons = seasons;
    this.episodes = episodes;
  }

  /**
   * Retorna o número de estéias da série.
   * 
   * @return o número de estéias
   */
  public int getSeasons() {
    return seasons;
  }

  /**
   * Define o número de estéias da série.
   * 
   * @param seasons o novo número de estéias
   */
  public void setSeasons(int seasons) {
    this.seasons = seasons;
  }

  /**
   * Retorna o número total de episódios da série.
   * 
   * @return o número de episódios
   */
  public int getEpisodes() {
    return episodes;
  }

  /**
   * Define o número total de episódios da série.
   * 
   * @param episodes o novo número de episódios
   */
  public void setEpisodes(int episodes) {
    this.episodes = episodes;
  }

  /**
   * Retorna a URL de streaming da série.
   * Implementa a interface Streamable.
   */
  @Override
  public void getStreamUrl() {
  }

}

