package bob.geunrobeol.platform.tech.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import bob.geunrobeol.platform.tech.config.LocationPrivacyConfig;
import bob.geunrobeol.platform.tech.vo.proc.BeaconRecord;
import bob.geunrobeol.platform.tech.vo.proc.ScannerData;
import bob.geunrobeol.platform.tech.vo.raw.BeaconData;
import bob.geunrobeol.platform.tech.vo.raw.ScannerRecord;

/**
 * {@link bob.geunrobeol.platform.tech.location.LocationPrivacyHandler} 내부에서 활용되는 class.
 * Concurrency를 위해 {@link ReadWriteLock}을 활용하며
 * 실질적으로 가명 및 위치정보 변경을 수행한다.
 */
public class BeaconWrapper {
    private final String beaconId;
    private final ReadWriteLock rwLock;
    private final Map<String, ScannerData> scannerMap;
    private final List<Map<String, Integer>> payloadsList;

    public BeaconWrapper(String beaconId) {
        this.beaconId = beaconId;
        this.rwLock = new ReentrantReadWriteLock();
        this.scannerMap = new HashMap<>();
        this.payloadsList = new ArrayList<>();
    }

    public ReadWriteLock getRwLock() {
        return rwLock;
    }

    public BeaconRecord getBeaconRecord() {
        
        // Scanners
        List<ScannerData> scanners = new ArrayList<>(scannerMap.values());


        // Dummy the location 
        boolean shouldDummy = scanners.stream()
            .filter(scannerData -> scannerData.getScannerId().equals(LocationPrivacyConfig.DUMMY_SCANNER_ID))
            .anyMatch(scannerData -> scannerData.getRssi() >= LocationPrivacyConfig.DUMMY_RSSI_THRESHOLD);

        if (shouldDummy) {
            scanners.forEach(scannerData -> {
                if (!scannerData.getScannerId().equals(LocationPrivacyConfig.DUMMY_SCANNER_ID)) {
                    scannerData.updateRssiDirectly(-100);
                } else {
                    scannerData.updateRssiDirectly(0);
                }
            });
        }

        // Payloads
        Map<String, Integer> payloads = new HashMap<>();
        payloadsList.stream()
                .limit(12)
                .flatMap(p -> p.entrySet().stream())
                .forEach(e -> payloads.compute(e.getKey(),
                        (k, v) -> v == null ? e.getValue() : Math.max(v, e.getValue())));
    
        return new BeaconRecord(beaconId, null, -1, scanners, payloads);
    }



    public void putScanner(ScannerRecord s, BeaconData b) {
        // Update Payloads
        payloadsList.add(0, b.payloads());

        // Update Scanners
        double rssi;
        if (!scannerMap.containsKey(s.scannerId())) {
            // Create new ScannerData
            scannerMap.put(s.scannerId(), new ScannerData(s.scannerId(), s.timestamp(), b.rssi()));
            rssi = b.rssi();
        } else {
            // Update existing one
            ScannerData scanner = scannerMap.get(s.scannerId());
            scanner.updateRssi(s.timestamp(), b.rssi());
            rssi = scanner.getRssi();
        }

        // PAYLOAD_FLUSH_MAX을 초과하는지 확인
        if (payloadsList.size() > LocationPrivacyConfig.PAYLOAD_FLUSH_MAX) {
            // 삭제할 요소의 시작 index 계산
            int startIndexToRemove = payloadsList.size() - LocationPrivacyConfig.PAYLOAD_FLUSH_REMAIN;

            // PAYLOAD_FLUSH_REMAIN만큼만 유지하도록 삭제
            payloadsList.subList(startIndexToRemove, payloadsList.size()).clear();
        }
    }

    @Override
    public String toString() {
        return "BeaconWrapper{" +
                "beaconId='" + beaconId + '\'' +
                ", scannerMap=" + scannerMap +
                ", payloadsList=" + payloadsList +
                '}';
    }
}