package model.support;

/**
 * Interface que permite que um objeto seja avaliável.
 * Implementando esta interface, uma classe pode receber avaliações de 1 a 5.
 * 
 * @version 1.0
 */
public interface Ratable {

  /**
   * Avalia o objeto com uma pontuação.
   * 
   * @param score a pontuação da avaliação
   */
  public void rate(int score);

}