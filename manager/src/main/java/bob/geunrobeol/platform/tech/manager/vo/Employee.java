package bob.geunrobeol.platform.tech.manager.vo;

import com.ibm.jgroupsig.BBS04;
import com.ibm.jgroupsig.GS;
import com.ibm.jgroupsig.GrpKey;
import com.ibm.jgroupsig.MemKey;
import com.ibm.jgroupsig.Signature;

import java.util.Objects;

public class Employee {
    public final int id;
    public final int authId;
    private final BBS04 bbs04;
    private final MemKey memKey;

    public Employee(int id, int authId, String grpKeyText, String memKeyText) {
        this.id = id;
        this.authId = authId;
        try {
            // Parse texts to keys.html
            GrpKey grpKey = new GrpKey(GS.BBS04_CODE, grpKeyText);
            MemKey memKey = new MemKey(GS.BBS04_CODE, memKeyText);

            // Set group key
            this.bbs04 = new BBS04();
            this.bbs04.setGrpKey(grpKey);

            // Set member key
            this.memKey = memKey;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String signBeacon() {
        Signature sig;
        String sigText;

        try {
            sig = bbs04.sign(String.valueOf(authId).getBytes(), memKey);
            sigText = sig.export();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return sigText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return id == employee.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}