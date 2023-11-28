package bob.geunrobeol.platform.tech.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 관련 설정.
 */
@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    // WebSocket 관련 Constants
    public static final String WS_SCANNER_TOPIC = "/loc/sc";
    public static final String WS_POSITION_TOPIC = "/loc/pos";
    public static final long WS_POSITION_DELAYS = 2000L;
    private static final String ORIGIN_PATTERNS = "*";

    /**
     * WebSocket endpoint 설정. Client는 해당 endpoint를 통해 기본적인 Connection을 수립한다.
     * @param registry StompEndpointRegistry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns(ORIGIN_PATTERNS).withSockJS();
    }

    /**
     * WebSocket에서 STOMP Channel들의 설정. 각 Channel 별로 Message가 오고간다.
     * @param config MessageBrokerRegistry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(
                WS_SCANNER_TOPIC,
                LocationPrivacyConfig.WS_PSEUDONYM_TOPIC,
                WS_POSITION_TOPIC);
    }
}
