package bob.geunrobeol.platform.tech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Boot가 시작되는 메인 함수. 주로 공통 Configuration 관련 Annotation을 붙여준다.
 */
@EnableScheduling
@SpringBootApplication
public class PlatformTechApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlatformTechApplication.class, args);
	}

}
