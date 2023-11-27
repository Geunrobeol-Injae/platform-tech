package bob.geunrobeol.platform.tech.vo;

import bob.geunrobeol.platform.tech.location.KalmanFilter;

/**
 * {@link BeaconRecord}의 Scanner class.
 * @see BeaconRecord
 */
public class ScannerData {
    private final String scannerId;
    private long timestamp;
    private KalmanFilter kalmanFilter;

    public ScannerData(String scannerId, long timestamp, int rssi) {
        this.scannerId = scannerId;
        this.timestamp = timestamp;
        this.kalmanFilter = new KalmanFilter(rssi);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getScannerId() {
        return scannerId;
    }

    public double getRssi() {
        return kalmanFilter.getEstimatedValue();
    }

    public void updateRssi(long timestamp, int rssi) {
        this.timestamp = timestamp;
        this.kalmanFilter.update(rssi);
    }

    @Override
    public String toString() {
        return "ScannerData{" +
                "timestamp=" + timestamp +
                ", scannerId='" + scannerId + '\'' +
                ", rssi=" + kalmanFilter.getEstimatedValue() +
                '}';
    }
}