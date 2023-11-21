package bob.geunrobeol.platform.tech.location;

import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import bob.geunrobeol.platform.tech.vo.BeaconRecord;
import bob.geunrobeol.platform.tech.vo.PositionRecord;
import bob.geunrobeol.platform.tech.vo.ScannerData;

import java.awt.geom.Point2D;



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

    
    public class KalmanFilter {
        private boolean initialized;
        private double processNoise;
        private double measurementNoise;
        private double predictedRSSI;
        private double errorCovariance;
    
        public KalmanFilter(double processNoise, double measurementNoise) {
            this.initialized = false;
            this.processNoise = processNoise;
            this.measurementNoise = measurementNoise;
            this.predictedRSSI = 0;
            this.errorCovariance = 0;
        }

        /**
         * 칼만필터(KalmanFilter)를 통한 RSSI 전처리 
         * @param rssi 필터링할 원본 RSSI 값
         * @return 필터링된 RSSI 값
         */
        public double filter(double rssi) {
            if (!initialized) {
                initialized = true;
                predictedRSSI = rssi;
                errorCovariance = 1;
            } else {
                double priorErrorCovariance = errorCovariance + processNoise;
                double kalmanGain = priorErrorCovariance / (priorErrorCovariance + measurementNoise);
                predictedRSSI = predictedRSSI + (kalmanGain * (rssi - predictedRSSI));
                errorCovariance = (1 - kalmanGain) * priorErrorCovariance;
            }
    
            return predictedRSSI;
        }
    }

    /**
     * 스캐너 데이터로부터 위치를 추정한다.
     * @param scanners 스캐너로부터 수집된 데이터 리스트
     * @return X와 Y 좌표를 포함하는 Map.Entry 객체, 여기서 Key는 X좌표, Value는 Y좌표
     */
    private Map.Entry<Long, Long> estimatePosition(List<ScannerData> scanners) {

        KalmanFilter kalmanFilter = new KalmanFilter(0.005, 20); //new말고..바꿀 예정

        Map<String, Point2D.Double> scannerPositions = new HashMap<>();
        scannerPositions.put("A", new Point2D.Double(0, 0));
        scannerPositions.put("B", new Point2D.Double(400, 0));  
        scannerPositions.put("C", new Point2D.Double(0, 450));
        scannerPositions.put("D", new Point2D.Double(400, 450));

        List<ScannerData> closestScanners = scanners.stream()
        .sorted(Comparator.comparingInt(ScannerData::getRssi).reversed())
        .limit(3)
        .collect(Collectors.toList());
 
        Point2D.Double[] points = new Point2D.Double[3];
        double[] distances = new double[3];
        for (int i = 0; i < 3; i++) {
            ScannerData scanner = closestScanners.get(i);
            points[i] = scannerPositions.get(scanner.getScannerId());
            double filteredRSSI = kalmanFilter.filter(scanner.getRssi()); // 칼만 필터를 사용하여 RSSI 값을 필터링
            distances[i] = calculateDistanceFromRssi(filteredRSSI);
        }

        Point2D.Double position = triangulate(points[0], distances[0], points[1], distances[1], points[2], distances[2]);

        return new AbstractMap.SimpleEntry<>((long) position.x, (long) position.y);
    }

    /**
     * RSSI 신호 강도를 거리로 변환한다.
     * @param rssi RSSI 신호 강도 값
     * @return 계산된 거리
     */
    private double calculateDistanceFromRssi(double rssi) {
        double referenceRssi = -50.0; // 참조 RSSI 값
        double pathLossParameter = 3.0; // 경로 손실 매개변수
        double referenceDistance = 1.0; // 참조 거리
    
        return Math.pow(10.0, (referenceRssi - rssi) / (10.0 * pathLossParameter)) * referenceDistance;
    }

    /**
     * 세 개의 스캐너 위치와 각 스캐너로부터의 거리를 기반으로 삼각측량을 수행한다.
     * @param p1 첫 번째 스캐너의 위치
     * @param d1 첫 번째 스캐너로부터의 거리
     * @param p2 두 번째 스캐너의 위치
     * @param d2 두 번째 스캐너로부터의 거리
     * @param p3 세 번째 스캐너의 위치
     * @param d3 세 번째 스캐너로부터의 거리
     * @return 삼각측량을 통해 추정된 위치
     */
    private Point2D.Double triangulate(Point2D.Double p1, double d1, Point2D.Double p2, double d2, Point2D.Double p3, double d3) {
        // 삼각측량 계산
        // 중심 좌표와 각 비콘으로부터의 거리를 사용하여 비콘의 위치를 추정
        double x, y;

        double A = 2*p2.x - 2*p1.x;
        double B = 2*p2.y - 2*p1.y;
        double C = Math.pow(d1, 2) - Math.pow(d2, 2) - Math.pow(p1.x, 2) + Math.pow(p2.x, 2) - Math.pow(p1.y, 2) + Math.pow(p2.y, 2);
        double D = 2*p3.x - 2*p2.x;
        double E = 2*p3.y - 2*p2.y;
        double F = Math.pow(d2, 2) - Math.pow(d3, 2) - Math.pow(p2.x, 2) + Math.pow(p3.x, 2) - Math.pow(p2.y, 2) + Math.pow(p3.y, 2);

        x = (C*E - F*B) / (E*A - B*D);
        y = (C*D - A*F) / (B*D - A*E);

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