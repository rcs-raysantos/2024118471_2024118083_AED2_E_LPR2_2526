package model.utilities;

/**
 * Classe que representa uma região/país no sistema.
 * Utiliza um código padronizado (ex: PT para Portugal) para facilitar comparações.
 * 
 * @version 1.0
 */
public class Region {
    /**
     * Código padronizado da região (ex: PT, ES, FR).
     * Utilizado para facilitar comparações sem depender de formações diferentes.
     * <p>
     * Exemplo: "portugal", "Portugal", "PORTUGAL" se tornam todas "PT"
     * </p>
     */
    private String code;
    /** Nome completo da região */
    private String name;

    /**
     * Construtor da região com código e nome.
     * 
     * @param code código padronizado da região
     * @param name nome completo da região
     */
    public Region(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * Retorna o código da região.
     * 
     * @return o código
     */
    public String getCode() {
        return code;
    }

    /**
     * Retorna o nome da região.
     * 
     * @return o nome
     */
    public String getName() {
        return name;
    }
}
