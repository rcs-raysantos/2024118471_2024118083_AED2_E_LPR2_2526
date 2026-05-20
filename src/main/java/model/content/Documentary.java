package model.content;

import java.time.LocalDate;

/**
 * Classe que representa um documentário no sistema.
 * Um documentário é um tipo de conteúdo com tópico e narrador específicos.
 *
 * @version 1.0
 */
public class Documentary extends Content {

  /** Tópico principal do documentário */
  private String topic;
  /** Narrador do documentário */
  private String narrator;

  /**
   * Construtor do documentário com todas as informações.
   * 
   * @param title título do documentário
   * @param releaseDate data de lançamento
   * @param duration duração em minutos
   * @param synopsis sinopse do documentário
   * @param topic tópico principal
   * @param narrator nome do narrador
   */
  public Documentary(String title, LocalDate releaseDate, int duration, String synopsis, String topic, String narrator) {
    super(title, releaseDate, duration, synopsis);
    this.topic = topic;
    this.narrator = narrator;
  }

  /**
   * Retorna o narrador do documentário.
   * 
   * @return o narrador
   */
  public String getNarrator() {
    return narrator;
  }
  
  /**
   * Define o narrador do documentário.
   * 
   * @param narrator o novo narrador
   */
  public void setNarrator(String narrator) {
    this.narrator = narrator;
  }

  /**
   * Retorna o tópico do documentário.
   * 
   * @return o tópico
   */
  public String getTopic() {
    return topic;
  }
  
  /**
   * Define o tópico do documentário.
   * 
   * @param topic o novo tópico
   */
  public void setTopic(String topic) {
    this.topic = topic;
  }

  /**
   * Retorna a URL de streaming do documentário.
   * Implementa a interface Streamable.
   */
  @Override
  public void getStreamUrl() {

  }
}