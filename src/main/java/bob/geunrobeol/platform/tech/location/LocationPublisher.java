package bob.geunrobeol.platform.tech.location;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bob.geunrobeol.platform.tech.config.VerifyConfig;
import bob.geunrobeol.platform.tech.config.WebSocketConfig;
import bob.geunrobeol.platform.tech.verify.AuthVerifier;
import bob.geunrobeol.platform.tech.verify.IdentityS;
import bob.geunrobeol.platform.tech.vo.BeaconPosition;
import bob.geunrobeol.platform.tech.vo.proc.BeaconRecord;
import bob.geunrobeol.platform.tech.vo.raw.ScannerRecord;

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

    @Autowired
    private AuthVerifier authVerifier;

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
        messagingTemplate.convertAndSend(WebSocketConfig.WS_SCANNER_TOPIC, msg);
    }

    /**
     * 위치정보 측위정보를 주기적으로 송신한다.
     * @see WebSocketConfig#WS_POSITION_DELAYS
     */
    @Scheduled(fixedDelay = WebSocketConfig.WS_POSITION_DELAYS) 
    public void publishPositions() {
        List<BeaconRecord> records = locationPreprocessor.popBeaconRecord();
        List<BeaconPosition> positions = new ArrayList<>();

        for (BeaconRecord r : records) {
            Point2D.Double positionPoint = locationEstimator.getPosition(r.getScanners());
            BeaconPosition position = new BeaconPosition(r.getBeaconId(), r.getTimestamp(), positionPoint, r.getPayloads());
            positions.add(position);

            // 근로자 위치가 위험 구역인지 판단
            if (VerifyConfig.isInDangerZone(positionPoint)) {
                // 위험 구역 접근 권한 여부 판단
                if (!authVerifier.verifyAccessAuth(r.getSigText(), r.getAuthId())) {
                    // 위험 구역 접근 권한 없을 시 제3자(Third Party)에게 신원요청 
                    IdentityS identity = authVerifier.requestOpen(r.getSigText());
                    if (identity != null) {
                        log.info("Unauthorized access to dangerous area by employee with ID {}: {}.", identity.id(), identity.name());
                        sendAlert(position, identity);
                    }
                }
            }
        }

        String msg = "[]";
        try {
            msg = objectMapper.writeValueAsString(positions);
        } catch (JsonProcessingException e) {
            log.error("json.writeValue.error", e);
        }
        messagingTemplate.convertAndSend(WebSocketConfig.WS_POSITION_TOPIC, msg);
    }

    private void sendAlert(BeaconPosition pos, IdentityS identity) {
        Map<String, Object> map = new HashMap<>();

        map.put("timestamp", pos.timestamp());
        map.put("beaconId", pos.beaconId());
        map.put("posX", pos.pos().x);
        map.put("posY", pos.pos().y);

        map.put("id", identity.id());
        map.put("name", identity.name());

        String msg = "{}";
        try {
            msg = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            log.error("json.writeValue.error", e);
        }
        messagingTemplate.convertAndSend(WebSocketConfig.WS_POSITION_TOPIC, msg);
    }
}