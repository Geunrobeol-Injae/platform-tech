package bob.geunrobeol.platform.tech.verify;

import static bob.geunrobeol.platform.tech.config.VerifyConfig.OPEN_URL;

import com.ibm.jgroupsig.BBS04;
import com.ibm.jgroupsig.GS;
import com.ibm.jgroupsig.GrpKey;
import com.ibm.jgroupsig.Signature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class AuthVerifier {
    private static final Logger log = LoggerFactory.getLogger(AuthVerifier.class);
    private static final String OPEN_REQ_USER = "SYS_ADMIN";
    private static final String OPEN_REQ_REASON = "UN_AUTH_DANGER_ZONE";

    private final Map<Integer, AuthKey> authKeys;
    private RestTemplate restTemplate;

    public AuthVerifier(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.authKeys = new HashMap<>();
    }

    public AuthVerifier(RestTemplate restTemplate, List<AuthKeyS> authKeySList) {
        this(restTemplate);
        for (AuthKeyS a : authKeySList) {
            addAuthKey(a);
        }
    }

    private void addAuthKey(AuthKeyS a) {
        int authId = a.authId();
        String grpKeyText = a.grpKeyText();
        String zones = a.zones();

        // 중복된 authId 검사
        if (authKeys.containsKey(authId)) {
            throw new IllegalArgumentException("Duplicate authId: " + authId);
        }

        // zones 형식 검증
        if (!zones.matches("[01]+")) {
            throw new IllegalArgumentException("Invalid zones format: " + zones);
        }

        // grpKeyText를 사용하여 BBS04 객체 생성
        BBS04 bbs04;
        try {
            bbs04 = new BBS04();
            bbs04.setup();
            GrpKey grpKey = new GrpKey(GS.BBS04_CODE, grpKeyText);
            bbs04.setGrpKey(grpKey);
        } catch (Exception e) {
            throw new RuntimeException("Error creating BBS04 instance", e);
        }

        // AuthKey 맵에 추가
        authKeys.put(authId, new AuthKey(authId, bbs04, zones));
    }
    
    public boolean verifyAccessAuth(String sigText, int authId) {
        // retrieve authKey
        AuthKey authKey = authKeys.get(authId);
        if (authKey == null) return false;

        // Parse into signature then verify
        Signature sig;
        boolean isVerified;
        try {
            sig = new Signature(GS.BBS04_CODE, sigText);
            isVerified = authKey.bbs04.verify(sig, String.valueOf(authId));
        } catch (Exception e) {
            isVerified = false;
        }

        return isVerified;
    }

    @Nullable
    public IdentityS requestOpen(String sigText) {
        Map<String, String> map = new HashMap<>();
        map.put("sigText", sigText);
        map.put("adminId", OPEN_REQ_USER);
        map.put("reason", OPEN_REQ_REASON);

        // To http entity
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(map, headers);

        IdentityS identity = restTemplate.postForObject(OPEN_URL, requestEntity, IdentityS.class);
        if (identity == null) {
            log.warn("Open Requested but nothing returned:\n{}", sigText);
        }

        return identity;
    }
}