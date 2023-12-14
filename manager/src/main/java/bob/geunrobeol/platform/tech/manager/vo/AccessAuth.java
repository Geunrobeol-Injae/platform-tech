package bob.geunrobeol.platform.tech.manager.vo;

import com.ibm.jgroupsig.BBS04;

import java.util.ArrayList;
import java.util.List;

public class AccessAuth {
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

    public Identity getIdentity(long index) {
        return identities.stream()
                .filter(i -> i.index() == index)
                .findAny()
                .orElse(null);
    }

    public void addIdentity(int id, long index, String name) {
        identities.add(new Identity(authId, index, id, name));
    }

    public void addIdentity(int id, String name) {
        identities.add(new Identity(authId, identityIndex++, id, name));
    }

    public List<Identity> getIdentities() {
        return new ArrayList<>(identities);
    }
}
