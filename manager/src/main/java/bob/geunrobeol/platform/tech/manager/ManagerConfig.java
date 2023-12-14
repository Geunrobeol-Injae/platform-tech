package bob.geunrobeol.platform.tech.manager;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bob.geunrobeol.platform.tech.manager.dto.AccessAuthS;
import bob.geunrobeol.platform.tech.manager.dto.IdentityS;
import bob.geunrobeol.platform.tech.manager.services.GroupManager;

@Configuration
public class ManagerConfig {
    Logger log = LoggerFactory.getLogger(ManagerConfig.class);

    public static final String KEY_DIR = "keys/";
    public static final String MGR_ACCESS_AUTHS = "mgr-access-auths.txt";
    public static final String MGR_IDENTITIES = "mgr-identities.txt";
    public static final String GRP_AUTH_KEYS = "grp-auth-keys.txt";
    public static final String MEM_KEY_FORMAT = "mem-key-%d.txt";
    public static final String MEM_SIG_FORMAT = "mem-sign-%d.txt";

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public GroupManager groupManager() {
        List<AccessAuthS> accessAuths = readFromFile(KEY_DIR + MGR_ACCESS_AUTHS, AccessAuthS.class);
        List<IdentityS> identities = readFromFile(KEY_DIR + MGR_IDENTITIES, IdentityS.class);
        return new GroupManager(accessAuths, identities);
    }

    private <T> List<T> readFromFile(String filePath, Class<T> valueType) {
        try {
            File file = new File(filePath);
            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, valueType);
            return objectMapper.readValue(file, type);
        } catch (FileNotFoundException e) {
            log.error("{} initialize with empty list.", e.getMessage());
            return new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + filePath, e);
        }
    }
}