/* 

package bob.geunrobeol.platform.tech;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import bob.geunrobeol.platform.tech.location.LocationTriangulator;
import bob.geunrobeol.platform.tech.vo.proc.ScannerData;

@SpringBootTest
public class TriangulatorTests {

    private static final Logger log = LoggerFactory.getLogger(TriangulatorTests.class);

    @Autowired
    private LocationTriangulator locationTriangulator;

    @Test
    public void triangulate1() {
        List<ScannerData> scanners = new ArrayList<>();
        Point2D.Double pos;

        // 첫 번째 측위 대상자의 첫 번째 타임스탬프 데이터
        scanners.add(new ScannerData("SCAN-A", 1L, -100));
        scanners.add(new ScannerData("SCAN-B", 1L, -100));
        scanners.add(new ScannerData("SCAN-C", 1L, -100));
        scanners.add(new ScannerData("SCAN-D", 1L, -0));

        pos = locationTriangulator.getPosition(scanners);
        log.info("Beacon(1) pos(1): {}, {}", pos.x, pos.y);
    }

    @Test
    public void triangulate2() {
        List<ScannerData> scanners = new ArrayList<>();
        Point2D.Double pos;

        // 두 번째 측위 대상자의 첫 번째 타임스탬프 데이터
        scanners.add(new ScannerData("SCAN-A", 1L, -25));
        scanners.add(new ScannerData("SCAN-B", 1L, -65));
        scanners.add(new ScannerData("SCAN-C", 1L, -40));
        scanners.add(new ScannerData("SCAN-D", 1L, -80));

        pos = locationTriangulator.getPosition(scanners);
        log.info("Beacon(2) pos(1): {}, {}", pos.x, pos.y);
    }

    @Test
    public void triangulate3() {
        List<ScannerData> scanners = new ArrayList<>();
        Point2D.Double pos;

        // 세 번째 측위 대상자의 첫 번째 타임스탬프 데이터

        pos = locationTriangulator.getPosition(scanners);
        log.info("Beacon(3) pos(1): {}, {}", pos.x, pos.y);
    }

    @Test
    public void triangulate4() {
        List<ScannerData> scanners = new ArrayList<>();
        Point2D.Double pos;

        // 네 번째 측위 대상자의 첫 번째 타임스탬프 데이터
        scanners.add(new ScannerData("SCAN-C", 1L, -40));

        pos = locationTriangulator.getPosition(scanners);
        log.info("Beacon(4) pos(1): {}, {}", pos.x, pos.y);
    }

    @Test
    public void triangulate5() {
        List<ScannerData> scanners = new ArrayList<>();
        Point2D.Double pos;

        // 다섯 번째 측위 대상자의 첫 번째 타임스탬프 데이터
        scanners.add(new ScannerData("SCAN-B", 1L, -75));
        scanners.add(new ScannerData("SCAN-C", 1L, -70));

        pos = locationTriangulator.getPosition(scanners);
        log.info("Beacon(5) pos(1): {}, {}", pos.x, pos.y);
    }

    @Test
    public void triangulate6() {
        List<ScannerData> scanners = new ArrayList<>();
        Point2D.Double pos;

        // 여섯 번째 측위 대상자의 첫 번째 타임스탬프 데이터
        scanners.add(new ScannerData("SCAN-A", 1L, -15));
        scanners.add(new ScannerData("SCAN-B", 1L, -35));
        scanners.add(new ScannerData("SCAN-C", 1L, -20));

        pos = locationTriangulator.getPosition(scanners);
        log.info("Beacon(6) pos(1): {}, {}", pos.x, pos.y);
    }
}

*/