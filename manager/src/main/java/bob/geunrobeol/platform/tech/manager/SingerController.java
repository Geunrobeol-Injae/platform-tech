package bob.geunrobeol.platform.tech.manager;

import com.ibm.jgroupsig.BBS04;
import com.ibm.jgroupsig.GS;
import com.ibm.jgroupsig.GrpKey;
import com.ibm.jgroupsig.MemKey;
import com.ibm.jgroupsig.Signature;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import bob.geunrobeol.platform.tech.manager.dto.EmployeeS;

@Controller
public class SingerController {
    @GetMapping(value = "sign")
    public String sign() {
        return "sign";
    }

    @PostMapping(value = "sign") @ResponseBody
    public ResponseEntity<String> sign(@RequestBody EmployeeS employee) {
        String sigText = getSigText(employee);
        return new ResponseEntity<>(sigText, HttpStatus.OK);
    }

    private String getSigText(EmployeeS employee) {
        String sigText;
        try {
            // Parse texts to keys
            GrpKey grpKey = new GrpKey(GS.BBS04_CODE, employee.grpKeyText());
            MemKey memKey = new MemKey(GS.BBS04_CODE, employee.memKeyText());

            // Set group key
            BBS04 bbs04 = new BBS04();
            bbs04.setGrpKey(grpKey);

            // Sign
            Signature sig = bbs04.sign(String.valueOf(employee.authId()), memKey);
            sigText = sig.export();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return sigText;
    }
}
