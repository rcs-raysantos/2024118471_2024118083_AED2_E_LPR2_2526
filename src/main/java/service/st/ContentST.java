package service.st;

import edu.princeton.cs.algs4.SeparateChainingHashST;
import model.content.Content;
import model.content.Documentary;
import model.content.Movie;
import model.content.Series;

import java.util.ArrayList;
import java.util.List;

/**
 * Tabela de Símbolos para a persistência de conteúdos multimédia utilizando Hash Table.
 * Utiliza o algoritmo de Separate Chaining para resolver colisões, proporcionando
 * um acesso eficiente aos conteúdos através do seu identificador único (ID).
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

        if(uptaded_content == null){
            throw new IllegalArgumentException("content can't be null");
        }

        Content contentExistente = st.get(id);

        if (uptaded_content.getTitle() != null && !uptaded_content.getTitle().trim().isEmpty()) {
            contentExistente.setTitle(uptaded_content.getTitle().trim());
        }

        if (uptaded_content.getReleaseDate() != null) {
            contentExistente.setReleaseDate(uptaded_content.getReleaseDate());
        }

        if (uptaded_content.getDuration() > 0) {
            contentExistente.setDuration(uptaded_content.getDuration());
        }

        if (uptaded_content.getSynopsis() != null) {
            contentExistente.setSynopsis(uptaded_content.getSynopsis());
        }

        if (contentExistente instanceof Movie && uptaded_content instanceof Movie) {
            Movie movieExistente = (Movie) contentExistente;
            Movie movieNovo = (Movie) uptaded_content;

            movieExistente.setBudget(movieNovo.getBudget());
            movieExistente.setBoxOffice(movieNovo.getBoxOffice());

        } else if (contentExistente instanceof Series && uptaded_content instanceof Series) {
            Series seriesExistente = (Series) contentExistente;
            Series seriesNova = (Series) uptaded_content;

            seriesExistente.setSeasons(seriesNova.getSeasons());
            seriesExistente.setEpisodes(seriesNova.getEpisodes());

        } else if (contentExistente instanceof Documentary && uptaded_content instanceof Documentary) {
            Documentary docExistente = (Documentary) contentExistente;
            Documentary docNovo = (Documentary) uptaded_content;

            if (docNovo.getTopic() != null) {
                docExistente.setTopic(docNovo.getTopic());
            }
            if (docNovo.getNarrator() != null) {
                docExistente.setNarrator(docNovo.getNarrator());
            }
        }

        st.put(id, contentExistente);
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