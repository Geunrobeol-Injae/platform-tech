package bob.geunrobeol.platform.tech.config;

import java.util.HashMap;
import java.util.Map;

/**
 * 위치정보 보호 관련 설정. 위치 측위와는 별개의 Configuration으로 분리시킴.
 * @see LocationConfig
 */
public class LocationPrivacyConfig {
    // Preprocessing Configurations
    // TODO change flush delay
    public final static long FLUSH_DELAY = 4000L;
    public final static int SCANNER_THRESHOLD = 30;
    public final static int SCANNER_BASE = 10;

    // TODO Pseudonymization Configurations
    private static final int PSUDONYM_RSSI = -60;
    private static final int PSUDONYM_MAX_SCANNERS = 3;

    // TODO Dummization Configurations
    private static final Map<String, Integer> DUMMY_SCANNER_RSSI;

    static {
        DUMMY_SCANNER_RSSI = new HashMap<>();
        DUMMY_SCANNER_RSSI.put("SCAN-D", -40);
    }

    public final static String WS_PSUDONYM_TOPIC = "/loc/ps";
}