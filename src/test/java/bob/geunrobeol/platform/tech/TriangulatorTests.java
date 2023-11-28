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
        scanners.add(new ScannerData("A", 1L, -65));
        scanners.add(new ScannerData("B", 1L, -80));
        scanners.add(new ScannerData("C", 1L, -25));
        scanners.add(new ScannerData("D", 1L, -40));

        pos = locationTriangulator.getPosition(scanners);
        log.info("Beacon(1) pos(1): {}, {}", pos.x, pos.y);

        // 첫 번째 측위 대상자의 두 번째 타임스탬프 데이터
        scanners.clear();
        scanners.add(new ScannerData("A", 2L, -50));
        scanners.add(new ScannerData("B", 2L, -70));
        scanners.add(new ScannerData("C", 2L, -40));
        scanners.add(new ScannerData("D", 2L, -55));

        pos = locationTriangulator.getPosition(scanners);
        log.info("Beacon(1) pos(2): {}, {}", pos.x, pos.y);
    }

    @Test
    public void triangulate2() {
        List<ScannerData> scanners = new ArrayList<>();
        Point2D.Double pos;

        // 두 번째 측위 대상자의 첫 번째 타임스탬프 데이터
        scanners.add(new ScannerData("A", 1L, -25));
        scanners.add(new ScannerData("B", 1L, -65));
        scanners.add(new ScannerData("C", 1L, -40));
        scanners.add(new ScannerData("D", 1L, -80));

        pos = locationTriangulator.getPosition(scanners);
        log.info("Beacon(2) pos(1): {}, {}", pos.x, pos.y);

        // 두 번째 측위 대상자의 두 번째 타임스탬프 데이터
        scanners.clear();
        scanners.add(new ScannerData("A", 2L, -40));
        scanners.add(new ScannerData("B", 2L, -25));
        scanners.add(new ScannerData("C", 2L, -80));
        scanners.add(new ScannerData("D", 2L, -65));

        pos = locationTriangulator.getPosition(scanners);
        log.info("Beacon(2) pos(2): {}, {}", pos.x, pos.y);
    }
}