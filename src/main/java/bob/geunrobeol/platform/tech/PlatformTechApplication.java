package bob.geunrobeol.platform.tech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PlatformTechApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlatformTechApplication.class, args);
	}

}
