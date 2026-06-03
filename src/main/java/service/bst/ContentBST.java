package service.bst;

import edu.princeton.cs.algs4.RedBlackBST;
import model.content.Content;
import model.utilities.Date;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestor de conteúdos multimédia utilizando uma Árvore de Pesquisa Binária Balanceada (Red-Black BST).
 * Esta estrutura organiza os conteúdos cronologicamente através da sua data de lançamento.
 * Permite a aplicação de filtros complexos (género, tipo, duração) e pesquisas por intervalos temporais.
 */
public class ContentBST {

    private final RedBlackBST<Date, List<Content>> bst; // chave: data, key: lista de conteudos

    // --------------------------- CONSTRUTOR ---------------------------

    /**
     * Inicializa uma nova instância de ContentBST.
     */
    public ContentBST() {
        bst = new RedBlackBST<>();
    }

    // --------------------------- MÉTODOS ---------------------------

    /**
     * Insere um novo conteúdo na árvore.
     * Se já existirem conteúdos na mesma data, o novo item é adicionado à lista existente.
     * * @param new_content O conteúdo a ser inserido.
     * @throws IllegalArgumentException se o conteúdo for nulo.
     */
    public void insert(Content new_content) {
        if(new_content == null){
            throw new IllegalArgumentException("content can't be null");
        }

        Date key = new Date(new_content.getReleaseDate());
        List<Content> list_content = bst.get(key);

        if(list_content == null){
            list_content = new ArrayList<>();
        }

        list_content.add(new_content); // adiciona o content na arvore
        bst.put(key, list_content); // atualiza a arvore com esse content
    }

    /**
     * Remove um conteúdo da árvore.
     * Se a lista de conteúdos para a data especificada ficar vazia, a chave é removida da BST.
     * * @param content O objeto Content a remover.
     * @throws IllegalArgumentException se o conteúdo for nulo.
     */
    public void remove(Content content) {
        if(content == null){
            throw new IllegalArgumentException("artist can't be null");
        }

        Date key = new Date(content.getReleaseDate());
        List<Content> list_content = bst.get(key);

        if(list_content == null){ // já está vazio
            return;
        }

        list_content.remove(content); // remove o content da arvore

        if(list_content.isEmpty()){
            bst.delete(key); // apaga tudo
        } else {
            bst.put(key, list_content); // atualiza a arvore sem aquele content
        }
    }

    // --------------------------- alinea e) ---------------------------

    /**
     * Procura todos os conteúdos lançados entre duas datas (inclusive).
     * * @param from Data de início do intervalo.
     * @param to Data de fim do intervalo.
     * @return Lista de conteúdos encontrados no intervalo temporal.
     */
    public List<Content> findByDateRange(LocalDate from, LocalDate to) {
        List<Content> result = new ArrayList<>();

        for (Date d : bst.keys(new Date(from), new Date(to))) {
            result.addAll(bst.get(d));
        }

        return result;
    }

    /**
     * Filtra os conteúdos por um tipo específico de classe (ex: Movie.class).
     * * @param type A classe correspondente ao tipo de conteúdo desejado.
     * @return Lista de conteúdos que são instâncias do tipo fornecido.
     */
    // Class<?> type -> significa: qualquer classe de qualquer tipo :)
    public List<Content> findByType(Class<?> type) {
        List<Content> result = new ArrayList<>();

        for(Date d : bst.keys()){ // para cada d na arvore (gaveta)
            for(Content c : bst.get(d)){ // para cada c na lista da arvore (documento)
                if(type.isInstance(c)){ // verifica se do tipo que foi colocado
                    result.add(c);
                }
            }
        }

        return result;
    }

    /**
     * Procura conteúdos que possuam um determinado género na sua lista de géneros.
     * * @param genreName Nome do género (ignora maiúsculas/minúsculas).
     * @return Lista de conteúdos encontrados com o género especificado.
     */
    public List<Content> findByGenre(String genreName) {
        List<Content> result = new ArrayList<>();

        for (Date d : bst.keys()) {
            for (Content c : bst.get(d)) {
                if (c.getGenres().stream().anyMatch(g -> g.getName().equalsIgnoreCase(genreName))) {
                    result.add(c);
                }
            }
        }

        return result;
    }

    /**
     * Pesquisa avançada combinando tipo de classe, género e intervalo de datas.
     * * @param type Classe do tipo de conteúdo (null para ignorar).
     * @param genreName Nome do género (null para ignorar).
     * @param from Data de início do intervalo.
     * @param to Data de fim do intervalo.
     * @return Lista de conteúdos que satisfazem todos os critérios fornecidos.
     */
    public List<Content> findByTypeGenreAndDateRange(Class<?> type, String genreName, LocalDate from, LocalDate to) {
        List<Content> result = new ArrayList<>();

        for (Date d : bst.keys(new Date(from), new Date(to))) {
            for (Content c : bst.get(d)) {
                boolean tp  = type == null || type.isInstance(c);
                boolean genre = genreName == null || c.getGenres().stream().anyMatch(g -> g.getName().equalsIgnoreCase(genreName));
                if (tp && genre) {
                    result.add(c);
                }
            }
        }

        return result;
    }

    // --------------------------- alinea f) ---------------------------

    /**
     * Procura conteúdos cujo título contém uma determinada sequência de caracteres.
     * * @param sub A substring a pesquisar no título (case-insensitive).
     * @return Lista de conteúdos correspondentes.
     */
    public List<Content> findByTitleSubstring(String sub) {
        List<Content> result = new ArrayList<>();
        String lower = sub.toLowerCase();

        for (Date d : bst.keys()) {
            for(Content u : bst.get(d)){
                if(u.getTitle().toLowerCase().contains(lower)){
                    result.add(u);
                }
            }
        }

        return result;
    }

    /**
     * Pesquisa completa integrando substring de título, tipo, género e intervalo de datas.
     * * @param sub Parte do título a pesquisar.
     * @param type Classe do tipo de conteúdo.
     * @param genreName Nome do género.
     * @param from Data inicial.
     * @param to Data final.
     * @return Lista filtrada de conteúdos.
     */
    public List<Content> findByTitleSubstringTypeGenreAndDateRange(String sub, Class<?> type, String genreName, LocalDate from, LocalDate to) {
        List<Content> result = new ArrayList<>();
        String lower = sub == null ? "" : sub.toLowerCase();

        for (Date d : bst.keys(new Date(from), new Date(to))) {
            for (Content c : bst.get(d)) {
                boolean substring   = lower.isEmpty() || c.getTitle().toLowerCase().contains(lower);
                boolean tp  = type == null || type.isInstance(c);
                boolean genre = genreName == null || c.getGenres().stream().anyMatch(g -> g.getName().equalsIgnoreCase(genreName));
                if (substring && tp && genre) {
                    result.add(c);
                }
            }
        }
        return result;
    }

    // --------------------------- alinea g) ---------------------------

    /**
     * Filtra conteúdos dentro de um intervalo de duração específica.
     * * @param minMin Duração mínima em minutos.
     * @param maxMin Duração máxima em minutos.
     * @return Lista de conteúdos com duração compreendida no intervalo.
     */
    public List<Content> findByDurationRange(int minMin, int maxMin) {
        List<Content> result = new ArrayList<>();

        for (Date d : bst.keys()) {
            for (Content c : bst.get(d)) {
                int dur = c.getDuration();
                if (dur >= minMin && dur <= maxMin) {
                    result.add(c);
                }
            }
        }

        return result;
    }

    // --------------------------- UTILIDADES ---------------------------

    /**
     * @return O número total de chaves (datas distintas) presentes na árvore.
     */
    public int size() {
        return bst.size();
    }

    /**
     * @return A data de lançamento mais antiga registada.
     */
    public LocalDate min() {
        return bst.min().getDate();
    }

    /**
     * @return A data de lançamento mais recente registada.
     */
    public LocalDate max() {
        return bst.max().getDate();
    }
}