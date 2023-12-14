package bob.geunrobeol.platform.tech.manager;

import static bob.geunrobeol.platform.tech.manager.ManagerConfig.KEY_DIR;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bob.geunrobeol.platform.tech.manager.dto.AccessAuthF;
import bob.geunrobeol.platform.tech.manager.dto.EmployeeF;
import bob.geunrobeol.platform.tech.manager.dto.OpenReqS;
import bob.geunrobeol.platform.tech.manager.services.GroupBuilder;
import bob.geunrobeol.platform.tech.manager.services.GroupManager;
import bob.geunrobeol.platform.tech.manager.vo.Identity;

@Controller
public class ManagerController {
    private static final Logger log = LoggerFactory.getLogger(ManagerController.class);

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private GroupBuilder groupBuilder;
    @Autowired
    private GroupManager groupManager;

    private List<Map<String, String>> openLogs = new ArrayList<>();

    private record SetupParam (List<AccessAuthF> accessAuths, List<EmployeeF> employees) {}

    @GetMapping(value = "/setup")
    public String getSetup() {
        return "setup";
    }

    @PostMapping(value = "/setup") @ResponseBody
    public ResponseEntity<Map<String, String>> setup(@RequestBody SetupParam setupParam) {
        Map<String, String> response = new HashMap<>();
        HttpStatus status;

        try {
            Map<String, Object> objects = groupBuilder.build(setupParam.accessAuths, setupParam.employees);
            for (Map.Entry<String, Object> kv : objects.entrySet()) {
                // Write string and file
                response.put(kv.getKey(), objectMapper.writeValueAsString(kv.getValue()));
                objectMapper.writeValue(new File(KEY_DIR + kv.getKey()), kv.getValue());
            }
            status = HttpStatus.OK;
        } catch (Exception e) {
            log.error("", e);
            response = Collections.singletonMap("error", e.getMessage());
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(response, status);
    }

    @PostMapping(value = "/open") @ResponseBody
    public ResponseEntity<Map<String, String>> open(@RequestBody OpenReqS openReq) {
        // Open
        Map.Entry<Identity, Map<String, String>> idAndLog = groupManager.open(openReq.sigText());

        // Identity
        Map<String, String> idMap = new HashMap<>();
        idMap.put("id", String.valueOf(idAndLog.getKey().id()));
        idMap.put("name", idAndLog.getKey().name());

        // Log
        Map<String, String> logMap = idAndLog.getValue();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        logMap.put("timestamp", timestamp);
        logMap.put("adminId", openReq.adminId());
        logMap.put("reason", openReq.reason());
        openLogs.add(logMap);

        return new ResponseEntity<>(idMap, HttpStatus.OK);
    }

    @GetMapping(value = "/logs")
    public String logs(Model model) {
        model.addAttribute("logs", openLogs);
        return "logs";
    }
}