package bob.geunrobeol.platform.tech;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {
	private static final Logger log = LoggerFactory.getLogger(ApplicationTests.class);

	@Test
	public void contextLoads() {
		assertEquals(1+1, 2, "one plus one should be two");
		log.info("Hello World!");
	}
}