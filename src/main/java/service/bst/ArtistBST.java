package service.bst;

import edu.princeton.cs.algs4.RedBlackBST;
import model.artists.Artist;
import model.utilities.Date;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestor de artistas utilizando uma Árvore de Pesquisa Binária Balanceada (Red-Black BST).
 * Esta estrutura organiza os artistas cronologicamente pela sua data de nascimento.
 * Permite realizar pesquisas por intervalos temporais, nacionalidade, género e correspondência de nomes.
 */
public class ArtistBST {

    private final RedBlackBST<Date, List<Artist>> bst; // chave: data, key: lista de artistas

    // --------------------------- CONSTRUTOR ---------------------------

    /**
     * Inicializa uma nova ArtistBST criando uma árvore Red-Black vazia.
     */
    public ArtistBST() {
        bst = new RedBlackBST<>();
    }

    // --------------------------- MÉTODOS ---------------------------

    /**
     * Insere um novo artista na árvore.
     * Agrupa artistas com a mesma data de nascimento numa lista associada a essa chave.
     * * @param new_artist O artista a ser inserido.
     * @throws IllegalArgumentException se o artista for nulo.
     */
    public void insert(Artist new_artist) {
        if(new_artist == null){
            throw new IllegalArgumentException("artist can't be null");
        }

        Date key = new Date(new_artist.getBirthDate());
        List<Artist> list_artist = bst.get(key);

        if (list_artist == null) {
            list_artist = new ArrayList<>();
        }

        list_artist.add(new_artist);
        bst.put(key, list_artist);
    }

//    public void edit(Artist old_artist, Artist new_artist) {
//        if(old_artist == null){
//            throw new IllegalArgumentException("artist can't be null");
//        }
//
//        if(new_artist == null){
//            throw new IllegalArgumentException("artist can't be null");
//        }
//
//        Date key = new Date(old_artist.getBirthDate());
//        List<Artist> list_artist = bst.get(key);
//        if (list_artist == null) {
//            list_artist = new ArrayList<>();
//        }
//
//        list_artist.remove(old_artist);
//        list_artist.add(new_artist);
//    }

    /**
     * Remove um artista específico da árvore.
     * Se a lista de artistas para aquela data de nascimento ficar vazia, a chave é removida da BST.
     * * @param artist O objeto Artist a remover.
     * @throws IllegalArgumentException se o parâmetro for nulo.
     */
    public void remove(Artist artist) {
        if(artist == null){
            throw new IllegalArgumentException("artist can't be null");
        }

        Date key = new Date(artist.getBirthDate());
        List<Artist> list_artist = bst.get(key);

        if(list_artist == null){ // já está vazio
            return;
        }

        list_artist.remove(artist); // remove o artist da arvore

        if(list_artist.isEmpty()){
            bst.delete(key); // apaga tudo
        } else {
            bst.put(key, list_artist); // atualiza a arvore sem aquele artist
        }
    }

    // --------------------------- alinea c) ---------------------------

    /**
     * Obtém todos os artistas nascidos num determinado intervalo de datas.
     * * @param from Data de início do intervalo.
     * @param to Data de fim do intervalo.
     * @return Lista de artistas ordenada cronologicamente por nascimento.
     */
    public List<Artist> findByBirthDateRange(LocalDate from, LocalDate to) {
        List<Artist> result = new ArrayList<>();

        for (Date d : bst.keys(new Date(from), new Date(to))) {
            result.addAll(bst.get(d));
        }

        return result;
    }

    /**
     * Procura artistas por uma nacionalidade específica.
     * * @param nationality Nacionalidade a pesquisar (ignora maiúsculas/minúsculas).
     * @return Lista de artistas que correspondem à nacionalidade fornecida.
     */
    public List<Artist> findByNationality(String nationality) {
        List<Artist> result = new ArrayList<>();

        for (Date d : bst.keys()) {
            for (Artist a : bst.get(d)) {
                if (nationality.equalsIgnoreCase(a.getNationality())) {
                    result.add(a);
                }
            }
        }

        return result;
    }

    /**
     * Pesquisa avançada combinando nacionalidade, género e intervalo de datas de nascimento.
     * * @param nationality Nacionalidade (null para ignorar filtro).
     * @param gender Género (null para ignorar filtro).
     * @param from Data inicial.
     * @param to Data final.
     * @return Lista filtrada de artistas.
     */
    public List<Artist> findByNationalityGenderAndBirthRange(String nationality, String gender, LocalDate from, LocalDate to) {
        List<Artist> result = new ArrayList<>();

        for (Date d : bst.keys(new Date(from), new Date(to))) {
            for (Artist a : bst.get(d)) {
                boolean nat = nationality == null || nationality.equalsIgnoreCase(a.getNationality());
                boolean gen = gender == null || gender.equalsIgnoreCase(a.getGender());
                if (nat && gen) { // se ambos estiverem ok, aí adiciona
                    result.add(a);
                }
            }
        }
        return result;
    }

    // --------------------------- alinea d) ---------------------------

    /**
     * Pesquisa artistas cujo nome contém uma determinada substring.
     * * @param sub Texto a procurar no nome (case-insensitive).
     * @return Lista de artistas encontrados.
     */
    public List<Artist> findByNameSubstring(String sub) {
        List<Artist> result = new ArrayList<>();
        String lower = sub.toLowerCase();

        for (Date d : bst.keys()) {
            for(Artist u : bst.get(d)){
                if(u.getName().toLowerCase().contains(lower)){
                    result.add(u);
                }
            }
        }

        return result;
    }

    /**
     * Pesquisa completa integrando nome, nacionalidade, género e intervalo temporal.
     * * @param substring Parte do nome a pesquisar.
     * @param nationality Nacionalidade pretendida.
     * @param gender Género pretendido.
     * @param from Data de início.
     * @param to Data de fim.
     * @return Lista de artistas que satisfazem todos os requisitos.
     */
    public List<Artist> findByNameSubstringNationalityGenderAndBirthRange(String substring, String nationality, String gender, LocalDate from, LocalDate to) {
        List<Artist> result = new ArrayList<>();
        String lower = substring == null ? "" : substring.toLowerCase();

        for (Date d : bst.keys(new Date(from), new Date(to))) {
            for (Artist a : bst.get(d)) {
                boolean sub = lower.isEmpty() || a.getName().toLowerCase().contains(lower);
                boolean nat = nationality == null || nationality.equalsIgnoreCase(a.getNationality());
                boolean gen = gender == null || gender.equalsIgnoreCase(a.getGender());
                if (sub && nat && gen) {
                    result.add(a);
                }
            }
        }
        return result;
    }

    // --------------------------- UTILIDADES ---------------------------

    /**
     * @return O número de chaves (datas de nascimento únicas) na árvore.
     */
    public int size() {
        return bst.size();
    }

    /**
     * @return A data de nascimento mais antiga presente na árvore.
     */
    public LocalDate min() {
        return bst.min().getDate();
    }

    /**
     * @return A data de nascimento mais recente presente na árvore.
     */
    public LocalDate max() {
        return bst.max().getDate();
    }
}