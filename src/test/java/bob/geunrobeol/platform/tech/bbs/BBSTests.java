package bob.geunrobeol.platform.tech.bbs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bob.geunrobeol.platform.tech.location.Company;
import bob.geunrobeol.platform.tech.location.Employee;
import bob.geunrobeol.platform.tech.location.ThirdParty;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class BBSTests {

    private static final Logger log = LoggerFactory.getLogger(BBSTests.class);

    private static final List<String> accessAuthTable;
    static {
        accessAuthTable = new ArrayList<>();
        accessAuthTable.add("0,11");
        accessAuthTable.add("1,10");
    }

    private static final List<String> employeeTable;
    static {
        employeeTable = new ArrayList<>();
        employeeTable.add("1,Alice,1");
        employeeTable.add("2,Bob,0");
    }

    private Set<Employee> employees;
    private Employee employee1;
    private Company company;
    private ThirdParty thirdParty;

    @BeforeEach
    public void setup() {
        // Setup third party
        thirdParty = new ThirdParty(accessAuthTable);

        // Setup employees
        employees = new HashSet<>();
        for (String text : employeeTable) {
            Employee e = thirdParty.addMember(text);
            if (employee1 == null) employee1 = e;
            employees.add(e);
        }

        // Setup company
        //List<String> authKeyTable = thirdParty.getAuthKeys();
        //company = new Company(authKeyTable);
    }

    @Test
    public void verification() {
        // sign
        String sigText = employee1.signBeacon();

        // verify
        boolean verified = company.verifyAccessAuth(sigText, employee1.authId);
        log.info("verified: {}", verified);
    }

    @Test
    public void open() {
        // sign
        String sigText = employee1.signBeacon();
        log.info("sigText:\n{}", sigText);

        // open
        Map.Entry<String, String> kv = thirdParty.open(sigText);
        if (kv == null) throw new RuntimeException("open.fail");

        employee1.appendOpenLog(kv.getValue());
        company.appendIdentity(sigText, kv.getKey());

        log.info("open identity: {}", kv.getKey());
        log.info("open log: {}", kv.getValue());
    }
}