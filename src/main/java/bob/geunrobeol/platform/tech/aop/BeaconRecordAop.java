package bob.geunrobeol.platform.tech.aop;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

import bob.geunrobeol.platform.tech.config.VerifyConfig;
import bob.geunrobeol.platform.tech.vo.proc.BeaconRecord;

@Aspect
@Component
public class BeaconRecordAop {
    private static final Logger log = LoggerFactory.getLogger(BeaconRecordAop.class);

    private ObjectMapper objectMapper;

    private Map<String, Map.Entry<String, Integer>> beaconData;

    public BeaconRecordAop(@Autowired ObjectMapper om) {
        this.objectMapper = om;
        File file1 = new File(VerifyConfig.KEY_DIR + "mem-sign-1.txt");
        File file2 = new File(VerifyConfig.KEY_DIR + "mem-sign-2.txt");

        String sigText1 = "";
        String sigText0 = "";
        try {
            sigText1 = objectMapper.readValue(file1, String.class);
            sigText0 = objectMapper.readValue(file2, String.class);
        } catch (IOException e) {
            log.error("sigText.read.fail", e);
        }

        beaconData = Map.of(
            "ble-w", new AbstractMap.SimpleEntry<>(sigText1, 1),
            "ble-y", new AbstractMap.SimpleEntry<>(sigText0, 0),
            "ble-g", new AbstractMap.SimpleEntry<>(sigText1, 1)
        );
    }

    @Around("execution(* bob.geunrobeol.platform.tech.location.ILocationPreprocessor.popBeaconRecord())")
    public Object injectSigTextAndAuthId(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        if (result instanceof List<?>) {
            List<?> list = (List<?>) result;
            for (Object item : list) {
                if (item instanceof BeaconRecord) {
                    BeaconRecord record = (BeaconRecord) item;
                    Map.Entry<String, Integer> data = beaconData.get(record.getBeaconId());
                    if (data != null) {
                        record.setSigText(data.getKey());
                        record.setAuthId(data.getValue());
                    }
                }
            }
            return list;
        }

        return result;
    }
}