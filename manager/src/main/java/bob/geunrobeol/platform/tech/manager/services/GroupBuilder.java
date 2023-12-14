package bob.geunrobeol.platform.tech.manager.services;

import static bob.geunrobeol.platform.tech.manager.ManagerConfig.GRP_AUTH_KEYS;
import static bob.geunrobeol.platform.tech.manager.ManagerConfig.MEM_KEY_FORMAT;
import static bob.geunrobeol.platform.tech.manager.ManagerConfig.MEM_SIG_FORMAT;
import static bob.geunrobeol.platform.tech.manager.ManagerConfig.MGR_ACCESS_AUTHS;
import static bob.geunrobeol.platform.tech.manager.ManagerConfig.MGR_IDENTITIES;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.jgroupsig.BBS04;
import com.ibm.jgroupsig.GS;
import com.ibm.jgroupsig.MemKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import bob.geunrobeol.platform.tech.manager.dto.AccessAuthF;
import bob.geunrobeol.platform.tech.manager.dto.AccessAuthS;
import bob.geunrobeol.platform.tech.manager.dto.AuthKeyS;
import bob.geunrobeol.platform.tech.manager.dto.EmployeeF;
import bob.geunrobeol.platform.tech.manager.dto.EmployeeS;
import bob.geunrobeol.platform.tech.manager.dto.IdentityS;
import bob.geunrobeol.platform.tech.manager.vo.AccessAuth;
import bob.geunrobeol.platform.tech.manager.vo.Identity;

@Service
public class GroupBuilder {
    private static final Logger log = LoggerFactory.getLogger(GroupBuilder.class);

    @Autowired
    private ObjectMapper objectMapper;

    public Map<String, Object> build(List<AccessAuthF> as, List<EmployeeF> es) {
        Map<Integer, AccessAuth> accessAuths = parse(as);
        List<EmployeeS> employees = parse(accessAuths, es);

        // Collect into Map
        Map<String, Object> map = new HashMap<>();
        map.put(MGR_ACCESS_AUTHS, accessAuths.values().stream()
                .map(this::export)
                .collect(Collectors.toList()));

        map.put(MGR_IDENTITIES, accessAuths.values().stream()
                .flatMap(a -> a.getIdentities().stream())
                .map(this::export)
                .collect(Collectors.toList()));

        map.put(GRP_AUTH_KEYS, accessAuths.values().stream()
                .map(this::exportGrp)
                .collect(Collectors.toList()));

        for (EmployeeS e : employees) {
            map.put(String.format(MEM_KEY_FORMAT, e.id()), e);

            // Create sign
            String sigText = Signer.sigTextBy(e.authId(), e.grpKeyText(), e.memKeyText());
            map.put(String.format(MEM_SIG_FORMAT, e.id()), sigText);
        }

        return map;
    }

    private Map<Integer, AccessAuth> parse(List<AccessAuthF> as) {
        Map<Integer, AccessAuth> accessAuths = new HashMap<>();

        for (AccessAuthF a : as) {
            if (accessAuths.containsKey(a.authId()))
                throw new IllegalArgumentException("accessAuth.authId.dupl");

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

            accessAuths.put(a.authId(), new AccessAuth(a.authId(), bbs04, grpKeyText, a.zones()));
        }

        return accessAuths;
    }

    private List<EmployeeS> parse(Map<Integer, AccessAuth> accessAuths, List<EmployeeF> es) {
        List<EmployeeS> employees = new ArrayList<>();

        if (es.size() == 0)
            throw new IllegalArgumentException("employee.empty");

        // Create member key (and also text)
        for (EmployeeF e : es) {
            if (employees.stream().anyMatch(employee -> employee.id() == e.id()))
                throw new IllegalArgumentException("employee.id.dupl");

            if (!accessAuths.containsKey(e.authId()))
                throw new IllegalArgumentException("employee.authId.invalid");
            String grpKeyText = accessAuths.get(e.authId()).grpKeyText;

            MemKey memKey;
            String memKeyText;
            try {
                memKey = createMemKey(accessAuths.get(e.authId()).bbs04);
                assert memKey != null;
                memKeyText = memKey.export();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            // Add identity
            accessAuths.get(e.authId()).addIdentity(e.id(), e.name());

            // Create employee instance
            employees.add(new EmployeeS(e.id(), e.authId(), grpKeyText, memKeyText));
        }

        return employees;
    }

    private MemKey createMemKey(BBS04 bbs04) throws Exception {
        MemKey memkey = new MemKey(GS.BBS04_CODE);

        // First protocol step
        long mout1 = bbs04.joinMgr(0, 0);
        if (mout1 == 0) return null;

        // Second protocol step
        bbs04.joinMem(memkey, 1, mout1);

        return memkey;
    }

    private AccessAuthS export(AccessAuth a) {
        String mgrKeyText;
        String gmlText;
        try {
            mgrKeyText = a.bbs04.getMgrKey().export();
            gmlText = a.bbs04.getGml().export();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new AccessAuthS(a.authId, a.zones, mgrKeyText, a.grpKeyText, gmlText);
    }

    private IdentityS export(Identity i) {
        return new IdentityS(i.authId(), i.index(), i.id(), i.name());
    }

    private AuthKeyS exportGrp(AccessAuth a) {
        return new AuthKeyS(a.authId, a.grpKeyText, a.zones);
    }
}
