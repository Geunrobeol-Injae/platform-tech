package bob.geunrobeol.platform.tech.location;

import org.springframework.stereotype.Service;

import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import bob.geunrobeol.platform.tech.config.LocationPrivacyConfig;
import bob.geunrobeol.platform.tech.config.ScannerConfig;
import bob.geunrobeol.platform.tech.vo.proc.ScannerData;

/**
 * 삼각측량법을 구현한 Class.
 */
@Service
public class LocationTriangulator implements ILocationEstimator {
    /**
     * BeaconRecord에서 제공된 스캐너 데이터를 사용하여 사용자의 위치를 추정한다.
     * 이 메소드는 RSSI 값을 기반으로 가장 강한 신호를 보낸 상위 3개의 스캐너를 선택하고,
     * 각 스캐너로부터의 거리를 계산하여 삼각측량을 수행한다.
     * @param scanners BeaconRecord에 포함된 스캐너 데이터 목록
     * @return 추정된 사용자의 위치 좌표 (x, y). Map.Entry 객체로, key는 x좌표, value는 y좌표를 나타낸다.
     */
    public Point2D.Double getPosition(List<ScannerData> scanners) {
        boolean isSpecificScannerZero = scanners.stream().anyMatch(s -> s.getScannerId().equals(LocationPrivacyConfig.DUMMY_SCANNER_ID) && s.getRssi() == 0);
        long nonSpecificMinusHundredCount = scanners.stream().filter(s -> !s.getScannerId().equals(LocationPrivacyConfig.DUMMY_SCANNER_ID) && s.getRssi() == -100).count();

        if (isSpecificScannerZero && nonSpecificMinusHundredCount == scanners.size() - 1) {
            return ScannerConfig.SCANNER_POSITIONS.get(LocationPrivacyConfig.DUMMY_SCANNER_ID);
}

        
        // 삼각측량 로직
        List<ScannerData> closestScanners = scanners.stream()
            .sorted(Comparator.comparingDouble(ScannerData::getRssi).reversed())
            .limit(3)
            .collect(Collectors.toList());

        Point2D.Double[] points = new Point2D.Double[3];
        double[] distances = new double[3];
        for (int i = 0; i < 3; i++) {
            ScannerData scanner = closestScanners.get(i);
            points[i] = ScannerConfig.SCANNER_POSITIONS.get(scanner.getScannerId());
            distances[i] = calculateDistanceFromRssi(scanner.getRssi());
        }

        return triangulate(points[0], distances[0], points[1], distances[1], points[2], distances[2]);
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
}