package bob.geunrobeol.platform.tech.vo;

import java.util.List;
import java.util.Map;

public record ScannerRecord(long timestamp, String scannerId, List<BeaconData> beacons) {
    public record BeaconData(String beaconId, Map<String, Integer> payloads, int rssi) {
        @Override
        public String toString() {
            return "BeaconData{" +
                    "beaconId='" + beaconId + '\'' +
                    ", payloads=" + payloads +
                    ", rssi=" + rssi +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ScannerRecord{" +
                "timestamp=" + timestamp +
                ", scannerId='" + scannerId + '\'' +
                ", beacons=" + beacons +
                '}';
    }
}