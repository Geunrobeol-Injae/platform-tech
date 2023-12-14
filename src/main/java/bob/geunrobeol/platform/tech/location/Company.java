package bob.geunrobeol.platform.tech.location;

import com.ibm.jgroupsig.BBS04;
import com.ibm.jgroupsig.GS;
import com.ibm.jgroupsig.GrpKey;
import com.ibm.jgroupsig.Signature;

import bob.geunrobeol.platform.tech.dto.AuthKeyS;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.Serializable;

public class Company {
    private static class AuthKey {
        public final int authId;
        public final BBS04 bbs04;
        public final String zones;

        public AuthKey(int authId, BBS04 bbs04, String zones) {
            this.authId = authId;
            this.bbs04 = bbs04;
            this.zones = zones;
        }
    }


    
    private final Map<Integer, AuthKey> authKeys;
    private final Map<String, String> identities;

    public Company() {
        this.authKeys = new HashMap<>();
        this.identities = new HashMap<>();
    }

    public Company(List<AuthKeyS> authKeySList) {
        this();
        for (AuthKeyS a : authKeySList) {
            addAuthKey(a);
        }
    }
    
    //더 아래있는 addAuthKey는 콤마로 나눔. 이건 역직렬화 통해 필드대로 가져옴. 
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
            GrpKey grpKey = new GrpKey(GS.BBS04_CODE, grpKeyText);
            bbs04 = new BBS04();
            bbs04.setGrpKey(grpKey);
        } catch (Exception e) {
            throw new RuntimeException("Error creating BBS04 instance", e);
        }

        // AuthKey 맵에 추가
        authKeys.put(authId, new AuthKey(authId, bbs04, zones));
    }

    /* 
    public void addAuthKey(String text) {
        List<String> parts = Arrays.asList(text.split(","));
        if (parts.size() != 3) throw new IllegalArgumentException("authKey.size");

        // Parse authId
        int authId = Integer.parseInt(parts.get(0));
        if (authKeys.containsKey(authId)) throw new IllegalArgumentException("authKey.authId.dupl");

        // Parse zones
        if (!parts.get(2).matches("[01]+")) throw new IllegalArgumentException("authKey.zones.values");
        String zones = parts.get(2);

        // Parse grpKey and create bbs04 instance
        BBS04 bbs04;
        try {
            String grpKeyText = parts.get(1);
            GrpKey grpKey = new GrpKey(GS.BBS04_CODE, grpKeyText);

            bbs04 = new BBS04();
            bbs04.setGrpKey(grpKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        authKeys.put(authId, new AuthKey(authId, bbs04, zones));
    }
    */

    
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

    public void appendIdentity(String sigText, String name) {
        identities.put(sigText, name);
    }

    public Map<String, String> getIdentities() {
        return new HashMap<>(identities);
    }
}