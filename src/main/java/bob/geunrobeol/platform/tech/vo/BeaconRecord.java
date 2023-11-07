package bob.geunrobeol.platform.tech.vo;

import java.util.List;
import java.util.Map;

public record BeaconRecord(String pseudonym, Map<String, Map<String, Integer>> scannerPayloads, List<ScannerData> scanners) {
    public record ScannerData(long timestamp, String scannerId, int rssi) {
        @Override
        public String toString() {
            return "ScannerData{" +
                    "timestamp=" + timestamp +
                    ", scannerId='" + scannerId + '\'' +
                    ", rssi=" + rssi +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "BeaconRecord{" +
                "pseudonym='" + pseudonym + '\'' +
                ", scannerPayloads=" + scannerPayloads +
                ", scanners=" + scanners +
                '}';
    }
}
