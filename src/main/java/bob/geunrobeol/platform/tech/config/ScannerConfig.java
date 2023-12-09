package bob.geunrobeol.platform.tech.config;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

public class ScannerConfig {

    public static final Map<String, Point2D.Double> SCANNER_POSITIONS;

    static {
        SCANNER_POSITIONS = new HashMap<>();
        SCANNER_POSITIONS.put("SCAN-A", new Point2D.Double(0, 0));
        SCANNER_POSITIONS.put("SCAN-B", new Point2D.Double(400, 0));
        SCANNER_POSITIONS.put("SCAN-C", new Point2D.Double(0, 400));
        SCANNER_POSITIONS.put("SCAN-D", new Point2D.Double(400, 400));
    }
}
