package bob.geunrobeol.platform.tech.location;

import org.springframework.stereotype.Service;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import bob.geunrobeol.platform.tech.vo.BeaconRecord;
import bob.geunrobeol.platform.tech.vo.PositionRecord;
import bob.geunrobeol.platform.tech.vo.ScannerData;
import java.awt.geom.Point2D;
import java.util.HashMap;


/**
 * 삼각측량법을 구현한 Class.
 */
@Service
public class LocationTriangulator implements ILocationEstimator {

    private Map<String, Point2D.Double> scannerPositions = new HashMap<>();

    public LocationTriangulator() {
        initializeScannerPositions();
    }

    /**
     * 초기 스캐너 위치를 설정한다. 
     * 이 메소드는 각 스캐너의 고유한 식별자와 해당 스캐너의 좌표를 매핑하여 저장한다.
     * 이 매핑은 삼각측량을 수행할 때 사용된다.
     */
    private void initializeScannerPositions() {
        scannerPositions.put("A", new Point2D.Double(0, 0));
        scannerPositions.put("B", new Point2D.Double(400, 0));
        scannerPositions.put("C", new Point2D.Double(0, 400));
        scannerPositions.put("D", new Point2D.Double(400, 400));
    }

    
    /**
     * Beacon별 데이터(N개)로부터 Beacon의 위치(1개)를 추정한다.
     * @param beaconRecord Beacon별 데이터
     * @return 단일 위치 데이터
     */
    @Override
    public PositionRecord getPosition(BeaconRecord beaconRecord) {
        long timestamp = beaconRecord.getScanners().stream()
                .map(ScannerData::getTimestamp)
                .max(Comparator.naturalOrder())
                .orElse(System.currentTimeMillis());
        Map.Entry<Long, Long> xy = estimatePosition(beaconRecord.getScanners());
        Map<String, Integer> payloads = reducePayload(beaconRecord.getScannerPayloads());

        return new PositionRecord(timestamp, beaconRecord.getPseudonym(), payloads, xy.getKey(), xy.getValue());
    }
    

    /**
     * BeaconRecord에서 제공된 스캐너 데이터를 사용하여 사용자의 위치를 추정한다.
     * 이 메소드는 RSSI 값을 기반으로 가장 강한 신호를 보낸 상위 3개의 스캐너를 선택하고,
     * 각 스캐너로부터의 거리를 계산하여 삼각측량을 수행한다.
     * @param scanners BeaconRecord에 포함된 스캐너 데이터 목록
     * @return 추정된 사용자의 위치 좌표 (x, y). Map.Entry 객체로, key는 x좌표, value는 y좌표를 나타낸다.
     */
    private Map.Entry<Long, Long> estimatePosition(List<ScannerData> scanners) {
        List<ScannerData> closestScanners = scanners.stream()
            .sorted(Comparator.comparingInt(ScannerData::getRssi).reversed())
            .limit(3)
            .collect(Collectors.toList());

        Point2D.Double[] points = new Point2D.Double[3];
        double[] distances = new double[3];
        for (int i = 0; i < 3; i++) {
            ScannerData scanner = closestScanners.get(i);
            points[i] = scannerPositions.get(scanner.getScannerId());
            distances[i] = calculateDistanceFromRssi(scanner.getRssi());

            System.out.println("Scanner ID: " + scanner.getScannerId() + ", Distance: " + distances[i]);
        }

        Point2D.Double position = triangulate(points[0], distances[0], points[1], distances[1], points[2], distances[2]);
        System.out.println("Estimated Position: x=" + position.x + ", y=" + position.y);

        return new AbstractMap.SimpleEntry<>((long) position.x, (long) position.y);

    }


    /**
     * RSSI 신호 강도를 거리로 변환한다.
     * @param rssi RSSI 신호 강도 값
     * @return 계산된 거리
     */
    private double calculateDistanceFromRssi(double rssi) {
        double referenceRssi = -50.0;
        double pathLossParameter = 3.0;
        double referenceDistance = 100.0;
    
        return Math.pow(10.0, (referenceRssi - rssi) / (10.0 * pathLossParameter)) * referenceDistance;
    }


    /**
     * 세 개의 스캐너 위치와 각 스캐너로부터의 거리를 기반으로 삼각측량을 수행한다.
     * 이 메소드는 세 점(스캐너 위치)과 각 점에서의 거리를 바탕으로 사용자의 위치를 계산한다.
     * @param p1 첫 번째 스캐너의 위치 (Point2D.Double 객체)
     * @param d1 첫 번째 스캐너로부터의 거리
     * @param p2 두 번째 스캐너의 위치 (Point2D.Double 객체)
     * @param d2 두 번째 스캐너로부터의 거리
     * @param p3 세 번째 스캐너의 위치 (Point2D.Double 객체)
     * @param d3 세 번째 스캐너로부터의 거리
     * @return 삼각측량을 통해 추정된 사용자의 위치 (Point2D.Double 객체). 
     *         만약 위치를 계산할 수 없는 경우 null을 반환할 수 있음.
     */
    private Point2D.Double triangulate(Point2D.Double p1, double d1, Point2D.Double p2, double d2, Point2D.Double p3, double d3) {
        // 선형 방정식 계산을 위한 변수
        double A = 2 * (p2.x - p1.x);
        double B = 2 * (p2.y - p1.y);
        double C = Math.pow(d1, 2) - Math.pow(d2, 2) - Math.pow(p1.x, 2) + Math.pow(p2.x, 2) - Math.pow(p1.y, 2) + Math.pow(p2.y, 2);
        double D = 2 * (p3.x - p2.x);
        double E = 2 * (p3.y - p2.y);
        double F = Math.pow(d2, 2) - Math.pow(d3, 2) - Math.pow(p2.x, 2) + Math.pow(p3.x, 2) - Math.pow(p2.y, 2) + Math.pow(p3.y, 2);
    
        // 사용자의 위치 (x, y) 계산
        double x = ((F * B) - (E * C)) / ((B * D) - (E * A));
        double y = ((F * A) - (D * C)) / ((A * E) - (D * B));
    
        return new Point2D.Double(x, y);
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