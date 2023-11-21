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
		BeaconRecord record = new BeaconRecord();
		List<ScannerData> data = new ArrayList<>();
		data.add(new ScannerData(1, "A", -50));
		data.add(new ScannerData(1, "B", -50));
		data.add(new ScannerData(1, "C", -50));
		data.add(new ScannerData(1, "D", -50));
		record.setScanners(data);
		PositionRecord posRec = locationTriangulator.getPosition(record);
		log.info("PositionRecord: {}", posRec);
	}
}