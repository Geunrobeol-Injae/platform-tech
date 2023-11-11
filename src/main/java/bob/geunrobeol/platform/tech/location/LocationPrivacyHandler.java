package bob.geunrobeol.platform.tech.location;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bob.geunrobeol.platform.tech.config.LocationPrivacyConfig;
import bob.geunrobeol.platform.tech.vo.BeaconData;
import bob.geunrobeol.platform.tech.vo.BeaconRecord;
import bob.geunrobeol.platform.tech.vo.InternalBeacon;
import bob.geunrobeol.platform.tech.vo.ScannerData;
import bob.geunrobeol.platform.tech.vo.ScannerRecord;

@Service
public class LocationPrivacyHandler implements ILocationPreprocessor {
    private static final Logger log = LoggerFactory.getLogger(LocationPrivacyHandler.class);

    HashMap<String, InternalBeacon> bconMap = new HashMap<>();

    @Autowired
    private LocationPrivacyPublisher publisher;

    @Override
    public void pushScanRecord(ScannerRecord scannerRecord) {
        log.info("push {}", scannerRecord);

        for (BeaconData b : scannerRecord.getBeacons()) {
            // Retrieve beacon (create if not exist)
            InternalBeacon ib;
            if (!bconMap.containsKey(b.getBeaconId())) {
                ib = new InternalBeacon(b.getBeaconId());
                bconMap.put(b.getBeaconId(), ib);
            } else {
                ib = bconMap.get(b.getBeaconId());
            }

            // Lock beacon first
            ib.getRwLock().writeLock().lock();
            try {
                ScannerData s = new ScannerData(scannerRecord.getTimestamp(), scannerRecord.getScannerId(), b.getRssi());

                // add scanners and payloads
                ib.getScanners().add(0, s);
                ib.putScannerPayloads(scannerRecord.getTimestamp(), b.getPayloads());

                // TODO apply change psudonym and location
            } finally {
                // Unlock beacon
                ib.getRwLock().writeLock().unlock();
            }
        }
    }

    @Override
    public List<BeaconRecord> popBeaconRecord() {
        List<BeaconRecord> records = new ArrayList<>();
        for (InternalBeacon ib : bconMap.values()) {
            ib.getRwLock().readLock().lock();
            try {
                   records.add(ib.getBeaconRecord());
            } finally {
                ib.getRwLock().readLock().unlock();
            }
        }
        return records;
    }

    @Scheduled(fixedDelay = LocationPrivacyConfig.FLUSH_DELAY)
    private void flushRecords() {
        int cnt = 0;
        for(InternalBeacon ib : bconMap.values()) {
            ib.getRwLock().writeLock().lock();
            try {
                if (ib.getScanners().size() > LocationPrivacyConfig.SCANNER_THRESHOLD) {
                    cnt++;
                    ib.setScanners(ib.getScanners().subList(0, LocationPrivacyConfig.SCANNER_BASE));
                }
            } finally {
                ib.getRwLock().writeLock().unlock();
            }
        }
        publisher.publishPsudonym("Flushed %d beacon(s)".formatted(cnt));
    }
}