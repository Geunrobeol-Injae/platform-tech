package bob.geunrobeol.platform.tech.manager.vo;

import com.ibm.jgroupsig.BBS04;

public record AuthKey(int authId, BBS04 bbs04, String zones) {}
