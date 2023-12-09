package bob.geunrobeol.platform.tech.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import software.amazon.kinesis.coordinator.Scheduler;

/**
 * Spring Boot의 Event Listener. Application의 Lifecycle 별로 수행할 작업들을 지정할 수 있다.
 */
@Configuration
public class CommonConfig {
    private static final Logger log = LoggerFactory.getLogger(CommonConfig.class);

    @Autowired
    private Scheduler clientScheduler;

    /**
     * Spring에서 Async 작업을 위한 Thread Pool을 관리하는 Bean.
     * 비동기로 실행할 작업이 있다면 해당 Bean을 통해 execute 하면 된다.
     * @return ThreadPoolTaskExecutor
     */
    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("Task-");

        executor.initialize();
        return executor;
    }

    /**
     * Application의 모든 Context Bean이 생성되고 난 후 발생하는 Event. 이 때 부터
     * Bean들을 활용할 수 있다.
     * @param event
     */
    @EventListener
    public void handleApplicationStartedEvent(ApplicationStartedEvent event) {
        // AWS Kinesis Client의 Scheduler를 실행시킨다.
        taskExecutor().execute(clientScheduler);
    }
}