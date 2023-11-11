package bob.geunrobeol.platform.tech.config;

public class LocationPrivacyConfig {
    // Preprocessing Configurations
    // TODO change flush delay
    public final static long FLUSH_DELAY = 4000L;
    public final static int SCANNER_THRESHOLD = 30;
    public final static int SCANNER_BASE = 10;

    // Pseudonymization Configurations

    public final static String WS_PSUDONYM_TOPIC = "/loc/ps";
}