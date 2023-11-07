package bob.geunrobeol.platform.tech.location;

import java.util.List;
import java.util.stream.Collectors;

import bob.geunrobeol.platform.tech.vo.BeaconRecord;
import bob.geunrobeol.platform.tech.vo.PositionRecord;

public interface ILocationEstimator {
    public PositionRecord getPosition(BeaconRecord beaconRecord);

    public default List<PositionRecord> getPositions(List<BeaconRecord> beaconRecords) {
        return beaconRecords.stream().map(this::getPosition).collect(Collectors.toList());
    }
}