package bob.geunrobeol.platform.tech.vo;

import java.util.Map;

/**
 * Beacon 위치 record. {@link BeaconRecord}가 {@link bob.geunrobeol.platform.tech.location.ILocationEstimator}를 통해
 * 해당 record로 변환된다.
 * @param timestamp
 * @param pseudonym
 * @param payloads
 * @param x
 * @param y
 */
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
