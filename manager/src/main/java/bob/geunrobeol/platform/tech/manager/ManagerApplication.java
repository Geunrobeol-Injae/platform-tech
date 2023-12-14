package bob.geunrobeol.platform.tech.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@SpringBootApplication
public class ManagerApplication {
	public static void main(String[] args) throws IOException {
		// Create Key Directory
		File keyDir = new File(ManagerConfig.KEY_DIR);
		if (!keyDir.exists()) {
			Files.createDirectory(keyDir.toPath());
		}

		// Startup
		SpringApplication.run(ManagerApplication.class, args);
	}
}
