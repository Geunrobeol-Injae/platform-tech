package bob.geunrobeol.platform.tech.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class InternalBeacon {
    private final String beaconId;
    private final ReadWriteLock rwLock;
    private BeaconRecord beaconRecord;

    public InternalBeacon(String beaconId) {
        this.beaconId = beaconId;
        this.rwLock = new ReentrantReadWriteLock();
        this.beaconRecord = new BeaconRecord("P"+beaconId, new HashMap<>(), new ArrayList<>());
    }

    public String getBeaconId() {
        return beaconId;
    }

    public ReadWriteLock getRwLock() {
        return rwLock;
    }

    public void setBeaconRecord(BeaconRecord beaconRecord) {
        this.beaconRecord = beaconRecord;
    }
    public BeaconRecord getBeaconRecord() {
        return beaconRecord;
    }
}