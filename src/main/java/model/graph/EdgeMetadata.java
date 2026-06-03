package model.graph;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Representa os metadados e atributos de peso associados a uma aresta (ligação) no grafo.
 * Esta classe armazena informações estruturais como o tipo de relacionamento, peso numérico,
 * carimbo de data/hora (timestamp) de criação e um mapa flexível para dados extra adicionais,
 * suportando a serialização de objetos para persistência em disco.
 */
public class EdgeMetadata implements Serializable {
    private static final long serialVersionUID = 1L;

    private final RelationType type;
    private final double weight;
    private final LocalDateTime timestamp;
    private final Map<String, Object> extraData;

    /**
     * Construtor de conveniência que inicializa os metadados da aresta com o tipo e peso especificados.
     * Define automaticamente o carimbo de data/hora atual (now) e instancia um mapa de dados extra vazio.
     *
     * @param type   O tipo de relacionamento (Enum {@link RelationType}) desta ligação.
     * @param weight O peso numérico ou relevância atribuída à aresta.
     * @throws IllegalArgumentException Se o tipo for nulo ou se o peso for negativo.
     */
    public EdgeMetadata(RelationType type, double weight) {
        this(type, weight, LocalDateTime.now(), new HashMap<>());
    }

    /**
     * Construtor completo que permite a especificação detalhada de todos os atributos dos metadados da aresta.
     * Valida os parâmetros de entrada para garantir a consistência do modelo de dados.
     *
     * @param type      O tipo de relacionamento (Enum {@link RelationType}) desta ligação.
     * @param weight    O peso numérico ou relevância atribuída à aresta.
     * @param timestamp O carimbo de data/hora associado à criação ou ocorrência do vínculo.
     * Se for nulo, será atribuído o valor de {@link LocalDateTime#now()}.
     * @param extraData Um mapa contendo propriedades customizadas adicionais para a aresta.
     * Se for nulo, será inicializado um novo mapa vazio.
     * @throws IllegalArgumentException Se o tipo for nulo ou se o peso for negativo.
     */
    public EdgeMetadata(RelationType type, double weight, LocalDateTime timestamp, Map<String, Object> extraData) {
        if (type == null) {
            throw new IllegalArgumentException("Relation type cannot be null");
        }
        if (weight < 0) {
            throw new IllegalArgumentException("Weight cannot be negative");
        }
        this.type = type;
        this.weight = weight;
        this.timestamp = timestamp == null ? LocalDateTime.now() : timestamp;
        this.extraData = extraData == null ? new HashMap<>() : new HashMap<>(extraData);
    }

    /**
     * Recupera o tipo de relacionamento que caracteriza a aresta.
     *
     * @return O {@link RelationType} correspondente.
     */
    public RelationType getType() {
        return type;
    }

    /**
     * Recupera o peso numérico ou valor de custo/afinidade configurado na aresta.
     *
     * @return O peso da aresta como um valor {@code double}.
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Recupera o carimbo de data e hora em que a ligação foi registada.
     *
     * @return O {@link LocalDateTime} associado à aresta.
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Retorna uma perspetiva imutável e protegida do mapa de dados adicionais.
     * Qualquer tentativa de modificação direta nesta coleção resultará numa exceção
     * {@link UnsupportedOperationException}.
     *
     * @return Um {@link Map} não modificável contendo as chaves e valores extras armazenados.
     */
    public Map<String, Object> getExtraData() {
        return Collections.unmodifiableMap(extraData);
    }

    /**
     * Procura e recupera um valor específico guardado no mapa de propriedades adicionais
     * a partir da sua chave identificadora.
     *
     * @param key O nome do atributo ou chave do metadado extra a ser consultado.
     * @return O {@link Object} associado à chave fornecida, ou {@code null} caso não exista.
     */
    public Object getExtraData(String key) {
        return extraData.get(key);
    }
}