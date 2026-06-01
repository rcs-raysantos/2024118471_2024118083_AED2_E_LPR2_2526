package model;

import model.support.Identifiable;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Classe abstrata que representa uma pessoa no sistema (ator, diretor, etc).
 * Implementa a interface Identifiable para ter um ID único.
 * 
 * @version 1.0
 */
public abstract class Person implements Identifiable, Serializable {
  private static final long serialVersionUID = 1L;

  /** Identificador único da pessoa */
  private String id;
  /** Nome da pessoa */
  private String name;
  /** Gênero da pessoa */
  private String gender;
  /** Data de nascimento da pessoa */
  private LocalDate birthDate;

  /**
   * Construtor da pessoa com nome e data de nascimento.
   * Gera um UUID aleatório como ID e não define gênero.
   * 
   * @param name nome da pessoa
   * @param birthDate data de nascimento da pessoa
   */
  protected Person(String name, LocalDate birthDate) {
    this(UUID.randomUUID().toString(), name, null, birthDate);
  }

  /**
   * Construtor da pessoa com nome, gênero e data de nascimento.
   * Gera um UUID aleatório como ID.
   * 
   * @param name nome da pessoa
   * @param gender gênero da pessoa
   * @param birthDate data de nascimento da pessoa
   */
  protected Person(String name, String gender, LocalDate birthDate) {
    this(UUID.randomUUID().toString(), name, gender, birthDate);
  }

  /**
   * Construtor completo da pessoa com todos os parâmetros.
   * 
   * @param id identificador único da pessoa
   * @param name nome da pessoa
   * @param gender gênero da pessoa
   * @param birthDate data de nascimento da pessoa
   */
  protected Person(String id, String name, String gender, LocalDate birthDate) {
    this.id = id;
    this.name = name;
    this.gender = gender;
    this.birthDate = birthDate;
  }

  /**
   * Retorna o identificador único da pessoa.
   * 
   * @return o ID da pessoa
   */
  @Override
  public String getId() {
    return id;
  }

  /**
   * Define o identificador da pessoa.
   * 
   * @param id o novo ID
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Retorna o nome da pessoa.
   * 
   * @return o nome
   */
  public String getName() {
    return name;
  }

  /**
   * Define o nome da pessoa.
   * 
   * @param name o novo nome
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Retorna o gênero da pessoa.
   * 
   * @return o gênero
   */
  public String getGender() {
    return gender;
  }

  /**
   * Define o gênero da pessoa.
   * 
   * @param gender o novo gênero
   */
  public void setGender(String gender) {
    this.gender = gender;
  }

  /**
   * Retorna a data de nascimento da pessoa.
   * 
   * @return a data de nascimento
   */
  public LocalDate getBirthDate() {
    return birthDate;
  }

  /**
   * Define a data de nascimento da pessoa.
   * 
   * @param birthDate a nova data de nascimento
   */
  public void setBirthDate(LocalDate birthDate) {
    this.birthDate = birthDate;
  }

  /**
   * Compara duas pessoas baseado em seus IDs.
   * 
   * @param o o objeto a comparar
   * @return true se as pessoas têm o mesmo ID, false caso contrário
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    if (getClass() != o.getClass()) return false;

    Person person = (Person) o;
    if (id == null) {
      return person.id == null;
    }
    return id.equals(person.id);
  }

  /**
   * Retorna o hash code da pessoa baseado no ID.
   * 
   * @return o hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
