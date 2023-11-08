package bob.geunrobeol.platform.tech.location;

import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bob.geunrobeol.platform.tech.vo.BeaconRecord;
import bob.geunrobeol.platform.tech.vo.PositionRecord;
import bob.geunrobeol.platform.tech.vo.ScannerData;

@Service
public class LocationTriangulator implements ILocationEstimator {

    @Override
    public PositionRecord getPosition(BeaconRecord beaconRecord) {
        long timestamp = beaconRecord.getScanners().stream()
                .map(ScannerData::getTimestamp)
                .max(Comparator.naturalOrder())
                .orElse(System.currentTimeMillis());
        Map<String, Integer> payloads = reducePayload(beaconRecord.getScannerPayloads());
        Map.Entry<Long, Long> xy = estimateLocation(beaconRecord.getScanners());

        return new PositionRecord(timestamp, beaconRecord.getPseudonym(), payloads, xy.getKey(), xy.getValue());
    }

    private Map<String, Integer> reducePayload(Map<Long, Map<String, Integer>> scannerPayloads) {
        Map<String, Integer> payloads = new HashMap<>();

        scannerPayloads.values()
                .stream().flatMap(m -> m.entrySet().stream())
                .forEach(e -> payloads.compute(e.getKey(),
                        (k, v) -> v == null ? e.getValue() : Math.max(v, e.getValue())));

        return payloads;
    }

    private Map.Entry<Long, Long> estimateLocation(List<ScannerData> scanners) {
        long x = 0, y = 0;

        // TODO estimateLocation
        if (scanners.size() != 0) {
            x = scanners.get(0).getRssi();
        }

        return new AbstractMap.SimpleEntry<>(x, y);
    }
}