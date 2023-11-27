package bob.geunrobeol.platform.tech.location;

import java.util.List;
import java.util.stream.Collectors;

import bob.geunrobeol.platform.tech.vo.BeaconPosition;
import bob.geunrobeol.platform.tech.vo.BeaconRecord;

/**
 * 위치 추정 Interface. 삼변측량법이나 핑거프린트 방식 등 각종 측위 Class들이
 * 해당 Interface를 implementation하게 된다.
 */
public interface ILocationEstimator {
    public BeaconPosition getPosition(BeaconRecord beacon);

    public default List<BeaconPosition> getPositions(List<BeaconRecord> beacons) {
        return beacons.stream().map(this::getPosition).collect(Collectors.toList());
    }
}