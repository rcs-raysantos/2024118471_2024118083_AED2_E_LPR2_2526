package model.artists;

import model.Person;
import model.content.Content;

import java.time.LocalDate;
import java.util.List;

/**
 * Classe abstrata que representa um artista (ator ou diretor) no sistema.
 * Um artista tem uma nacionalidade e uma filmografia.
 *
 * @version 1.0
 */
public abstract class Artist extends Person {

  /** Nacionalidade do artista */
  private String nationality;

  /**
   * Construtor do artista com todas suas informações.
   * 
   * @param name nome do artista
   * @param gender gênero do artista
   * @param birthDate data de nascimento do artista
   * @param nationality nacionalidade do artista
   */
  public Artist(String name, String gender, LocalDate birthDate, String nationality) {
    super(name, gender, birthDate);
    this.nationality = nationality;
  }

  /**
   * Retorna a filmografia do artista.
   * 
   * @return lista de conteúdos do artista
   */
  public abstract List<Content> getFilmography();

  /**
   * Retorna a nacionalidade do artista.
   * 
   * @return a nacionalidade
   */
  public String getNationality() {
    return nationality;
  }

  /**
   * Define a nacionalidade do artista.
   * 
   * @param nationality a nova nacionalidade
   */
  public void setNationality(String nationality) {
    this.nationality = nationality;
  }
}
