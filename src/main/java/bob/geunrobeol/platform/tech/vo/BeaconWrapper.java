package bob.geunrobeol.platform.tech.vo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import bob.geunrobeol.platform.tech.vo.raw.BeaconData;
import bob.geunrobeol.platform.tech.vo.raw.ScannerRecord;

/**
 * {@link bob.geunrobeol.platform.tech.location.LocationPrivacyHandler} 내부에서 활용되는 class.
 * Concurrency를 위해 {@link ReadWriteLock}을 활용하며
 * {@link BeaconRecord}에 대한 Wrapper class이다.
 */
public class BeaconWrapper {
    private final String beaconId;
    private final ReadWriteLock rwLock;
    private final BeaconRecord beaconRecord;

    public BeaconWrapper(String beaconId) {
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

    public void putScanner(ScannerRecord scanner, BeaconData beacon) {
        beaconRecord.putScanner(scanner.scannerId(), scanner.timestamp(), beacon.rssi());
        beaconRecord.putPayload(beacon.payloads());
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