package model.artists;

import model.content.Content;

import java.time.LocalDate;
import java.util.List;

/**
 * Classe que representa um ator no sistema.
 * Um ator tem uma agência e uma lista de conteúdos em que atuou.
 *
 * @version 1.0
 */
public class Actor extends Artist {

  /** Agência que representa o ator */
  private String agency;
  /** Lista de conteúdos em que o ator atuou */
  private List<Content> content;

  /**
   * Construtor do ator com todas suas informações.
   * 
   * @param name nome do ator
   * @param gender gênero do ator
   * @param birthDate data de nascimento do ator
   * @param nationality nacionalidade do ator
   * @param agency agência representante do ator
   * @param content lista de conteúdos do ator
   */
  public Actor(String name, String gender, LocalDate birthDate, String nationality, String agency, List<Content> content) {
    super(name, gender, birthDate, nationality);
    this.agency = agency;
    this.content = content;
  }

  /**
   * Retorna a agência do ator.
   * 
   * @return a agência
   */
  public String getAgency() {
    return agency;
  }

  /**
   * Define a agência do ator.
   * 
   * @param agency a nova agência
   */
  public void setAgency(String agency) {
    this.agency = agency;
  }

  /**
   * Retorna a lista de conteúdos do ator.
   * 
   * @return lista de conteúdos
   */
  public List<Content> getContent() {
    return content;
  }

  /**
   * Define a lista de conteúdos do ator.
   * 
   * @param content a nova lista de conteúdos
   */
  public void setContent(List<Content> content) {
    this.content = content;
  }

  /**
   * Retorna a filmografia do ator.
   * 
   * @return lista de conteúdos em que atuou
   */
  public List<Content> getFilmography() {
    if (content != null) {
      return content;
    }
    return List.of();
  }
}
