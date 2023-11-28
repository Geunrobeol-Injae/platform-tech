package bob.geunrobeol.platform.tech.config;

import java.util.HashMap;
import java.util.Map;

/**
 * 위치정보 보호 관련 설정. 위치 측위와는 별개의 Configuration으로 분리시킴.
 * @see LocationConfig
 */
public class LocationPrivacyConfig {
    // Pseudonymization Configurations
    public static final int PSEUDONYM_RSSI = -60;
    public static final int PSEUDONYM_MAX_SCANNERS = 3;

    // Dummization Configurations
    public static final Map<String, Integer> DUMMY_SCANNER_RSSI;

    static {
        DUMMY_SCANNER_RSSI = new HashMap<>();
        DUMMY_SCANNER_RSSI.put("SCAN-D", -40);
    }

    public static final int PAYLOAD_FLUSH_MAX = 16;
    public static final int PAYLOAD_FLUSH_REMAIN = 8;

    public static final String WS_PSEUDONYM_TOPIC = "/loc/ps";
}