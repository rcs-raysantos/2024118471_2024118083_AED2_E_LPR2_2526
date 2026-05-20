package service.st;

import edu.princeton.cs.algs4.SeparateChainingHashST;
import model.content.Content;

import java.util.ArrayList;
import java.util.List;

/**
 * Tabela de Símbolos para a persistência de conteúdos multimédia utilizando Hash Table.
 * Utiliza o algoritmo de Separate Chaining para resolver colisões, proporcionando
 * um acesso eficiente aos conteúdos através do seu identificador único (ID).
 * * @author Rayssa Santos
 * @version 1.0
 */
public class ContentST {
    private final SeparateChainingHashST<String, Content> st; // estrutura: key = id, value = content

    // --------------------------- CONSTRUTOR ---------------------------

    /**
     * Inicializa uma nova Tabela de Símbolos para conteúdos.
     */
    public ContentST() {
        st = new SeparateChainingHashST<String, Content>(); // inicializa a symbol table
    }

    // --------------------------- MÉTODOS ---------------------------

    /**
     * Insere um novo conteúdo na tabela.
     * * @param new_content O objeto Content a ser inserido.
     * @throws IllegalArgumentException se o conteúdo for nulo ou se o ID já estiver registado.
     */
    public void insert(Content new_content) {
        if(new_content == null){
            throw new IllegalArgumentException("content can't be null");
        }

        if(st.contains(new_content.getId())){
            throw new IllegalArgumentException("this id has already a content: " + new_content.getId());
        }

        st.put(new_content.getId(), new_content);
    }

    /**
     * Remove um conteúdo da tabela através do seu ID.
     * * @param id O identificador único do conteúdo a remover.
     * @throws IllegalArgumentException se o ID não existir na tabela.
     */
    public void remove(String id) {
        if(!st.contains(id)){
            throw new IllegalArgumentException("content does not exist: " + id);
        }

        st.delete(id);
    }

    /**
     * Retorna uma lista com todos os conteúdos armazenados na tabela.
     * * @return Uma List contendo todos os objetos Content.
     */
    public List<Content> listAll() {
        List<Content> result = new ArrayList<>();
        for(String id : st.keys()){
            result.add(st.get(id));
        }
        return result;
    }

    /**
     * Atualiza os dados de um conteúdo existente.
     * * @param id O ID do conteúdo a ser editado.
     * @param uptaded_content O novo objeto Content com os dados atualizados.
     * @throws IllegalArgumentException se o conteúdo não for encontrado.
     */
    public void edit(String id, Content uptaded_content) {
        if(!st.contains(id)){
            throw new IllegalArgumentException("content does not exist: " + id);
        }

        st.put(id, uptaded_content);
    }

    // --------------------------- UTILIDADES ---------------------------

    /**
     * @return O número total de conteúdos na tabela.
     */
    public int size(){
        return st.size();
    }

    /**
     * @return true se a tabela estiver vazia, false caso contrário.
     */
    public boolean isEmpty(){
        return st.isEmpty();
    }

    /**
     * Verifica a existência de um conteúdo pelo ID.
     * * @param id O identificador a procurar.
     * @return true se o conteúdo existir, false caso contrário.
     */
    public boolean contains(String id) {
        return st.contains(id);
    }

    /**
     * Permite obter um iterador para todas as chaves (IDs) da tabela.
     * * @return Um Iterable com os IDs dos conteúdos.
     */
    public Iterable<String> keys(){
        return st.keys();
    }

    public Content get(String id) {
        return st.get(id);
    }
}