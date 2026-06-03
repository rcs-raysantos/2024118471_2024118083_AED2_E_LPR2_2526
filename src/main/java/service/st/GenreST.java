package service.st;

import edu.princeton.cs.algs4.SeparateChainingHashST;
import model.content.Genre;

import java.util.ArrayList;
import java.util.List;

/**
 * Tabela de Símbolos para a persistência de gêneros baseada em Hash Table.
 * Utiliza o algoritmo de Separate Chaining para tratar colisões, permitindo
 * operações de inserção, remoção e procura com complexidade média de $O(1)$.
 */
public class GenreST {
    private final SeparateChainingHashST<String, Genre> st; // estrutura: key = id, value = genre

    // --------------------------- CONSTRUTOR ---------------------------

    /**
     * Inicializa uma nova Tabela de Símbolos para gêneros.
     */
    public GenreST() {
        st = new SeparateChainingHashST<String, Genre>(); // inicializa a symbol table
    }

    // --------------------------- MÉTODOS ---------------------------

    /**
     * Insere um novo gênero na tabela.
     *
     * @param new_genre O gênero a ser registado.
     * @throws IllegalArgumentException se o gênero for nulo ou se o ID já existir na tabela.
     */
    public void insert(Genre new_genre) {
        if(new_genre == null){
            throw new IllegalArgumentException("genre can't be null");
        }

        if(st.contains(new_genre.getId())){
            throw new IllegalArgumentException("this id has already a genre: " + new_genre.getId());
        }

        st.put(new_genre.getId(), new_genre);
    }

    /**
     * Remove um gênero da tabela através do seu identificador.
     *
     * @param id O identificador único do gênero a remover.
     * @throws IllegalArgumentException se o gênero não for encontrado.
     */
    public void remove(String id) {
        if(!st.contains(id)){
            throw new IllegalArgumentException("genre does not exist: " + id);
        }

        st.delete(id);
    }

    /**
     * Obtém gênero por ID.
     * @param id identificador único
     * @return Genre ou null se não existir
     */
    public Genre get(String id) {
        return st.get(id);
    }

    /**
     * Converte todos os gêneros presentes na tabela para uma lista.
     *
     * @return Uma List contendo todos os gêneros registados.
     */
    public List<Genre> listAll() {
        List<Genre> result = new ArrayList<>();
        for(String id : st.keys()){
            result.add(st.get(id));
        }
        return result;
    }

    /**
     * Atualiza os dados de um gênero existente.
     *
     * @param id O identificador do gênero a editar.
     * @param uptaded_genre O objeto Genre com os dados atualizados.
     * @throws IllegalArgumentException se o gênero não existir na tabela.
     */
    public void edit(String id, Genre uptaded_genre) {
        if(!st.contains(id)){
            throw new IllegalArgumentException("genre does not exist: " + id);
        }

        st.put(id, uptaded_genre);
    }

    // --------------------------- UTILIDADES ---------------------------

    /**
     * @return O número total de gêneros registados.
     */
    public int size(){
        return st.size();
    }

    /**
     * @return true se a tabela não contiver gêneros, false caso contrário.
     */
    public boolean isEmpty(){
        return st.isEmpty();
    }

    /**
     * Verifica se um determinado ID já se encontra registado.
     *
     * @param id O identificador a procurar.
     * @return true se existir, false caso contrário.
     */
    public boolean contains(String id) {
        return st.contains(id);
    }

    /**
     * Permite iterar sobre todas as chaves (IDs) presentes na tabela.
     *
     * @return Um Iterable com as chaves da tabela.
     */
    public Iterable<String> keys(){
        return st.keys();
    }
}