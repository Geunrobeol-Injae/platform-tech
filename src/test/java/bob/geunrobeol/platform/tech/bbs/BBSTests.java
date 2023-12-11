package bob.geunrobeol.platform.tech.bbs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        List<String> authKeyTable = thirdParty.getAuthKeys();
        company = new Company(authKeyTable);
    }
    @Test
    public void decodeSigText() {
        // Sign
        String sigText = employee1.signBeacon();
        log.info("string length: {}", sigText.length());

        // Compress
        String compSigText = compress(sigText);
        log.info("compressed length: {}", compSigText.length());

        // Decode
        byte[] bytes = Base64.getMimeDecoder().decode(sigText);
        log.info("byte length: {}", bytes.length);

        // Compress
        byte[] compBytes = compress(bytes);
        log.info("compressed length: {}", compBytes.length);

        // Hex
        StringBuffer hex = new StringBuffer();
        for (byte b : bytes) hex.append(String.format("%02X", b));
        log.info("Hex: {}", hex);
    }

    public static byte[] compress(byte[] data) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
                gzipOutputStream.write(data);
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception as needed
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] decompress(byte[] compressedData) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedData);
        try {
            try (GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
                 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = gzipInputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }

                return byteArrayOutputStream.toByteArray();
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception as needed
            return new byte[0]; // or throw a custom exception, return an empty array, etc.
        }
    }

    private static String compress(String input) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(input.length());
            try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
                gzipOutputStream.write(input.getBytes("UTF-8"));
            }
            byte[] compressedBytes = byteArrayOutputStream.toByteArray();
            return Base64.getEncoder().encodeToString(compressedBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String decompress(String compressedInput) {
        try {
            byte[] compressedBytes = Base64.getDecoder().decode(compressedInput);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedBytes);
            try (GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream)) {
                byte[] buffer = new byte[1024];
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                int len;
                while ((len = gzipInputStream.read(buffer)) > 0) {
                    byteArrayOutputStream.write(buffer, 0, len);
                }
                return byteArrayOutputStream.toString("UTF-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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