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

/**
 * 위치정보 전처리 및 개인정보 보호 Handler. 전처리를 수행함과 동시에 개인정보 보호 기능까지 탑재되어 있다.
 */
@Service
public class LocationPrivacyHandler implements ILocationPreprocessor {
    private static final Logger log = LoggerFactory.getLogger(LocationPrivacyHandler.class);

    /**
     * BeaconId를 Key로 가지는 HashMap
     */
    HashMap<String, InternalBeacon> bconMap = new HashMap<>();

    @Autowired
    private LocationPrivacyPublisher publisher;

    /**
     * Scanner로부터 수신된 데이터를 입력한다. 입력 과정에서 데이터 전처리와 동시에
     * 위치정보 보호 관련 기능들(가명처리나 위치정보 처리)을 수행한다.
     * @param scannerRecord Scanner로 부터 수신된 데이터
     */
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
                // Create a record as Scanner Data for Beacon Record
                ScannerData s = new ScannerData(scannerRecord.getTimestamp(), scannerRecord.getScannerId(), b.getRssi());

                // add scanners and payloads
                ib.getScanners().add(0, s);
                ib.putScannerPayloads(scannerRecord.getTimestamp(), b.getPayloads());

                // TODO apply transition of psudonym and location


            } finally {
                // Unlock beacon
                ib.getRwLock().writeLock().unlock();
            }
        }
    }

    /**
     * Beacon별 데이터를 조회한다.
     * @return Beacon별 데이터
     */
    @Override
    public List<BeaconRecord> popBeaconRecord() {
        List<BeaconRecord> records = new ArrayList<>();
        for (InternalBeacon ib : bconMap.values()) {
            // Read lock first
            ib.getRwLock().readLock().lock();
            try {
                records.add(ib.getBeaconRecord());
            } finally {
                // Relase lock finally
                ib.getRwLock().readLock().unlock();
            }
        }
        return records;
    }

    /**
     * 주기적으로 Scanner Data를 삭제한다. 특정 Threshold 수 이상인 경우 Base 수 만큼만 남기고 삭제한다.
     */
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