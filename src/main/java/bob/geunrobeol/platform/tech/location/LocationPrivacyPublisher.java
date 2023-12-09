package bob.geunrobeol.platform.tech.location;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import bob.geunrobeol.platform.tech.config.LocationPrivacyConfig;

/**
 * 위치정보 보호 WebSocket Publisher. 위치 측위와는 별도로 분리했다.
 * @see LocationPublisher
 */
@Service
public class LocationPrivacyPublisher {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 전처리 또는 가명화 관련 Event들을 Publish한다.
     * @param msg 출력할 메시지
     */
    public void publishPseudonym(String msg) {
        messagingTemplate.convertAndSend(LocationPrivacyConfig.WS_PSEUDONYM_TOPIC, msg);
    }
}
