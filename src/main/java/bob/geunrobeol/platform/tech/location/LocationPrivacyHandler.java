package bob.geunrobeol.platform.tech.location;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bob.geunrobeol.platform.tech.vo.BeaconWrapper;
import bob.geunrobeol.platform.tech.vo.proc.BeaconRecord;
import bob.geunrobeol.platform.tech.vo.raw.BeaconData;
import bob.geunrobeol.platform.tech.vo.raw.ScannerRecord;

/**
 * 위치정보 전처리 및 개인정보 보호 Handler. 전처리를 수행함과 동시에 개인정보 보호 기능까지 탑재되어 있다.
 */
@Service
public class LocationPrivacyHandler implements ILocationPreprocessor {
    private static final Logger log = LoggerFactory.getLogger(LocationPrivacyHandler.class);

    /**
     * BeaconId를 Key로 가지는 HashMap
     */
    HashMap<String, BeaconWrapper> beaconMap = new HashMap<>();

    @Autowired
    private LocationPrivacyPublisher publisher;

    @Autowired
    private PseudonymProvider pseudonymProvider;

    /**
     * Scanner로부터 수신된 데이터를 입력한다. 입력 과정에서 데이터 전처리와 동시에
     * 위치정보 보호 관련 기능들(가명처리나 위치정보 처리)을 수행한다.
     * @param scanner Scanner로 부터 수신된 데이터
     */
    @Override
    public void pushScanRecord(ScannerRecord scanner) {
        log.info("push {}", scanner);

        for (BeaconData beacon : scanner.beacons()) {
            // Retrieve beacon (create if not exist)
            BeaconWrapper bw;
            if (!beaconMap.containsKey(beacon.beaconId())) {
                bw = new BeaconWrapper(beacon.beaconId());
                beaconMap.put(beacon.beaconId(), bw);
            } else {
                bw = beaconMap.get(beacon.beaconId());
            }

            // Lock beacon first
            bw.getRwLock().writeLock().lock();
            try {
                // Push Scanned Results
                bw.putScanner(scanner, beacon);

                if (bw.isPseudonymExpired()) {
                    String newPseudonym = pseudonymProvider.refresh(bw.getPseudonym(), scanner.scannerId());
                    bw.setPseudonym(newPseudonym);
                }

            } finally {
                // Unlock beacon
                bw.getRwLock().writeLock().unlock();
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
        for (BeaconWrapper bw : beaconMap.values()) {
            // Read lock first
            bw.getRwLock().readLock().lock();
            try {
                records.add(bw.getBeaconRecord());
            } finally {
                // Relase lock finally
                bw.getRwLock().readLock().unlock();
            }
        }
        return records;
    }
}