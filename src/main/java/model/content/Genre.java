package model.content;
import model.users.User;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Classe que representa um gênero no sistema.
 * Um gênero possui um nome e está associado a usuários e conteúdos.
 *
 * @version 1.0
 */
public class Genre implements Serializable {
  private static final long serialVersionUID = 1L;

  /** Identificador único do gênero */
  private final String id;
  /** Nome do gênero */
  private String name;
  /** Data de criação do gênero */
  private final LocalDate creationDate;
  /** Lista de usuários que preferem este gênero */
  private List<User> users;
  /** Lista de conteúdos deste gênero */
  private List<Content> contents;

  /**
   * Construtor do gênero com todas as informações.
   * 
   * @param name nome do gênero
   * @param users lista de usuários interessados
   * @param contents lista de conteúdos do gênero
   */
  public Genre(String name, List<User> users, List<Content> contents) {
    this.id = UUID.randomUUID().toString();
    this.name = name;
    this.creationDate = LocalDate.now();
    this.users = users;
    this.contents = contents;
  }

  /**
   * Retorna o nome do gênero.
   * 
   * @return o nome
   */
  public String getName() {
    return name;
  }
  
  /**
   * Define o nome do gênero.
   * 
   * @param name o novo nome
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Retorna a lista de usuários interessados no gênero.
   * 
   * @return lista de usuários
   */
  public List<User> getUsers() {
    return users;
  }
  
  /**
   * Define a lista de usuários interessados no gênero.
   * 
   * @param users a nova lista de usuários
   */
  public void setUsers(List<User> users) {
    this.users = users;
  }

  /**
   * Retorna a lista de conteúdos do gênero.
   * 
   * @return lista de conteúdos
   */
  public List<Content> getContents() {
    return contents;
  }

  /**
   * Define a lista de conteúdos do gênero.
   * 
   * @param contents a nova lista de conteúdos
   */
  public void setContents(List<Content> contents) {
    this.contents = contents;
  }

  /**
   * Retorna o ID único do gênero.
   * 
   * @return o ID
   */
  public String getId() {
    return id;
  }

  /**
   * Retorna a data de criação do gênero.
   * 
   * @return a data de criação
   */
  public LocalDate getCreationDate() {
    return creationDate;
  }
}
