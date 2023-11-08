package bob.geunrobeol.platform.tech.location;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import bob.geunrobeol.platform.tech.config.LocationConfig;
import bob.geunrobeol.platform.tech.vo.ScannerRecord;

@Service
public class LocationPrivacyPublisher {
    private static final Logger log = LoggerFactory.getLogger(LocationPrivacyPublisher.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void publishScanner(ScannerRecord record) {
        String msg = "{}";
        try {
            msg = objectMapper.writeValueAsString(record);
        } catch (JsonProcessingException e) {
            log.error("json.writeValue.error", e);
        }

        messagingTemplate.convertAndSend(LocationConfig.WS_SCANNER_TOPIC, msg);
    }

    public void publishPsudonym(String msg) {
        messagingTemplate.convertAndSend(LocationConfig.WS_PSUDONYM_TOPIC, msg);
    }
}
