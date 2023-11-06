package bob.geunrobeol.platform.tech.location;

import java.util.List;

import bob.geunrobeol.platform.tech.vo.BeaconRecord;
import bob.geunrobeol.platform.tech.vo.ScannerRecord;

public interface ILocationPreprocessor {
    public void pushScanRecord(ScannerRecord scannerRecord);
    public List<BeaconRecord> popBeaconRecord();
}