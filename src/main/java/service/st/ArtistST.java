package service.st;

import edu.princeton.cs.algs4.SeparateChainingHashST;
import model.artists.Artist;

import java.util.ArrayList;
import java.util.List;

/**
 * Tabela de Símbolos para a persistência de artistas utilizando uma Hash Table.
 * Implementa a técnica de Separate Chaining para gestão de colisões, garantindo
 * operações de pesquisa, inserção e remoção altamente eficientes através do ID do artista.
 */
public class ArtistST {
    private final SeparateChainingHashST<String, Artist> st; // estrutura: key = id, value = artists

    // --------------------------- CONSTRUTOR ---------------------------

    /**
     * Inicializa uma nova Tabela de Símbolos para artistas.
     */
    public ArtistST() {
        st = new SeparateChainingHashST<String, Artist>(); // inicializa a symbol table
    }

    // --------------------------- MÉTODOS ---------------------------

    /**
     * Insere um novo artista na tabela.
     * * @param new_artist O objeto Artist a ser inserido.
     * @throws IllegalArgumentException se o artista for nulo ou se o ID já estiver em uso.
     */
    public void insert(Artist new_artist) {
        if(new_artist == null){
            throw new IllegalArgumentException("artists can't be null");
        }

        if(st.contains(new_artist.getId())){
            throw new IllegalArgumentException("this id has already an artists: " + new_artist.getId());
        }

        st.put(new_artist.getId(), new_artist);
    }

    /**
     * Remove um artista da tabela através do seu identificador.
     * * @param id O identificador único do artista a remover.
     * @throws IllegalArgumentException se o artista não for encontrado.
     */
    public void remove(String id) {
        if(!st.contains(id)){
            throw new IllegalArgumentException("artists does not exist: " + id);
        }

        st.delete(id);
    }

    /**
     * Atualiza os dados de um artista existente.
     * * @param id O identificador do artista a editar.
     * @param uptaded_artist O objeto Artist com as novas informações.
     * @throws IllegalArgumentException se o artista não existir na tabela.
     */
    public void edit(String id, Artist uptaded_artist) {
        if(!st.contains(id)){
            throw new IllegalArgumentException("artists does not exist: " + id);
        }

        st.put(id, uptaded_artist);
    }

    /**
     * Retorna uma lista contendo todos os artistas registados na tabela.
     * * @return Uma List com todos os objetos Artist.
     */
    public List<Artist> listAll() {
        List<Artist> result = new ArrayList<>();
        for(String id : st.keys()){
            result.add(st.get(id));
        }
        return result;
    }

    // --------------------------- UTILIDADES ---------------------------

    /**
     * @return O número total de artistas armazenados.
     */
    public int size(){
        return st.size();
    }

    /**
     * @return true se a tabela estiver vazia, false caso contrário.
     */
    public boolean isEmpty() { return st.isEmpty(); }

    /**
     * Verifica se existe um artista registado com o ID fornecido.
     * * @param id O identificador a procurar.
     * @return true se o artista existir na tabela.
     */
    public boolean contains(String id) {
        return st.contains(id);
    }

    /**
     * Permite a iteração sobre todas as chaves (IDs) da tabela.
     * * @return Um Iterable contendo as chaves da tabela de símbolos.
     */
    public Iterable<String> keys(){
        return st.keys();
    }

    public Artist get(String id) {
        return st.get(id);
    }
}