package bob.geunrobeol.platform.tech.vo.raw;

import java.util.List;

import bob.geunrobeol.platform.tech.vo.proc.BeaconRecord;

/**
 * Scanner로부터 수집된 데이터 class. {@link BeaconData}를 갖고 있으며,
 * {@link bob.geunrobeol.platform.tech.location.ILocationPreprocessor}를 통해
 * {@link BeaconRecord}로 변환된다.
 * @see BeaconData
 */
public record ScannerRecord (long timestamp, String scannerId, List<BeaconData> beacons) {
    @Override
    public String toString() {
        return "ScannerRecord{" +
                "timestamp=" + timestamp +
                ", scannerId='" + scannerId + '\'' +
                ", beacons=" + beacons +
                '}';
    }
}