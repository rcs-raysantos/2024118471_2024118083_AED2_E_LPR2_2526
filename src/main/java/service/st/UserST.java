package service.st;

import edu.princeton.cs.algs4.SeparateChainingHashST;
import model.users.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Tabela de Símbolos para a persistência de utilizadores baseada em Hash Table.
 * Utiliza o algoritmo de Separate Chaining para tratar colisões, permitindo
 * operações de inserção, remoção e procura com complexidade média de $O(1)$.
 */
public class UserST {
    private SeparateChainingHashST<String, User> st; // estrutura: key = id, value = user

    // --------------------------- CONSTRUTOR ---------------------------

    /**
     * Inicializa uma nova Tabela de Símbolos para utilizadores.
     */
    public UserST() {
        st = new SeparateChainingHashST<String, User>(); // inicializa a symbol table
    }

    // --------------------------- MÉTODOS ---------------------------

    /**
     * Insere um novo utilizador na tabela.
     * * @param new_user O utilizador a ser registado.
     * @throws IllegalArgumentException se o utilizador for nulo ou se o ID já existir na tabela.
     */
    public void insert(User new_user) {
        if(new_user == null){
            throw new IllegalArgumentException("user can't be null");
        }

        if(st.contains(new_user.getId())){
            throw new IllegalArgumentException("this id has already an user: " + new_user.getId());
        }

        st.put(new_user.getId(), new_user);
    }

    /**
     * Remove um utilizador da tabela através do seu identificador.
     * * @param id O identificador único do utilizador a remover.
     * @throws IllegalArgumentException se o utilizador não for encontrado.
     */
    public void remove(String id) {
        if(!st.contains(id)){
            throw new IllegalArgumentException("user does not exist: " + id);
        }

        st.delete(id);
    }

    /**
     * Obtém utilizador por ID.
     * @param id identificador único
     * @return User ou null se não existir
     */
    public User get(String id) {
        return st.get(id);
    }

    /**
     * Converte todos os utilizadores presentes na tabela para uma lista.
     * * @return Uma List contendo todos os utilizadores registados.
     */
    public List<User> listAll() {
        List<User> result = new ArrayList<>();
        for(String id : st.keys()){
            result.add(st.get(id));
        }
        return result;
    }

    /**
     * Atualiza os dados de um utilizador existente.
     * * @param id O identificador do utilizador a editar.
     * @param updated_user O objeto User com os dados atualizados.
     * @throws IllegalArgumentException se o utilizador não existir na tabela.
     */
    public void edit(String id, User updated_user) {
        if(!st.contains(id)){
            throw new IllegalArgumentException("user does not exist: " + id);
        }

        if(updated_user == null){
            throw new IllegalArgumentException("user can't be null");
        }

        User userExistente = st.get(id);

        if (updated_user.getName() != null) {
            userExistente.setName(updated_user.getName());
        }

        if (updated_user.getEmail() != null) {
            userExistente.setEmail(updated_user.getEmail());
        }

        if (updated_user.getPassword() != null) {
            userExistente.setPassword(updated_user.getPassword());
        }

        if(updated_user.getBirthDate() != null) {
            userExistente.setBirthDate(updated_user.getBirthDate());
        }

        st.put(id, userExistente);
    }

    public void clear() {
        this.st = new SeparateChainingHashST<String, User>(); // substitui a velha pela nova
    }

    // --------------------------- UTILIDADES ---------------------------

    /**
     * @return O número total de utilizadores registados.
     */
    public int size(){
        return st.size();
    }

    /**
     * @return true se a tabela não contiver utilizadores, false caso contrário.
     */
    public boolean isEmpty(){
        return st.isEmpty();
    }

    /**
     * Verifica se um determinado ID já se encontra registado.
     * * @param id O identificador a procurar.
     * @return true se existir, false caso contrário.
     */
    public boolean contains(String id) {
        return st.contains(id);
    }

    /**
     * Permite iterar sobre todas as chaves (IDs) presentes na tabela.
     * * @return Um Iterable com as chaves da tabela.
     */
    public Iterable<String> keys(){
        return st.keys();
    }
}