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

/**
 * 삼각측량법을 구현한 Class.
 */
@Service
public class LocationTriangulator implements ILocationEstimator {

    /**
     * Beacon별 데이터(N개)로부터 Beacon의 위치(1개)를 추정한다.
     * @param beaconRecord Beacon별 데이터
     * @return 단일 위치 데이터
     */
    @Override
    public PositionRecord getPosition(BeaconRecord beaconRecord) {
        // Timestamp는 가장 최신의 정보로 설정
        long timestamp = beaconRecord.getScanners().stream()
                .map(ScannerData::getTimestamp)
                .max(Comparator.naturalOrder())
                .orElse(System.currentTimeMillis());
        Map.Entry<Long, Long> xy = estimatePosition(beaconRecord.getScanners());
        Map<String, Integer> payloads = reducePayload(beaconRecord.getScannerPayloads());

        return new PositionRecord(timestamp, beaconRecord.getPseudonym(), payloads, xy.getKey(), xy.getValue());
    }

    /**
     * 스캐너 데이터로부터 위치를 추정한다.
     * @param scanners 스캐너로부터 수집된 데이터
     * @return XY 좌표
     */
    private Map.Entry<Long, Long> estimatePosition(List<ScannerData> scanners) {
        long x = 0, y = 0;

        // TODO estimateLocation
        if (scanners.size() != 0) {
            x = scanners.get(0).getRssi();
        }

        return new AbstractMap.SimpleEntry<>(x, y);
    }
    
    /**
     * Scanner들이 수신한 Payload를 단일 Payload로 정리한다.
     * 현재는 Battery와 Click여부만을 수집하므로 각 Key별 최댓값을 반환한다.
     * @param scannerPayloads Scanner로부터 수신한 Payload
     * @return 단일 Payload
     */
    private Map<String, Integer> reducePayload(Map<Long, Map<String, Integer>> scannerPayloads) {
        Map<String, Integer> payloads = new HashMap<>();

        scannerPayloads.values()
                .stream().flatMap(m -> m.entrySet().stream())
                .forEach(e -> payloads.compute(e.getKey(),
                        (k, v) -> v == null ? e.getValue() : Math.max(v, e.getValue())));

        return payloads;
    }
}