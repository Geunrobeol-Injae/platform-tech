package bob.geunrobeol.platform.tech.location;

import org.springframework.stereotype.Component;

@Component
public class PseudonymProvider {
    public String refresh(String org, String key) {
        // TODO refresh
        return org + key;
    }
}
