package bob.geunrobeol.platform.tech.vo;

import java.util.List;

public class ScannerRecord {
    private long timestamp;
    private String scannerId;
    private List<BeaconData> beacons;

    public ScannerRecord(long timestamp, String scannerId, List<BeaconData> beacons) {
        this.timestamp = timestamp;
        this.scannerId = scannerId;
        this.beacons = beacons;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getScannerId() {
        return scannerId;
    }

    public void setScannerId(String scannerId) {
        this.scannerId = scannerId;
    }

    public List<BeaconData> getBeacons() {
        return beacons;
    }

    public void setBeacons(List<BeaconData> beacons) {
        this.beacons = beacons;
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