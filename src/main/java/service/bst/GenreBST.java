package service.bst;

import edu.princeton.cs.algs4.RedBlackBST;
import model.content.Genre;
import model.utilities.Date;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestor de gêneros utilizando uma Árvore de Pesquisa Binária Balanceada (Red-Black BST).
 * Esta estrutura organiza os gêneros cronologicamente pela sua data de criação.
 * Permite realizar pesquisas por intervalos temporais e filtros nominais.
 */
public class GenreBST {

    private final RedBlackBST<Date, List<Genre>> bst; // chave: data, key: lista de gêneros

    // --------------------------- CONSTRUTOR ---------------------------

    /**
     * Inicializa uma nova GenreBST criando uma árvore Red-Black vazia.
     */
    public GenreBST() {
        bst = new RedBlackBST<>();
    }

    // --------------------------- MÉTODOS ---------------------------

    /**
     * Insere um novo gênero na árvore. Se vários gêneros se criarem na mesma data,
     * são todos agrupados numa lista associada a essa chave (data).
     *
     * @param new_genre O gênero a ser inserido.
     * @throws IllegalArgumentException se o gênero for nulo.
     */
    public void insert(Genre new_genre) {
        if(new_genre == null){
            throw new IllegalArgumentException("genre can't be null");
        }

        Date key = new Date(new_genre.getCreationDate());
        List<Genre> list_genre = bst.get(key);

        if(list_genre == null){
            list_genre = new ArrayList<>();
        }

        list_genre.add(new_genre); // adiciona o genre na arvore
        bst.put(key, list_genre); // atualiza a arvore com esse genre
    }

    /**
     * Remove um gênero da árvore. Se a lista de gêneros para a data em questão
     * ficar vazia, a chave (data) é totalmente removida da árvore.
     *
     * @param genre O gênero a remover.
     * @throws IllegalArgumentException se o gênero for nulo.
     */
    public void remove(Genre genre) {
        if(genre == null){
            throw new IllegalArgumentException("genre can't be null");
        }

        Date key = new Date(genre.getCreationDate());
        List<Genre> list_genre = bst.get(key);

        if(list_genre == null){ // já está vazio
            return;
        }

        list_genre.remove(genre); // remove o genre da arvore

        if(list_genre.isEmpty()){
            bst.delete(key); // apaga tudo
        } else {
            bst.put(key, list_genre); // atualiza a arvore sem aquele genre
        }
    }

    // --------------------------- UTILIDADES ---------------------------

    /** @return número de chaves (datas) na BST */
    public int size() {
        return bst.size();
    }

    /** @return data de criação mais antiga */
    public LocalDate min() {
        return bst.min().getDate();
    }

    /** @return data de criação mais recente */
    public LocalDate max() {
        return bst.max().getDate();
    }
}