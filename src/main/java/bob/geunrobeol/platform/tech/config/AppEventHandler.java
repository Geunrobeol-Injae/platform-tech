package bob.geunrobeol.platform.tech.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import software.amazon.kinesis.coordinator.Scheduler;

/**
 * Spring Boot의 Event Listener. Application의 Lifecycle 별로 수행할 작업들을 지정할 수 있다.
 */
@Component
public class AppEventHandler {
    private static final Logger log = LoggerFactory.getLogger(AppEventHandler.class);

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private Scheduler clientScheduler;

    /**
     * Application의 모든 Context Bean이 생성되고 난 후 발생하는 Event. 이 때 부터
     * Bean들을 활용할 수 있다.
     * @param event
     */
    @EventListener
    public void handleApplicationStartedEvent(ApplicationStartedEvent event) {
        // AWS Kinesis Client의 Scheduler를 실행시킨다.
        taskExecutor.execute(clientScheduler);
    }
}
