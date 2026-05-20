package model.support;

/**
 * Interface que permite que um objeto seja streamável.
 * Implementando esta interface, uma classe pode ser transmitida em tempo real (streaming).
 * 
 * @version 1.0
 */
public interface Streamable {

  /**
   * Retorna a URL de streaming do objeto.
   */
  void getStreamUrl();
}
