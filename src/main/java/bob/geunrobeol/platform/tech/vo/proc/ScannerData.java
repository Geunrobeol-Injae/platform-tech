package bob.geunrobeol.platform.tech.vo.proc;

import bob.geunrobeol.platform.tech.location.KalmanFilter;

/**
 * {@link BeaconRecord}의 Scanner class.
 * @see BeaconRecord
 */
public class ScannerData {
    private final String scannerId;
    private long timestamp;
    private final KalmanFilter kalmanFilter;

    public ScannerData(String scannerId, long timestamp, int rssi) {
        this.scannerId = scannerId;
        this.timestamp = timestamp;
        this.kalmanFilter = new KalmanFilter(rssi);
    }

    public String getScannerId() {
        return scannerId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getRssi() {
        return kalmanFilter.get();
    }

    public void updateRssi(long timestamp, int rssi) {
        this.timestamp = timestamp;
        this.kalmanFilter.update(rssi);
    }

    public void updateRssiDirectly(int rssi) {
        this.kalmanFilter.set(rssi); 
    }

    @Override
    public String toString() {
        return "ScannerData{" +
                "scannerId='" + scannerId + '\'' +
                ", timestamp=" + timestamp +
                ", rssi=" + kalmanFilter.get() +
                '}';
    }
}