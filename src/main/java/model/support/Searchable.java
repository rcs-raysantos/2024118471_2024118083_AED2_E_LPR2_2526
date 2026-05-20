package model.support;

/**
 * Interface que permite que um objeto seja pesquisável.
 * Implementando esta interface, uma classe pode ser buscada por uma query.
 * 
 * @version 1.0
 */
public interface Searchable {

  /**
   * Busca o objeto usando uma query de texto.
   * 
   * @param q a query/texto de busca
   */
  public void search(String q);

}