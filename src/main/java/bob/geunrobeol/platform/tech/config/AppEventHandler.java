package bob.geunrobeol.platform.tech.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import software.amazon.kinesis.coordinator.Scheduler;

@Component
public class AppEventHandler {
    private static final Logger log = LoggerFactory.getLogger(AppEventHandler.class);

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private Scheduler clientScheduler;

    @EventListener
    public void handleApplicationStartedEvent(ApplicationStartedEvent event) {
        log.info("ApplicationStartedEvent");
        taskExecutor.execute(clientScheduler);
    }


}
