package bob.geunrobeol.platform.tech.location;

import java.util.List;

import bob.geunrobeol.platform.tech.vo.proc.BeaconRecord;
import bob.geunrobeol.platform.tech.vo.raw.ScannerRecord;

/**
 * 위치정보 전처리 Interface. Scanner로 부터 수신된 데이터들을 Beacon 별로 정리한다.
 */
public interface ILocationPreprocessor {
    public void pushScanRecord(ScannerRecord scannerRecord);
    public List<BeaconRecord> popBeaconRecord();
}