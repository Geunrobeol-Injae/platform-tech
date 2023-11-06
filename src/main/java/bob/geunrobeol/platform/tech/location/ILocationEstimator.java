package bob.geunrobeol.platform.tech.location;

import bob.geunrobeol.platform.tech.vo.BeaconRecord;
import bob.geunrobeol.platform.tech.vo.PositionRecord;

public interface ILocationEstimator {
    public PositionRecord getPosition(BeaconRecord beaconRecord);
}
