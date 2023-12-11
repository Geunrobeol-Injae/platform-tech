package bob.geunrobeol.platform.tech.vo;

import java.awt.geom.Point2D;
import java.util.Map;

/**
 * Beacon 위치 record. {@link bob.geunrobeol.platform.tech.vo.proc.BeaconRecord}가
 * {@link bob.geunrobeol.platform.tech.location.ILocationEstimator}를 통해
 * 해당 record로 변환된다.
 * @param beaconId
 * @param timestamp
 * @param pos
 * @param payloads
 */
public record BeaconPosition(String beaconId, long timestamp, Point2D.Double pos, Map<String, Integer> payloads) {
    @Override
    public String toString() {
        return "BeaconPosition{" +
                "beaconId='" + beaconId + '\'' +
                ", timestamp=" + timestamp +
                ", pos.x=" + pos.x +
                ", pos.y=" + pos.y +
                ", payloads=" + payloads +
                '}';
    }
}