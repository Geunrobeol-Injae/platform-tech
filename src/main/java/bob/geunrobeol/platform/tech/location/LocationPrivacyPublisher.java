package bob.geunrobeol.platform.tech.location;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import bob.geunrobeol.platform.tech.config.LocationPrivacyConfig;

@Service
public class LocationPrivacyPublisher {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void publishPsudonym(String msg) {
        messagingTemplate.convertAndSend(LocationPrivacyConfig.WS_PSUDONYM_TOPIC, msg);
    }
}
