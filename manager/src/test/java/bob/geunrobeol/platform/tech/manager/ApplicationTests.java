package bob.geunrobeol.platform.tech.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static bob.geunrobeol.platform.tech.manager.ManagerConfig.MGR_ACCESS_AUTHS;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.jgroupsig.BBS04;
import com.ibm.jgroupsig.GS;
import com.ibm.jgroupsig.MgrKey;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;

import bob.geunrobeol.platform.tech.manager.dto.AccessAuthS;
import bob.geunrobeol.platform.tech.manager.services.GroupManager;

@SpringBootTest
class ApplicationTests {
	private static final Logger log = LoggerFactory.getLogger(ApplicationTests.class);

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void contextLoads() {
		assertEquals(1+1, 2, "one plus one should be two");
		log.info("Hello World!");
	}

	@Test
	public void exportAndImport() throws Exception {
		BBS04 bbs04 = new BBS04();
		bbs04.setup();

		String mgrKeyText = bbs04.getMgrKey().export();
		Map<String, String> map = new HashMap<>();
		map.put("mgrKeyText", mgrKeyText);

		File file = new File("./keys/sample.txt");
		if (!file.getParentFile().exists()) {
			Files.createDirectory(file.toPath().getParent());
		}
		objectMapper.writeValue(file, map);

		Map<String, String> map2 = objectMapper.readValue(file, new TypeReference<Map<String, String>>() {});
		MgrKey mgrKey = new MgrKey(GS.BBS04_CODE, map2.get("mgrKeyText"));
		bbs04.setMgrKey(mgrKey);
	}
}