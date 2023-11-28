package bob.geunrobeol.platform.tech;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bob.geunrobeol.platform.tech.vo.BeaconWrapper;
import bob.geunrobeol.platform.tech.vo.proc.BeaconRecord;
import bob.geunrobeol.platform.tech.vo.proc.ScannerData;
import bob.geunrobeol.platform.tech.vo.raw.BeaconData;
import bob.geunrobeol.platform.tech.vo.raw.ScannerRecord;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class BeaconWrapperTest {

    private static final Logger log = LoggerFactory.getLogger(BeaconWrapperTest.class);

    @Test
    public void testDummyingLogic() {

        BeaconWrapper beaconWrapper = new BeaconWrapper("testBeacon");

        // 스캐너 데이터 추가
        beaconWrapper.putScanner(new ScannerRecord(1L, "A", List.of(new BeaconData("testBeacon", new HashMap<>(), -20))), new BeaconData("testBeacon", new HashMap<>(), -20));
        beaconWrapper.putScanner(new ScannerRecord(1L, "B", List.of(new BeaconData("testBeacon", new HashMap<>(), -50))), new BeaconData("testBeacon", new HashMap<>(), -50));
        beaconWrapper.putScanner(new ScannerRecord(1L, "C", List.of(new BeaconData("testBeacon", new HashMap<>(), -70))), new BeaconData("testBeacon", new HashMap<>(), -70));
        beaconWrapper.putScanner(new ScannerRecord(1L, "D", List.of(new BeaconData("testBeacon", new HashMap<>(), -30))), new BeaconData("testBeacon", new HashMap<>(), -30));

        // putScanner 후 로그 출력
        logAfterUpdate(beaconWrapper);



        BeaconWrapper beaconWrapper2 = new BeaconWrapper("testBeacon2");

        // 스캐너 데이터 추가
        beaconWrapper2.putScanner(new ScannerRecord(1L, "A", List.of(new BeaconData("testBeacon2", new HashMap<>(), -20))), new BeaconData("testBeacon2", new HashMap<>(), -20));
        beaconWrapper2.putScanner(new ScannerRecord(1L, "B", List.of(new BeaconData("testBeacon2", new HashMap<>(), -50))), new BeaconData("testBeacon2", new HashMap<>(), -50));
        beaconWrapper2.putScanner(new ScannerRecord(1L, "C", List.of(new BeaconData("testBeacon2", new HashMap<>(), -70))), new BeaconData("testBeacon2", new HashMap<>(), -70));
        beaconWrapper2.putScanner(new ScannerRecord(1L, "D", List.of(new BeaconData("testBeacon2", new HashMap<>(), -30))), new BeaconData("testBeacon2", new HashMap<>(), -60));

        // putScanner 후 로그 출력
        logAfterUpdate(beaconWrapper2);

    }

    private void logAfterUpdate(BeaconWrapper beaconWrapper) {
        log.info("After update: " + formatScannerData(beaconWrapper));
    }

    private String formatScannerData(BeaconWrapper beaconWrapper) {
        BeaconRecord record = beaconWrapper.getBeaconRecord();
        return record.getScanners().stream()
            .map(scannerData -> scannerData.getScannerId() + ": RSSI = " + scannerData.getRssi())
            .collect(Collectors.joining(", "));
    }
}
