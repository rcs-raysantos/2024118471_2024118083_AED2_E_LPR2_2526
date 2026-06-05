package service.bst;

import edu.princeton.cs.algs4.RedBlackBST;
import model.users.User;
import model.utilities.Date;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestor de utilizadores baseado numa Árvore de Pesquisa Binária Balanceada (Red-Black BST).
 * Esta estrutura permite organizar os utilizadores por data de registo, facilitando
 * a pesquisa por intervalos temporais e a aplicação de filtros geográficos e nominais.
 */
public class UserBST {
    private RedBlackBST<Date, List<User>> bst; // chave: data, key: lista de usuarios

    // --------------------------- CONSTRUTOR ---------------------------

    /**
     * Inicializa uma nova UserBST criando uma árvore Red-Black vazia.
     */
    public UserBST() {
        bst = new RedBlackBST<>();
    }

    // --------------------------- MÉTODOS ---------------------------

    /**
     * Insere um utilizador na árvore. Se vários utilizadores se registarem na mesma data,
     * são todos agrupados numa lista associada a essa chave (data).
     * * @param new_user O utilizador a ser inserido.
     * @throws IllegalArgumentException se o utilizador for nulo.
     */
    public void insert(User new_user) {
        if(new_user == null){
            throw new IllegalArgumentException("user can't be null");
        }

        Date key = new Date(new_user.getRegistrationDate());
        List<User> list_user = bst.get(key);

        if(list_user == null){
            list_user = new ArrayList<>();
        }

        list_user.add(new_user); // adiciona o user na arvore
        bst.put(key, list_user); // atualiza a arvore com esse user
    }

    /**
     * Remove um utilizador da árvore. Se a lista de utilizadores para a data em questão
     * ficar vazia, a chave (data) é totalmente removida da árvore.
     * * @param user O utilizador a remover.
     * @throws IllegalArgumentException se o utilizador for nulo.
     */
    public void remove(User user) {
        if(user == null){
            throw new IllegalArgumentException("user can't be null");
        }

        Date key = new Date(user.getRegistrationDate());
        List<User> list_user = bst.get(key);

        if(list_user == null){ // já está vazio
            return;
        }

        list_user.remove(user); // remove o user da arvore

        if(list_user.isEmpty()){
            bst.delete(key); // apaga tudo
        } else {
            bst.put(key, list_user); // atualiza a arvore sem aquele user
        }
    }

    // --------------------------- alinea a) ---------------------------

    /**
     * Procura utilizadores registados num intervalo de datas.
     * * @param from Data de início do intervalo.
     * @param to Data de fim do intervalo.
     * @return Lista de utilizadores encontrados no período indicado.
     */
    public List<User> findByRegistrationRange(LocalDate from, LocalDate to) {
        List<User> result = new ArrayList<>();

        for (Date d : bst.keys(new Date(from), new Date(to))) {
            result.addAll(bst.get(d));
        }

        return result;
    }

    /**
     * Procura utilizadores pertencentes a uma região específica.
     * * @param code Código da região (ex: "PT").
     * @return Lista de utilizadores da região indicada.
     */
    public List<User> findByRegion(String code) {
        List<User> result = new ArrayList<>();

        for (Date d : bst.keys()) {
            for(User u : bst.get(d)){
                if(code.equalsIgnoreCase(u.getRegion().getCode())){
                    result.add(u);
                }
            }
        }

        return result;
    }

    /**
     * Filtra utilizadores por região dentro de um intervalo de datas de registo.
     * * @param code Código da região.
     * @param from Data inicial.
     * @param to Data final.
     * @return Lista de utilizadores filtrada.
     */
    public List<User> findByRegionAndDateRange(String code, LocalDate from, LocalDate to) {
        List<User> result = new ArrayList<>();

        for (Date d : bst.keys(new Date(from), new Date(to))) {
            for(User u : bst.get(d)){
                if(code.equalsIgnoreCase(u.getRegion().getCode())){
                    result.add(u);
                }
            }
        }

        return result;
    }

    // --------------------------- alinea b) ---------------------------

    /**
     * Pesquisa utilizadores cujo nome contém uma determinada sequência de texto.
     * * @param sub Texto (substring) a procurar no nome.
     * @return Lista de utilizadores com correspondência no nome.
     */
    public List<User> findByNameSubstring(String sub) {
        List<User> result = new ArrayList<>();
        String lower = sub.toLowerCase();

        for (Date d : bst.keys()) {
            for(User u : bst.get(d)){
                if(u.getName().toLowerCase().contains(lower)){
                    result.add(u);
                }
            }
        }

        return result;
    }

    /**
     * Filtra utilizadores por nome e região em simultâneo.
     * * @param sub Texto a procurar no nome.
     * @param region Código da região.
     * @return Lista de utilizadores que cumprem ambos os critérios.
     */
    public List<User> findByNameSubstringAndRegion(String sub, String region) {
        List<User> result = new ArrayList<>();
        String lower = sub.toLowerCase();

        for (Date d : bst.keys()) {
            for(User u : bst.get(d)){
                if(u.getName().toLowerCase().contains(lower) && region.equalsIgnoreCase(u.getRegion().getCode())){
                    result.add(u);
                }
            }
        }

        return result;
    }

    /**
     * Pesquisa avançada que combina nome, região e intervalo de datas.
     * * @param sub Nome ou parte do nome (opcional).
     * @param region Código da região (opcional).
     * @param from Data de início do intervalo.
     * @param to Data de fim do intervalo.
     * @return Lista de utilizadores encontrados.
     */
    public List<User> findByNameSubstringRegionAndDateRange(String sub, String region, LocalDate from, LocalDate to) {
        List<User> result = new ArrayList<>();
        String lower = sub == null ? "" : sub.toLowerCase(); // ? -> «sim» : -> «não»

        for (Date d : bst.keys(new Date(from), new Date(to))) {
            for(User u : bst.get(d)){
                if((lower.isEmpty() || u.getName().toLowerCase().contains(lower))
                        && (region == null || region.equalsIgnoreCase(u.getRegion().getCode()))){
                    result.add(u);
                }
            }
        }

        return result;
    }

    public void clear(){
        this.bst = new RedBlackBST<Date, List<User>>();
    }

    // --------------------------- UTILIDADES ---------------------------

    /** @return número de chaves (datas) na BST */
    public int size() {
        return bst.size();
    }

    /** @return data de registo mais antiga */
    public LocalDate min() {
        return bst.min().getDate();
    }

    /** @return data de registo mais recente */
    public LocalDate max() {
        return bst.max().getDate();
    }
}