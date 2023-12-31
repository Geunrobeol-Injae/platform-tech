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
    private String beaconId;
    private String sigText; 
    private int authId; 
    private List<ScannerData> scanners;
    private Map<String, Integer> payloads;

    public BeaconRecord(String beaconId, String sigText, int authId, List<ScannerData> scanners, Map<String, Integer> payloads) {
        this.beaconId = beaconId;
        this.sigText = sigText;
        this.authId = authId;
        this.scanners = scanners;
        this.payloads = payloads;
    }

    public String getBeaconId() {
        return beaconId;
    }

    public long getTimestamp() {
        return scanners.stream()
                .map(ScannerData::getTimestamp)
                .max(Comparator.naturalOrder())
                .orElse(-1L);
    }

    public String getSigText() {
        return sigText;
    }

    public void setSigText(String sigText) {
        this.sigText = sigText;
    }

    public int getAuthId() {
        return authId;
    }

    public void setAuthId(int authId) {
        this.authId = authId;
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
                "beaconId='" + beaconId + '\'' +
                ", sigText='" + sigText + '\'' +
                ", authId=" + authId +
                ", scanners=" + scanners +
                ", payloads=" + payloads +
                '}';
    }
}
