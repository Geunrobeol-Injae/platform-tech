package bob.geunrobeol.platform.tech.bbs;

import com.ibm.jgroupsig.BBS04;
import com.ibm.jgroupsig.GS;
import com.ibm.jgroupsig.GrpKey;
import com.ibm.jgroupsig.Signature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bob.geunrobeol.platform.tech.verify.AuthKey;
import bob.geunrobeol.platform.tech.verify.AuthKeyS;

public class Company {

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