package bob.geunrobeol.platform.tech.manager.services;

import com.ibm.jgroupsig.BBS04;
import com.ibm.jgroupsig.GS;
import com.ibm.jgroupsig.GrpKey;
import com.ibm.jgroupsig.MemKey;
import com.ibm.jgroupsig.Signature;

public class Signer {
    public static String sigTextBy(int authId, String grpKeyText, String memKeyText) {
        String sigText;
        try {
            // Parse texts to keys
            GrpKey grpKey = new GrpKey(GS.BBS04_CODE, grpKeyText);
            MemKey memKey = new MemKey(GS.BBS04_CODE, memKeyText);

            // Set group key
            BBS04 bbs04 = new BBS04();
            bbs04.setGrpKey(grpKey);

            // Sign
            Signature sig = bbs04.sign(String.valueOf(authId), memKey);
            sigText = sig.export();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return sigText;
    }
}
