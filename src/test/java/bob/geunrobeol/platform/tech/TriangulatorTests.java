package bob.geunrobeol.platform.tech;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import bob.geunrobeol.platform.tech.location.LocationTriangulator;
import bob.geunrobeol.platform.tech.vo.BeaconPosition;
import bob.geunrobeol.platform.tech.vo.BeaconRecord;

@SpringBootTest
public class TriangulatorTests {

    private static final Logger log = LoggerFactory.getLogger(TriangulatorTests.class);

    @Autowired
    private LocationTriangulator locationTriangulator;

    @Test
    public void triangulate1() {
        BeaconRecord record = new BeaconRecord();
        BeaconPosition position;

        // 첫 번째 측위 대상자의 첫 번째 타임스탬프 데이터
        record.putScanner("A", 1L, -65);
        record.putScanner("B", 1L, -80);
        record.putScanner("C", 1L, -25);
        record.putScanner("D", 1L, -40);

        position = locationTriangulator.getPosition(record);
        log.info("Beacon pos(1): {}, {}", position.pos().x, position.pos().y);

        // 첫 번째 측위 대상자의 두 번째 타임스탬프 데이터
        record.putScanner("A", 2L, -50);
        record.putScanner("B", 2L, -70);
        record.putScanner("C", 2L, -40);
        record.putScanner("D", 2L, -55);

        position = locationTriangulator.getPosition(record);
        log.info("Beacon pos(2): {}, {}", position.pos().x, position.pos().y);
    }

    @Test
    public void triangulate2() {
        BeaconRecord record = new BeaconRecord();
        BeaconPosition position;

        // 두 번째 측위 대상자의 첫 번째 타임스탬프 데이터
        record.putScanner("A", 1L, -25);
        record.putScanner("B", 1L, -65);
        record.putScanner("C", 1L, -40);
        record.putScanner("D", 1L, -80);

        position = locationTriangulator.getPosition(record);
        log.info("Beacon pos(1): {}, {}", position.pos().x, position.pos().y);

        // 두 번째 측위 대상자의 두 번째 타임스탬프 데이터
        record.putScanner("A", 2L, -40);
        record.putScanner("B", 2L, -25);
        record.putScanner("C", 2L, -80);
        record.putScanner("D", 2L, -65);

        position = locationTriangulator.getPosition(record);
        log.info("Beacon pos(2): {}, {}", position.pos().x, position.pos().y);
    }
}
