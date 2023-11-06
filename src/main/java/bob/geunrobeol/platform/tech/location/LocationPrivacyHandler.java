package bob.geunrobeol.platform.tech.location;

import org.springframework.stereotype.Service;

import java.util.List;

import bob.geunrobeol.platform.tech.vo.BeaconRecord;
import bob.geunrobeol.platform.tech.vo.ScannerRecord;

@Service
public class LocationPrivacyHandler implements ILocationPreprocessor {
    @Override
    public void pushScanRecord(ScannerRecord scannerRecord) {
        // TODO pushScanRecord
    }

    @Override
    public List<BeaconRecord> popBeaconRecord() {
        // TODO popBeaconRecord
        return null;
    }
}
