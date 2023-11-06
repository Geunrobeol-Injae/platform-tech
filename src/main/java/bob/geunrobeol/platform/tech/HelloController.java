package bob.geunrobeol.platform.tech;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO test only
@RestController
public class HelloController {
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public String hello() {
        return "{\"message\": \"Hello, World!\"}";
    }
}
