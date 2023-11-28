package bob.geunrobeol.platform.tech.location;

import java.awt.geom.Point2D;
import java.util.List;

import bob.geunrobeol.platform.tech.vo.proc.ScannerData;

/**
 * 위치 추정 Interface. 삼변측량법이나 핑거프린트 방식 등 각종 측위 Class들이
 * 해당 Interface를 implementation하게 된다.
 */
public interface ILocationEstimator {
    public Point2D.Double getPosition(List<ScannerData> scanners);
}