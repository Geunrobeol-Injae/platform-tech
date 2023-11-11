package bob.geunrobeol.platform.tech.vo;

/**
 * {@link BeaconRecord}의 Scanner class.
 * @see BeaconRecord
 */
public class ScannerData {
    private long timestamp;
    private String scannerId;
    private int rssi;

    public ScannerData(long timestamp, String scannerId, int rssi) {
        this.timestamp = timestamp;
        this.scannerId = scannerId;
        this.rssi = rssi;
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

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    @Override
    public String toString() {
        return "ScannerData{" +
                "timestamp=" + timestamp +
                ", scannerId='" + scannerId + '\'' +
                ", rssi=" + rssi +
                '}';
    }
}