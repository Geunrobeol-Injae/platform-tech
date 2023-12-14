package bob.geunrobeol.platform.tech.verify;

import com.ibm.jgroupsig.BBS04;

public class AuthKey {
    public final int authId;
    public final BBS04 bbs04;
    public final String zones;

    public AuthKey(int authId, BBS04 bbs04, String zones) {
        this.authId = authId;
        this.bbs04 = bbs04;
        this.zones = zones;
    }
}