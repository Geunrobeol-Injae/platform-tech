package bob.geunrobeol.platform.tech;

import static bob.geunrobeol.platform.tech.config.VerifyConfig.KEY_DIR;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import bob.geunrobeol.platform.tech.location.ILocationPreprocessor;
import bob.geunrobeol.platform.tech.verify.AuthVerifier;
import bob.geunrobeol.platform.tech.verify.IdentityS;
import bob.geunrobeol.platform.tech.vo.proc.BeaconRecord;
import bob.geunrobeol.platform.tech.vo.raw.BeaconData;
import bob.geunrobeol.platform.tech.vo.raw.ScannerRecord;

@SpringBootTest
public class AuthVerifyTests {

    private static final Logger log = LoggerFactory.getLogger(AuthVerifyTests.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ILocationPreprocessor locationPreprocessor;
    @Autowired
    private AuthVerifier authVerifier;

    private static final String SIG_TEXT_FILEPATH = KEY_DIR + "mem-sign-1.txt";

    @Test
    public void BeaconRecordFromAop() {
        List<BeaconData> beaconDataList = List.of(new BeaconData("ble-w", new HashMap<>(), -30));
        ScannerRecord record = new ScannerRecord(1, "SCAN-A", beaconDataList);
        locationPreprocessor.pushScanRecord(record);
        List<BeaconRecord> beacons = locationPreprocessor.popBeaconRecord();
        log.info("beacons: {}", beacons);
    }

    @Test @Disabled
    public void loadSigText() throws Exception {
        File file = new File(SIG_TEXT_FILEPATH);
        JavaType type = objectMapper.getTypeFactory().constructType(String.class);
        String sigText = objectMapper.readValue(file, type);
        IdentityS identity = authVerifier.requestOpen(sigText);
        log.info("opened identity: {}", identity);
    }
}
