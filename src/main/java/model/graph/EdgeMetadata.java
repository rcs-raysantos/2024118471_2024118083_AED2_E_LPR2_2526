package model.graph;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EdgeMetadata implements Serializable {
    private static final long serialVersionUID = 1L;

    private final RelationType type;
    private final double weight;
    private final LocalDateTime timestamp;
    private final Map<String, Object> extraData;

    public EdgeMetadata(RelationType type, double weight) {
        this(type, weight, LocalDateTime.now(), new HashMap<>());
    }

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

    public RelationType getType() {
        return type;
    }

    public double getWeight() {
        return weight;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Map<String, Object> getExtraData() {
        return Collections.unmodifiableMap(extraData);
    }

    public Object getExtraData(String key) {
        return extraData.get(key);
    }
}
