package bob.geunrobeol.platform.tech.location;

import com.ibm.jgroupsig.BBS04;
import com.ibm.jgroupsig.GS;
import com.ibm.jgroupsig.IndexProof;
import com.ibm.jgroupsig.MemKey;
import com.ibm.jgroupsig.Signature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class ThirdParty {
    private static final Logger log = LoggerFactory.getLogger(ThirdParty.class);

    private static class AccessAuth {
        public final int authId;
        public final BBS04 bbs04;
        public final String grpKeyText;
        public final String zones;
        private final List<Identity> identities;
        private long identityIndex;

        public AccessAuth(int authId, BBS04 bbs04, String grpKeyText, String zones) {
            this.authId = authId;
            this.bbs04 = bbs04;
            this.grpKeyText = grpKeyText;
            this.zones = zones;
            this.identities = new ArrayList<>();
            this.identityIndex = 0L;
        }

        @Nullable
        public Identity getIdentity(long index) {
            return identities.stream()
                    .filter(i -> i.index == index)
                    .findAny()
                    .orElse(null);
        }

        public void addIdentity(int id, String name) {
            identities.add(new Identity(authId, identityIndex++, id, name));
        }
    }

    private static class Identity {
        public final int authId;
        public final long index;
        public final int id;
        public final String name;

        public Identity(int authId, long index, int id, String name) {
            this.authId = authId;
            this.index = index;
            this.id = id;
            this.name = name;
        }
    }

    private final Map<Integer, AccessAuth> accessAuths;

    public ThirdParty() {
        this.accessAuths = new HashMap<>();
    }

    public ThirdParty(List<String> texts) {
        this();
        for (String text : texts) {
            addAccessAuth(text);
        }
    }

    public void addAccessAuth(String text) {
        // Parse
        Map.Entry<Integer, String> kv = parseAccessAuth(text);
        if (this.accessAuths.containsKey(kv.getKey())) throw new IllegalArgumentException("accessAuth.authId.dupl");

        // Create group key
        BBS04 bbs04;
        String grpKeyText;
        try {
            bbs04 = new BBS04();
            bbs04.setup();
            grpKeyText = bbs04.getGrpKey().export();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // append access auth
        this.accessAuths.put(kv.getKey(), new AccessAuth(kv.getKey(), bbs04, grpKeyText, kv.getValue()));
    }

    private Map.Entry<Integer, String> parseAccessAuth(String text) {
        List<String> parts = Arrays.asList(text.split(","));

        // Verification
        if (parts.size() != 2) throw new IllegalArgumentException("accessAuth.size");
        if (!parts.get(1).matches("[01]+")) throw new IllegalArgumentException("accessAuth.zones.values");

        // Parse authId
        int authId = Integer.parseInt(parts.get(0));

        // Parse zones
        String zones = parts.get(1);

        return new AbstractMap.SimpleEntry<>(authId, zones);
    }

    public List<String> getAuthKeys() {
        List<String> authKeys = new ArrayList<>();

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<Integer, AccessAuth> kv : accessAuths.entrySet()) {
            sb.append(kv.getKey()).append(",")
                    .append(kv.getValue().grpKeyText).append(",")
                    .append(kv.getValue().zones);
            authKeys.add(sb.toString());
            sb.setLength(0); // clear
        }

        return authKeys;
    }

    private Employee addMember(int id, String name, int authId) {
        // Create member key (and also text)
        MemKey memKey;
        String memKeyText;
        try {
            memKey = createMemKey(authId);
            assert memKey != null;
            memKeyText = memKey.export();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Add identity
        accessAuths.get(authId).addIdentity(id, name);

        // Create employee instance
        return new Employee(id, authId, accessAuths.get(authId).grpKeyText, memKeyText);
    }

    public Employee addMember(String text) {
        List<String> parts = Arrays.asList(text.split(","));
        if (parts.size() != 3) throw new IllegalArgumentException("inputText.size");

        // Parse text into attributes
        int id = Integer.parseInt(parts.get(0));
        String name = parts.get(1);
        int authId = Integer.parseInt(parts.get(2));

        // Verify range of authId
        if (!accessAuths.containsKey(authId))
            throw new IllegalArgumentException("accessAuth.authId");

        return addMember(id, name, authId);
    }

    private MemKey createMemKey(int authId) throws Exception {
        BBS04 bbs04 = accessAuths.get(authId).bbs04;
        MemKey memkey = new MemKey(GS.BBS04_CODE);

        // First protocol step
        long mout1 = bbs04.joinMgr(0, 0);
        if (mout1 == 0) return null;

        // Second protocol step
        bbs04.joinMem(memkey, 1, mout1);

        return memkey;
    }

    public Map.Entry<String, String> open(String sigText) {
        // open signature
        Map.Entry<AccessAuth, Identity> authAndIdentity = openSignature(sigText);
        if (authAndIdentity == null) return null;

        // create open log
        String openLog = createOpenLog(authAndIdentity.getKey(), authAndIdentity.getValue());

        return new AbstractMap.SimpleEntry<>(authAndIdentity.getValue().name, openLog);
    }

    private Map.Entry<AccessAuth, Identity> openSignature(String sigText) {
        // Parse text into signature
        Signature sig;
        try {
            sig = new Signature(GS.BBS04_CODE, sigText);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // try to open signature
        AccessAuth accessAuth = null;
        Identity identity = null;
        for (AccessAuth a : accessAuths.values()) {
            IndexProof indexProof = null;
            try {
                indexProof = a.bbs04.open(sig);
            } catch (Exception e) {
                // do nothing
            }

            if (indexProof != null) {
                accessAuth = a;
                identity = a.getIdentity(indexProof.getIndex());
                if (identity != null) break;
            }
        }
        return (identity != null) ? new AbstractMap.SimpleEntry<>(accessAuth, identity) : null;
    }

    private String createOpenLog(AccessAuth accessAuth, Identity identity) {
        StringBuffer sb = new StringBuffer();
        sb.append(accessAuth.authId).append(",").append(accessAuth.zones).append(",")
                .append(identity.id).append(",").append(identity.name);
        return sb.toString();
    }
}