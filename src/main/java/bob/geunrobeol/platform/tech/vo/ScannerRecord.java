package bob.geunrobeol.platform.tech.vo;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

public record ScannerRecord(long timestamp, String scannerId, List<BeaconData> beacons) {
    public static ScannerRecord fromByteBuffer(ByteBuffer byteBuffer) {
        // TODO fromByteBuffer
        return null;
    }

    public record BeaconData(String beaconId, Map<String, String> payloads, int rssi) {
        @Override
        public String toString() {
            return "ScannerRecord{" +
                    "beaconId='" + beaconId + '\'' +
                    ", payloads=" + payloads +
                    ", rssi=" + rssi +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ScanData{" +
                "timestamp=" + timestamp +
                ", scannerId='" + scannerId + '\'' +
                ", beacons=" + beacons +
                '}';
    }

}