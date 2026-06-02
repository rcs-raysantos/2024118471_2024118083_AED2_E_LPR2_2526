package service.serialization;

import java.io.Serializable;

/**
 * Representa um registo simplificado de dados de um conteúdo multimédia para efeitos
 * de persistência e transferência de dados (DTO - Data Transfer Object).
 * Esta classe encapsula metadados básicos como título, ano de lançamento, duração
 * e tipo de conteúdo, suportando o protocolo de serialização binária para arquivo de estados.
 */
public class ContentRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String title;
    private final int year;
    private final int duration;
    private final String type;

    /**
     * Constrói e inicializa uma nova instância imutável de um registo de conteúdo.
     *
     * @param title    O título ou nome da produção multimédia.
     * @param year     O ano de lançamento ou publicação da obra.
     * @param duration A duração temporal do conteúdo (tipicamente expressa em minutos).
     * @param type     A tipologia do conteúdo (por exemplo, "Filme", "Série", etc.).
     */
    public ContentRecord(String title, int year, int duration, String type) {
        this.title = title;
        this.year = year;
        this.duration = duration;
        this.type = type;
    }

    /**
     * Recupera o título atribuído ao registo do conteúdo.
     *
     * @return Uma string contendo o título da obra.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Recupera o ano oficial de lançamento do conteúdo.
     *
     * @return O ano de publicação como um valor inteiro.
     */
    public int getYear() {
        return year;
    }

    /**
     * Recupera o tempo total de duração registado para a produção multimédia.
     *
     * @return A duração da obra como um valor inteiro.
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Recupera a classificação tipológica associada à obra.
     *
     * @return Uma string representativa do tipo de conteúdo.
     */
    public String getType() {
        return type;
    }
}