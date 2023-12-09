package bob.geunrobeol.platform.tech.vo.proc;

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
    private List<ScannerData> scanners;
    private Map<String, Integer> payloads;

    public BeaconRecord(String pseudonym, List<ScannerData> scanners, Map<String, Integer> payloads) {
        this.pseudonym = pseudonym;
        this.scanners = scanners;
        this.payloads = payloads;
    }

    public String getPseudonym() {
        return pseudonym;
    }

    public long getTimestamp() {
        return scanners.stream()
                .map(ScannerData::getTimestamp)
                .max(Comparator.naturalOrder())
                .orElse(-1L);
    }

    public List<ScannerData> getScanners() {
        return new ArrayList<>(scanners);
    }

    public Map<String, Integer> getPayloads() {
        return new HashMap<>(payloads);
    }

    @Override
    public String toString() {
        return "BeaconRecord{" +
                "pseudonym='" + pseudonym + '\'' +
                ", scanners=" + scanners +
                ", payloads=" + payloads +
                '}';
    }
}
