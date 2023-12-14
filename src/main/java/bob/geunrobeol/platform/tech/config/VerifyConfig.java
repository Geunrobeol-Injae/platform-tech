package bob.geunrobeol.platform.tech.config;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bob.geunrobeol.platform.tech.verify.AuthKeyS;
import bob.geunrobeol.platform.tech.verify.AuthVerifier;

@Configuration
public class VerifyConfig {

    private static final Logger log = LoggerFactory.getLogger(VerifyConfig.class);

    public static final String KEY_DIR = "keys/";

    public static final String GRP_AUTH_KEYS = "grp-auth-keys.txt";

    public static final String OPEN_URL = "http://manager:8080/open";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    AuthVerifier authVerifier() {
        List<AuthKeyS> authKeySList = readFromFile(KEY_DIR + GRP_AUTH_KEYS, AuthKeyS.class);
        return new AuthVerifier(restTemplate, authKeySList);
    }

    private <T> List<T> readFromFile(String filePath, Class<T> valueType) {
        try {
            File file = new File(filePath);
            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, valueType);
            return objectMapper.readValue(file, type);
        } catch (FileNotFoundException e) {
            log.error("{} initialize with empty list.", e.getMessage());
            return new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + filePath, e);
        }
    }

    public static boolean isInDangerZone(Point2D.Double positionPoint) {
        // X와 Y 좌표가 10 이상 20 이하인 경우 위험 구역으로 간주한다고 가정
        return positionPoint.x >= 10 && positionPoint.x <= 20 && positionPoint.y >= 10 && positionPoint.y <= 20;
    }
}