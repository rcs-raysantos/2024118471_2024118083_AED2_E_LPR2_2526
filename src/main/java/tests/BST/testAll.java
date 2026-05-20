package tests.BST;

/**
 * @brief Classe principal de orquestração para os testes das Binary Search Trees (R3).
 * * Esta classe atua como um "Test Suite" para a fase R3, centralizando a execução
 * de todos os testes unitários relacionados com as Red-Black BSTs de Utilizadores,
 * Conteúdos, Artistas e Géneros. É essencial para validar as funcionalidades de
 * ordenação e pesquisas por intervalo.
 * @version 1.0
 * @date 2026
 */
public class testAll {

    /**
     * @brief Ponto de entrada principal para a bateria de testes R3.
     * * Este método imprime o cabeçalho informativo, executa sequencialmente
     * os métodos de teste de cada estrutura BST e confirma a conclusão
     * do processo no terminal.
     * * @param args Argumentos da linha de comando (não utilizados).
     * * @see UserBSTTest#runAll()
     * @see ContentBSTTest#runAll()
     * @see ArtistBSTTest#runAll()
     * @see GenreBSTTest#runAll()
     */
    public static void main(String[] args) {

        System.out.println("\nTESTES R3 — RedBlackBST (BST)\n");

        /** Executa os testes de procura e filtragem de utilizadores */
        UserBSTTest.runAll();

        /** Executa os testes de organização cronológica de conteúdos */
        ContentBSTTest.runAll();

        /** Executa os testes de ordenação e estatísticas de artistas */
        ArtistBSTTest.runAll();

        /** Executa os testes de gestão e hierarquia de géneros */
        GenreBSTTest.runAll();

        System.out.println("\nTESTES BST CONCLUIDOS\n");
    }
}