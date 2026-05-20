package model.users;

import model.Person;
import model.content.Genre;
import model.support.Searchable;
import model.utilities.Region;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Classe que representa um usuário no sistema.
 * Um usuário tem email, senha, data de registro e preferências de gênero.
 * Implementa a interface Searchable para buscar conteùdos.
 * 

 * @version 1.0
 */
public class User extends Person implements Searchable {

  /** Email do usuário */
  private String email;
  /** Senha do usuário */
  private String password;
  /** Data de registro do usuário no sistema */
  private LocalDate registrationDate;
  /** Região/localização do usuário */
  private Region region;
  /** Lista de gêneros favoritos do usuário */
  private List<Genre> genres;

  /**
   * Construtor do usuário sem gênero especificado.
   *
   * @param name nome do usuário
   * @param email email do usuário
   * @param password senha do usuário
   * @param registrationDate data de registro
   * @param region região do usuário
   * @param birthDate data de nascimento do usuário
   */
  public User(String name, String email, String password, LocalDate registrationDate, Region region, LocalDate birthDate) {
    this(name, null, email, password, registrationDate, region, birthDate);
  }

  /**
   * Construtor completo do usuário com todas as informações.
   *
   * @param name nome do usuário
   * @param gender gênero do usuário
   * @param email email do usuário
   * @param password senha do usuário
   * @param registrationDate data de registro
   * @param region região do usuário
   * @param birthDate data de nascimento do usuário
   */
  public User(String name, String gender, String email, String password, LocalDate registrationDate, Region region, LocalDate birthDate) {
    super(UUID.randomUUID().toString(), name, gender, birthDate);
    this.email = email;
    this.password = password;
    this.registrationDate = registrationDate;
    this.region = region;
    this.genres = new ArrayList<>();
  }

  /**
   * Retorna a data de registro do usuário.
   * 
   * @return a data de registro
   */
  public LocalDate getRegistrationDate() {
    return this.registrationDate;
  }

  /**
   * Retorna a lista de gêneros favoritos do usuário.
   * 
   * @return cópia da lista de gêneros
   */
  public List<Genre> getGenres() {
    return new ArrayList<>(genres);
  }

  /**
   * Adiciona um gênero à lista de favoritos do usuário.
   * 
   * @param g o gênero a adicionar
   */
  public void addGenre(Genre g) {
    if (g != null && !genres.contains(g)) {
      genres.add(g);
    }
  }

  /**
   * Remove um gênero da lista de favoritos do usuário.
   * 
   * @param g o gênero a remover
   */
  public void removeGenre(Genre g) {
    genres.remove(g);
  }

  /**
   * Retorna o email do usuário.
   * 
   * @return o email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Define o email do usuário.
   * 
   * @param email o novo email
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Retorna a senha do usuário.
   * 
   * @return a senha
   */
  public String getPassword() {
    return password;
  }

  /**
   * Define a senha do usuário.
   * 
   * @param password a nova senha
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Define a data de registro do usuário.
   * 
   * @param registrationDate a nova data de registro
   */
  public void setRegistrationDate(LocalDate registrationDate) {
    this.registrationDate = registrationDate;
  }

  /**
   * Retorna a região do usuário.
   * 
   * @return a região
   */
  public Region getRegion() {
    return region;
  }

  /**
   * Define a região do usuário.
   * 
   * @param region a nova região
   */
  public void setRegion(Region region) {
    this.region = region;
  }

  /**
   * Busca por uma query no sistema.
   * Implementa a interface Searchable.
   * 
   * @param q a query de busca
   */
  @Override
  public void search(String q) {

  }

}
