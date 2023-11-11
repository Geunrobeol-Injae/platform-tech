package bob.geunrobeol.platform.tech.vo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class InternalBeacon {
    private final String beaconId;
    private final ReadWriteLock rwLock;
    private final BeaconRecord beaconRecord;

    public InternalBeacon(String beaconId) {
        this.beaconId = beaconId;
        this.rwLock = new ReentrantReadWriteLock();
        this.beaconRecord = new BeaconRecord();
    }

    public String getBeaconId() {
        return beaconId;
    }

    public ReadWriteLock getRwLock() {
        return rwLock;
    }

    public BeaconRecord getBeaconRecord() {
        return beaconRecord;
    }

    public String getPseudonym() {
        return beaconRecord.getPseudonym();
    }

    public void setPseudonym(String pseudonym) {
        this.beaconRecord.setPseudonym(pseudonym);
    }

    public Map<Long, Map<String, Integer>> getScannerPayloads() {
        return beaconRecord.getScannerPayloads();
    }

    public void putScannerPayloads(long timestamp, Map<String, Integer> payloads) {
        beaconRecord.putScannerPayloads(timestamp, payloads);
    }

    public List<ScannerData> getScanners() {
        return beaconRecord.getScanners();
    }

    public void setScanners(List<ScannerData> scanners) {
        beaconRecord.setScanners(scanners);
    }

    @Override
    public String toString() {
        return "InternalBeacon{" +
                "beaconId='" + beaconId + '\'' +
                ", rwLock=" + rwLock +
                ", beaconRecord=" + beaconRecord +
                '}';
    }
}