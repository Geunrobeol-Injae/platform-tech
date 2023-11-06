package bob.geunrobeol.platform.tech.vo;

import java.util.Map;

public record PositionRecord(String pseudonym, Map<String, String> payloads, long x, long y) {
    @Override
    public String toString() {
        return "PositionRecord{" +
                "pseudonym='" + pseudonym + '\'' +
                ", payloads=" + payloads +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
