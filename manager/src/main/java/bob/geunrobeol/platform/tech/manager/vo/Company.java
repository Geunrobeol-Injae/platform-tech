package bob.geunrobeol.platform.tech.manager.vo;

import com.ibm.jgroupsig.BBS04;
import com.ibm.jgroupsig.GS;
import com.ibm.jgroupsig.GrpKey;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Company {
    private final Map<Integer, AuthKey> authKeys;

    public Company() {
        this.authKeys = new HashMap<>();
    }

    public Company(List<String> texts) {
        this();
        for (String text : texts) {
            addAuthKey(text);
        }
    }

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
}