package model.artists;

import model.content.Content;

import java.time.LocalDate;
import java.util.List;

/**
 * Classe que representa um diretor no sistema.
 * Um diretor tem um estilo característico e uma lista de conteúdos que dirigiu.
 *
 * @version 1.0
 */
public class Director extends Artist {

  /** Estilo característico do diretor */
  private String style;
  /** Lista de conteúdos dirigidos */
  private List<Content> content;

  /**
   * Construtor do diretor com todas suas informações.
   * 
   * @param name nome do diretor
   * @param gender gênero do diretor
   * @param birthDate data de nascimento do diretor
   * @param nationality nacionalidade do diretor
   * @param style estilo característico do diretor
   * @param content lista de conteúdos dirigidos
   */
  public Director(String name, String gender, LocalDate birthDate, String nationality, String style, List<Content> content) {
    super(name, gender, birthDate, nationality);
    this.style = style;
    this.content = content;
  }

  /**
   * Retorna o estilo do diretor.
   * 
   * @return o estilo
   */
  public String getStyle() {
    return style;
  }

  /**
   * Define o estilo do diretor.
   * 
   * @param style o novo estilo
   */
  public void setStyle(String style) {
    this.style = style;
  }

  /**
   * Retorna a lista de conteúdos dirigidos.
   * 
   * @return lista de conteúdos
   */
  public List<Content> getContent() {
    return content;
  }

  /**
   * Define a lista de conteúdos dirigidos.
   * 
   * @param content a nova lista de conteúdos
   */
  public void setContent(List<Content> content) {
    this.content = content;
  }

  /**
   * Retorna a filmografia do diretor.
   * 
   * @return lista de conteúdos dirigidos
   */
  public List<Content> getFilmography() {
    if (content != null) {
      return content;
    }
    return List.of();
  }
}
