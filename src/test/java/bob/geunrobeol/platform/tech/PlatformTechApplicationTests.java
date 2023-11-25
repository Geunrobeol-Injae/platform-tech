package bob.geunrobeol.platform.tech;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import bob.geunrobeol.platform.tech.location.LocationTriangulator;
import bob.geunrobeol.platform.tech.vo.BeaconRecord;
import bob.geunrobeol.platform.tech.vo.PositionRecord;
import bob.geunrobeol.platform.tech.vo.ScannerData;

@SpringBootTest
class PlatformTechApplicationTests {

	private static final Logger log = LoggerFactory.getLogger(PlatformTechApplicationTests.class);

	@Autowired
	private LocationTriangulator locationTriangulator;


	@Test
	void contextLoads() {
		// 첫 번째 측위 대상자의 첫 번째 타임스탬프 데이터
		BeaconRecord target1Record1 = new BeaconRecord();
		List<ScannerData> target1Data1 = new ArrayList<>();
		target1Data1.add(new ScannerData(1, "A", -65));
		target1Data1.add(new ScannerData(1, "B", -80));
		target1Data1.add(new ScannerData(1, "C", -25));
		target1Data1.add(new ScannerData(1, "D", -40));
		target1Record1.setScanners(target1Data1);

		// 첫 번째 측위 대상자의 두 번째 타임스탬프 데이터
		BeaconRecord target1Record2 = new BeaconRecord();
		List<ScannerData> target1Data2 = new ArrayList<>();
		target1Data2.add(new ScannerData(2, "A", -50));
		target1Data2.add(new ScannerData(2, "B", -70));
		target1Data2.add(new ScannerData(2, "C", -40));
		target1Data2.add(new ScannerData(2, "D", -55));
		target1Record2.setScanners(target1Data2);

		// 두 번째 측위 대상자의 첫 번째 타임스탬프 데이터
		BeaconRecord target2Record1 = new BeaconRecord();
		List<ScannerData> target2Data1 = new ArrayList<>();
		target2Data1.add(new ScannerData(1, "A", -25));
		target2Data1.add(new ScannerData(1, "B", -65));
		target2Data1.add(new ScannerData(1, "C", -40));
		target2Data1.add(new ScannerData(1, "D", -80));
		target2Record1.setScanners(target2Data1);

		// 두 번째 측위 대상자의 두 번째 타임스탬프 데이터
		BeaconRecord target2Record2 = new BeaconRecord();
		List<ScannerData> target2Data2 = new ArrayList<>();
		target2Data2.add(new ScannerData(2, "A", -40));
		target2Data2.add(new ScannerData(2, "B", -25));
		target2Data2.add(new ScannerData(2, "C", -80));
		target2Data2.add(new ScannerData(2, "D", -65));
		target2Record2.setScanners(target2Data2);

		// 각 BeaconRecord에 대한 위치 추정
		PositionRecord posRec1 = locationTriangulator.getPosition(target1Record1);
		PositionRecord posRec2 = locationTriangulator.getPosition(target1Record2);
		PositionRecord posRec3 = locationTriangulator.getPosition(target2Record1);
		PositionRecord posRec4 = locationTriangulator.getPosition(target2Record2);

		// 로그 출력
		log.info("Target 1, Timestamp 1: {}", posRec1);
		log.info("Target 1, Timestamp 2: {}", posRec2);
		log.info("Target 2, Timestamp 1: {}", posRec3);
		log.info("Target 2, Timestamp 2: {}", posRec4);
	}


}