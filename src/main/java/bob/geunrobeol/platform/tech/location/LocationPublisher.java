package bob.geunrobeol.platform.tech.location;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

import bob.geunrobeol.platform.tech.config.LocationConfig;
import bob.geunrobeol.platform.tech.vo.BeaconRecord;
import bob.geunrobeol.platform.tech.vo.PositionRecord;
import bob.geunrobeol.platform.tech.vo.ScannerRecord;

/**
 * 위치정보 관련 WebSocket Publisher. Scanner 수신 데이터나 위치정보 측위 관련 데이터들을 송신한다.
 */
@Service
public class LocationPublisher {
    private static final Logger log = LoggerFactory.getLogger(LocationPublisher.class);
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ILocationPreprocessor locationPreprocessor;

    @Autowired
    private ILocationEstimator locationEstimator;

    /**
     * Scanner 데이터를 송신한다.
     * @param record Scanner 데이터
     */
    public void publishScanner(ScannerRecord record) {
        String msg = "{}";
        try {
            msg = objectMapper.writeValueAsString(record);
        } catch (JsonProcessingException e) {
            log.error("json.writeValue.error", e);
        }

        messagingTemplate.convertAndSend(LocationConfig.WS_SCANNER_TOPIC, msg);
    }

    /**
     * 위치정보 측위정보를 주기적으로 송신한다.
     * @see LocationConfig#WS_POSITION_DELAYS
     */
    @Scheduled(fixedDelay = LocationConfig.WS_POSITION_DELAYS)
    public void publishPositions() {
        List<BeaconRecord> records = locationPreprocessor.popBeaconRecord();
        List<PositionRecord> positions = locationEstimator.getPositions(records);

        // TODO save positions

        String msg = "[]";
        try {
            msg = objectMapper.writeValueAsString(positions);
        } catch (JsonProcessingException e) {
            log.error("json.writeValue.error", e);
        }

        messagingTemplate.convertAndSend(LocationConfig.WS_POSITION_TOPIC, msg);
    }
}