package bob.geunrobeol.platform.tech.location;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import bob.geunrobeol.platform.tech.vo.BeaconData;
import bob.geunrobeol.platform.tech.vo.BeaconRecord;
import bob.geunrobeol.platform.tech.vo.InternalBeacon;
import bob.geunrobeol.platform.tech.vo.ScannerData;
import bob.geunrobeol.platform.tech.vo.ScannerRecord;

@Service
public class LocationPrivacyHandler implements ILocationPreprocessor {
    private static final Logger log = LoggerFactory.getLogger(LocationPrivacyHandler.class);
    
    HashMap<String, InternalBeacon> beaconMap = new HashMap<>();

    private final long FLUSH_DELAY = 10000L;

    private final int SCANNER_THRESHOLD = 25;

    private final int SCANNER_BASE = 10;

    @Autowired
    private LocationPrivacyPublisher publisher;

    @Override
    public void pushScanRecord(ScannerRecord scannerRecord) {
        log.info("push {}", scannerRecord);
        publisher.publishScanner(scannerRecord);

        for (BeaconData b : scannerRecord.getBeacons()) {
            beaconMap.putIfAbsent(b.getBeaconId(), new InternalBeacon(b.getBeaconId()));
            InternalBeacon ib = beaconMap.get(b.getBeaconId());

            ib.getRwLock().writeLock().lock();
            try {
                ScannerData s = new ScannerData(scannerRecord.getTimestamp(), scannerRecord.getScannerId(), b.getRssi());
                // add scanners and payloads
                ib.getBeaconRecord().getScanners().add(0, s);
                ib.getBeaconRecord().getScannerPayloads().put(scannerRecord.getTimestamp(), b.getPayloads());

                // TODO apply change psudonym and location
            } finally {
                ib.getRwLock().writeLock().unlock();
            }
        }
    }

    @Override
    public List<BeaconRecord> popBeaconRecord() {
        // TODO increase throughput
        beaconMap.values().forEach(v -> v.getRwLock().readLock().lock());
        try {
            return beaconMap.values().stream()
                    .map(InternalBeacon::getBeaconRecord)
                    .collect(Collectors.toList());
        } finally {
            beaconMap.values().forEach(v -> v.getRwLock().readLock().unlock());
        }
    }

    @Scheduled(fixedDelay = FLUSH_DELAY)
    private void flushRecords() {
        int cnt = 0;
        for(Map.Entry<String, InternalBeacon> kv : beaconMap.entrySet()) {
            InternalBeacon ib = kv.getValue();
            ib.getRwLock().writeLock().lock();
            try {
                if (ib.getBeaconRecord().getScanners().size() > SCANNER_THRESHOLD) {
                    cnt++;
                    BeaconRecord br = new BeaconRecord(
                            ib.getBeaconRecord().getPseudonym(),
                            ib.getBeaconRecord().getScannerPayloads(),
                            ib.getBeaconRecord().getScanners().subList(0, SCANNER_BASE));
                    ib.setBeaconRecord(br);
                }
            } finally {
                ib.getRwLock().writeLock().unlock();

            }
        }
        publisher.publishPsudonym("Flushed %d beacons' records".formatted(cnt));
    }
}