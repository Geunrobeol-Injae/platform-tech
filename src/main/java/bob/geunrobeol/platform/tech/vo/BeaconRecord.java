package bob.geunrobeol.platform.tech.vo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bob.geunrobeol.platform.tech.vo.raw.ScannerRecord;

/**
 * Beacon별 데이터 class. {@link ScannerRecord}를 갖고 있으며,
 * {@link bob.geunrobeol.platform.tech.location.ILocationPreprocessor}를 통해
 * {@link BeaconRecord}로부터 변환되었다.
 * @see ScannerRecord
 */
public class BeaconRecord {
    private String pseudonym;
    private Map<String, ScannerData> scanners;
    private List<Map<String, Integer>> payloadsList;

    public BeaconRecord() {
        this.pseudonym = "";
        this.scanners = new HashMap<>();
        this.payloadsList = new ArrayList<>();
    }

    public String getPseudonym() {
        return pseudonym;
    }

    public void setPseudonym(String pseudonym) {
        this.pseudonym = pseudonym;
    }

    public List<ScannerData> getScanners() {
        return new ArrayList<>(scanners.values());
    }

    public void putScanner(String scannerId, long timestamp, int rssi) {
        ScannerData scannerData;
        if (!scanners.containsKey(scannerId)) {
            // Create new ScannerData
            scannerData = new ScannerData(scannerId, timestamp, rssi);
            scanners.put(scannerId, scannerData);
        } else {
            // Update existing one
            scannerData = scanners.get(scannerId);
            scannerData.updateRssi(timestamp, rssi);
        }
    }

    public long getTimestamp() {
        return scanners.values().stream()
                .map(ScannerData::getTimestamp)
                .max(Comparator.naturalOrder())
                .orElse(-1L);
    }

    /**
     * Scanner들이 수신한 Payload를 단일 Payload로 정리한다.
     * 현재는 Battery와 Click여부만을 수집하므로 각 Key별 최댓값을 반환한다.
     * @return 단일 Payload
     */
    public Map<String, Integer> getPayloads() {
        Map<String, Integer> payloads = new HashMap<>();
        payloadsList.stream()
                .limit(12)
                .flatMap(p -> p.entrySet().stream())
                .forEach(e -> payloads.compute(e.getKey(),
                        (k, v) -> v == null ? e.getValue() : Math.max(v, e.getValue())));
        return payloads;
    }

    public void putPayload(Map<String, Integer> payloads) {
        this.payloadsList.add(0, payloads);
        if (this.payloadsList.size() > 8) {
            this.payloadsList.remove(8);
        }
    }

    @Override
    public String toString() {
        return "BeaconRecord{" +
                "pseudonym='" + pseudonym + '\'' +
                ", scanners=" + scanners +
                ", payloads=" + payloadsList +
                '}';
    }
}
