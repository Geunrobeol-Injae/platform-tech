package bob.geunrobeol.platform.tech.vo;

import java.util.Map;

public record PositionRecord(long timestamp, String pseudonym, Map<String, Integer> payloads, long x, long y) {
    @Override
    public String toString() {
        return "PositionRecord{" +
                "timestamp=" + timestamp +
                ", pseudonym='" + pseudonym + '\'' +
                ", payloads=" + payloads +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
