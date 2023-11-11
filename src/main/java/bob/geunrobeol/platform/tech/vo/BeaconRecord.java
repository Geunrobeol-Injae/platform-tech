package bob.geunrobeol.platform.tech.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeaconRecord {
    private String pseudonym;
    private Map<Long, Map<String, Integer>> scannerPayloads;
    private List<ScannerData> scanners;

    public BeaconRecord() {
        this("", new HashMap<>(), new ArrayList<>());
    }

    public BeaconRecord(String pseudonym, Map<Long, Map<String, Integer>> scannerPayloads, List<ScannerData> scanners) {
        this.pseudonym = pseudonym;
        this.scannerPayloads = scannerPayloads;
        this.scanners = scanners;
    }

    public String getPseudonym() {
        return pseudonym;
    }

    public void setPseudonym(String pseudonym) {
        this.pseudonym = pseudonym;
    }

    public Map<Long, Map<String, Integer>> getScannerPayloads() {
        return scannerPayloads;
    }

    public void putScannerPayloads(long timestamp, Map<String, Integer> payloads) {
        scannerPayloads.put(timestamp, payloads);
    }

    public List<ScannerData> getScanners() {
        return scanners;
    }

    public void setScanners(List<ScannerData> scanners) {
        this.scanners = scanners;
    }

    @Override
    public String toString() {
        return "BeaconRecord{" +
                "pseudonym='" + pseudonym + '\'' +
                ", scannerPayloads=" + scannerPayloads +
                ", scanners=" + scanners +
                '}';
    }
}

