package model.content;

import model.users.User;

import java.time.LocalDate;

/**
 * Representa uma avaliação crítica (classificação e comentário) submetida por um utilizador
 * a respeito de uma obra cinematográfica específica.
 * Atua como uma entidade de ligação que armazena a pontuação quantitativa, o feedback
 * qualitativo textual e o registo temporal do evento.
 */
public class Rating {

    /**
     * O utilizador autor da avaliação crítica.
     */
    private User user;

    /**
     * A obra cinematográfica (filme) que está a ser avaliada.
     */
    private Movie movie;

    /**
     * A classificação quantitativa atribuída à obra, medida numa escala linear de 1 a 5 estrelas.
     */
    private int stars; // vai de 1-5

    /**
     * O comentário textual, análise ou crítica descritiva redigida pelo utilizador.
     */
    private String comment;

    /**
     * A data cronológica em que a avaliação foi registada no sistema.
     */
    private LocalDate date;
}