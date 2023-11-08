package bob.geunrobeol.platform.tech.vo;

import java.util.Map;

public class BeaconData {
    private String beaconId;
    private Map<String, Integer> payloads;
    private int rssi;

    public BeaconData(String beaconId, Map<String, Integer> payloads, int rssi) {
        this.beaconId = beaconId;
        this.payloads = payloads;
        this.rssi = rssi;
    }

    public String getBeaconId() {
        return beaconId;
    }

    public void setBeaconId(String beaconId) {
        this.beaconId = beaconId;
    }

    public Map<String, Integer> getPayloads() {
        return payloads;
    }

    public void setPayloads(Map<String, Integer> payloads) {
        this.payloads = payloads;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    @Override
    public String toString() {
        return "BeaconData{" +
                "beaconId='" + beaconId + '\'' +
                ", payloads=" + payloads +
                ", rssi=" + rssi +
                '}';
    }
}