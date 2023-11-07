package bob.geunrobeol.platform.tech.location;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bob.geunrobeol.platform.tech.vo.BeaconRecord;
import bob.geunrobeol.platform.tech.vo.ScannerRecord;

@Service
public class LocationPrivacyHandler implements ILocationPreprocessor {
    private static final Logger log = LoggerFactory.getLogger(LocationPrivacyHandler.class);

    @Override
    public void pushScanRecord(ScannerRecord scannerRecord) {
        log.info("push {}", scannerRecord);
        // TODO pushScanRecord
    }

    @Override
    public List<BeaconRecord> popBeaconRecord() {
        // TODO popBeaconRecord
        return new ArrayList<BeaconRecord>();
    }
}