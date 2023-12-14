package bob.geunrobeol.platform.tech.aop;

import bob.geunrobeol.platform.tech.vo.proc.BeaconRecord;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

@Aspect
@Component
public class BeaconRecordAop {

    private Map<String, Map.Entry<String, Integer>> beaconData;

    public BeaconRecordAop() {
        beaconData = Map.of(
            "ble-w", new AbstractMap.SimpleEntry<>("sigText1", 0),
            "ble-y", new AbstractMap.SimpleEntry<>("sigText2", 1),
            "ble-g", new AbstractMap.SimpleEntry<>("sigText3", 2)
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
