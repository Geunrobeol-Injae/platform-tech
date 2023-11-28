package bob.geunrobeol.platform.tech.vo.raw;

import java.util.Map;

import bob.geunrobeol.platform.tech.vo.proc.ScannerData;

/**
 * {@link ScannerData}의 Beacon class.
 * @see ScannerData
 */
public record BeaconData (String beaconId, Map<String, Integer> payloads, int rssi) {
    @Override
    public String toString() {
        return "BeaconData{" +
                "beaconId='" + beaconId + '\'' +
                ", payloads=" + payloads +
                ", rssi=" + rssi +
                '}';
    }
}