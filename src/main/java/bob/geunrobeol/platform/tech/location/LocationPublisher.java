package bob.geunrobeol.platform.tech.location;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import bob.geunrobeol.platform.tech.config.WebSocketConfig;
import bob.geunrobeol.platform.tech.dto.AuthKeyS;
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
    private ResourceLoader resourceLoader;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ILocationPreprocessor locationPreprocessor;

    @Autowired
    private ILocationEstimator locationEstimator;

    private ThirdParty thirdParty;
    private Map<Integer, Employee> employees;
    private Company company;

    private static final List<String> accessAuthTable;
    static {
        accessAuthTable = new ArrayList<>();
        accessAuthTable.add("0,11");
        accessAuthTable.add("1,10");
    }

    private static final List<String> employeeTable;
    static {
        employeeTable = new ArrayList<>();
        employeeTable.add("1,ble-w,0");  
        employeeTable.add("2,ble-y,0");
        employeeTable.add("3,ble-g,1");
    }

    public LocationPublisher() {
        this.thirdParty = new ThirdParty(accessAuthTable);

        
        this.employees = new HashMap<>();
        for (String empData : employeeTable) {
            Employee e = thirdParty.addMember(empData);
            employees.put(e.id, e);
        }


        List<AuthKeyS> authKeySList = readFromFile("classpath:xyz.txt", AuthKeyS.class);
        this.company = new Company(authKeySList);
    }

    private <T> List<T> readFromFile(String filePath, Class<T> valueType) {
        try {
            Resource resource = resourceLoader.getResource(filePath);
            InputStream inputStream = resource.getInputStream();

            return objectMapper.readValue(inputStream, objectMapper.getTypeFactory().constructCollectionType(List.class, valueType));
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + filePath, e);
        }
    }


    


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
            if (isInDangerZone(positionPoint)) {
                // BeaconRecord에서 sigText와 authId 추출
                String sigText = r.getSigText();
                int authId = r.getAuthId();

                // 위험 구역 접근 권한 여부 판단
                if (!company.verifyAccessAuth(sigText, authId)) {
                    // 위험 구역 접근 권한 없을 시 제3자(Third Party)에게 신원요청 
                    Map.Entry<String, String> identityInfo = thirdParty.open(sigText);
                    log.info("Identity opened: {}, Log: {}", identityInfo.getKey(), identityInfo.getValue());
                    log.warn("Unauthorized access to dangerous area by employee with ID {}.", r.getBeaconId());
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

    private boolean isInDangerZone(Point2D.Double positionPoint) {
        // X와 Y 좌표가 10 이상 20 이하인 경우 위험 구역으로 간주한다고 가정
        return positionPoint.x >= 10 && positionPoint.x <= 20 && positionPoint.y >= 10 && positionPoint.y <= 20;
    }
}