package bob.geunrobeol.platform.tech.vo;

import java.awt.geom.Point2D;
import java.util.Map;

/**
 * Beacon 위치 record. {@link BeaconRecord}가 {@link bob.geunrobeol.platform.tech.location.ILocationEstimator}를 통해
 * 해당 record로 변환된다.
 * @param timestamp
 * @param pseudonym
 * @param payloads
 * @param pos
 */
public record BeaconPosition(String pseudonym, long timestamp, Map<String, Integer> payloads, Point2D.Double pos) {
    @Override
    public String toString() {
        return "PositionRecord{" +
                "timestamp=" + timestamp +
                ", pseudonym='" + pseudonym + '\'' +
                ", payloads=" + payloads +
                ", x=" + pos.x +
                ", y=" + pos.y +
                '}';
    }
}