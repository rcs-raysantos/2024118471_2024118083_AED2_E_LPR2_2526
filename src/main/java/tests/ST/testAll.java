package tests.ST;

/**
 * @brief Classe principal de orquestração para os testes das Symbol Tables (R2).
 * * Esta classe atua como um "Test Suite", centralizando a execução de todos os
 * testes unitários relacionados com as Tabelas de Símbolos de Utilizadores,
 * Conteúdos e Artistas. Garante que todos os requisitos da fase R2 sejam validados de uma só vez.
 */
public class testAll {

    /**
     * @brief Ponto de entrada principal para a execução dos testes ST.
     * * Este método imprime o cabeçalho de teste, executa sequencialmente as
     * baterias de testes de cada estrutura (UserST, ContentST, ArtistST) e
     * confirma a conclusão de todos os testes no terminal.
     * * @param args Argumentos da linha de comando (não utilizados).
     */
    public static void main(String[] args) {

        System.out.println("\nTESTES R2 — Symbol Tables (ST)\n");

        /** @see UserSTTest#runAll() */
        UserSTTest.runAll();

        /** @see ContentSTTest#runAll() */
        ContentSTTest.runAll();

        /** @see ArtistSTTest#runAll() */
        ArtistSTTest.runAll();

        System.out.println("\nTESTE ST CONCLUIDOS\n");
    }
}