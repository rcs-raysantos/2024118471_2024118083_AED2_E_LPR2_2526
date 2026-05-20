package model.content;

import java.time.LocalDate;

/**
 * Classe que representa um filme no sistema.
 * Um filme é um tipo de conteúdo com orçamento e bilheteria.
 *
 * @version 1.0
 */
public class Movie extends Content {

  /** Orçamento do filme em dólares */
  private double budget;
  /** Arrecadação de bilheteria do filme em dólares */
  private double boxOffice;

  /**
   * Construtor do filme com todas as informações.
   *
   * @param title título do filme
   * @param releaseDate data de lançamento
   * @param duration duração em minutos
   * @param synopsis sinopse do filme
   * @param budget orçamento do filme
   * @param boxOffice bilheteria do filme
   */
  public Movie(String title, LocalDate releaseDate, int duration, String synopsis, double budget, double boxOffice) {
    super(title, releaseDate, duration, synopsis);
    this.budget = budget;
    this.boxOffice = boxOffice;
  }

  /**
   * Retorna o orçamento do filme.
   * 
   * @return o orçamento
   */
  public double getBudget() {
    return budget;
  }
  
  /**
   * Define o orçamento do filme.
   * 
   * @param budget o novo orçamento
   */
  public void setBudget(double budget) {
    this.budget = budget;
  }

  /**
   * Retorna a bilheteria do filme.
   * 
   * @return a bilheteria
   */
  public double getBoxOffice() {
    return boxOffice;
  }
  
  /**
   * Define a bilheteria do filme.
   * 
   * @param boxOffice a nova bilheteria
   */
  public void setBoxOffice(double boxOffice) {
    this.boxOffice = boxOffice;
  }

  /**
   * Retorna a URL de streaming do filme.
   * Implementa a interface Streamable.
   */
  @Override
  public void getStreamUrl() {
  }
}