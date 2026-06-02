package model.utilities;

import java.time.LocalDate;

/**
 * Wrapper para LocalDate ser usado como chave na RedBlackBST.
 * Fornece métodos de comparação e igualdade para manipulação correta de datas.
 */
public class Date implements Comparable<Date> {
    /** Data armazenada internamente */
    private final LocalDate date;

    /**
     * Construtor da classe Date.
     * 
     * @param date a LocalDate a ser envolvida
     */
    public Date(LocalDate date) {
        this.date = date;
    }

    /**
     * Retorna a data armazenada.
     * 
     * @return a LocalDate
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Compara esta data com outra data.
     * 
     * @param otherDate a outra data a comparar
     * @return valor negativo se esta data vem antes, 0 se igual, positivo se vem depois
     */
    @Override
    public int compareTo(Date otherDate) {
        return this.date.compareTo(otherDate.date);
    }

    /**
     * Verifica se duas datas são iguais.
     * Implementado para que comparações funcionem corretamente em estruturas de dados.
     * 
     * @param obj o objeto a comparar
     * @return true se as datas são iguais, false caso contrário
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Date dateKey = (Date) obj;
        return date.equals(dateKey.date);
    }

    /**
     * Retorna o código de hash para a data.
     * 
     * @return o hash code
     */
    @Override
    public int hashCode() {
        return date.hashCode();
    }
}
