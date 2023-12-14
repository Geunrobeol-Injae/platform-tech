package bob.geunrobeol.platform.tech.manager.services;

import com.ibm.jgroupsig.BBS04;
import com.ibm.jgroupsig.GS;
import com.ibm.jgroupsig.Gml;
import com.ibm.jgroupsig.GrpKey;
import com.ibm.jgroupsig.IndexProof;
import com.ibm.jgroupsig.MgrKey;
import com.ibm.jgroupsig.Signature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bob.geunrobeol.platform.tech.manager.dto.AccessAuthS;
import bob.geunrobeol.platform.tech.manager.dto.IdentityS;
import bob.geunrobeol.platform.tech.manager.vo.AccessAuth;
import bob.geunrobeol.platform.tech.manager.vo.Identity;

public class GroupManager {

    private static final Logger log = LoggerFactory.getLogger(GroupManager.class);


    private final Map<Integer, AccessAuth> accessAuths;

    public GroupManager() {
        this.accessAuths = new HashMap<>();
    }

    public GroupManager(List<AccessAuthS> accessAuths, List<IdentityS> identities) {
        this();

        log.info("accessAuths: {}", accessAuths.size());
        log.info("identities: {}", identities.size());

        for (AccessAuthS a : accessAuths) {
            loadAccessAuth(a);
        }

        for (IdentityS i : identities) {
            loadIdentity(i);
        }
    }

    private void loadAccessAuth(AccessAuthS a) {
        if (this.accessAuths.containsKey(a.authId())) throw new IllegalArgumentException("accessAuth.authId.dupl");
        if (!a.zones().matches("[01]+")) throw new IllegalArgumentException("accessAuth.zones.values");

        // Set Keys
        BBS04 bbs04;
        GrpKey grpKey;
        MgrKey mgrKey;
        Gml gml;

        try {
            bbs04 = new BBS04();
            bbs04.setup();

            grpKey = new GrpKey(GS.BBS04_CODE, a.grpKeyText());
            mgrKey = new MgrKey(GS.BBS04_CODE, a.mgrKeyText());
            gml = new Gml(GS.BBS04_CODE, a.gmlText());

            bbs04.setGrpKey(grpKey);
            bbs04.setMgrKey(mgrKey);
            bbs04.setGml(gml);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // append access auth
        this.accessAuths.put(a.authId(), new AccessAuth(a.authId(), bbs04, a.grpKeyText(), a.zones()));
    }

    private void loadIdentity(IdentityS i) {
        if (!accessAuths.containsKey(i.authId())) throw new IllegalArgumentException("identity.authId.invalid");
        accessAuths.get(i.authId()).addIdentity(i.id(), i.index(), i.name());
    }

    public Map.Entry<Identity, Map<String, String>> open(String sigText) {
        // open signature
        Map.Entry<AccessAuth, Identity> authAndIdentity = openSignature(sigText);
        if (authAndIdentity == null) return null;

        // create open log
        Map<String, String> openLog = createOpenLog(authAndIdentity.getKey(), authAndIdentity.getValue());

        return new AbstractMap.SimpleEntry<>(authAndIdentity.getValue(), openLog);
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

    private Map<String, String> createOpenLog(AccessAuth accessAuth, Identity identity) {
        Map<String, String> map = new HashMap<>();
        map.put("authId", String.valueOf(accessAuth.authId));
        map.put("zones", accessAuth.zones);
        map.put("workerId", String.valueOf(identity.id()));
        map.put("workerName", identity.name());
        return map;
    }
}
