package bob.geunrobeol.platform.tech.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.UUID;

import bob.geunrobeol.platform.tech.location.SignalProcessorFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.kinesis.common.ConfigsBuilder;
import software.amazon.kinesis.common.KinesisClientUtil;
import software.amazon.kinesis.coordinator.Scheduler;
import software.amazon.kinesis.retrieval.polling.PollingConfig;

/**
 * 위치정보 관련 Configuration.
 * @see LocationPrivacyConfig
 */
@PropertySource("classpath:location.properties")
@Configuration
public class LocationConfig {
    // TODO inject from external file(s).
    @Value("${kds.regionName}")
    private String AWS_REGION_NAME = "";

    @Value("${kds.streamName}")
    private String AWS_KINESIS_STREAM_NAME = "";

    // WebSocket 관련 Constants
    public static final String WS_SCANNER_TOPIC = "/loc/sc";
    public static final String WS_POSITION_TOPIC = "/loc/pos";
    public static final long WS_POSITION_DELAYS = 2000L;

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
     * AWS Kinesis Data Stream의 Processor를 관리하는 Bean.
     * 해당 Scheduler가 Data를 처리할 Worker 들을 자동으로 관리한다.
     * @see software.amazon.kinesis.processor.ShardRecordProcessorFactory
     * @param signalProcessorFactory Data를 처리할 Worker들을 생성하는 역할
     * @return Scheduler
     */
    @Bean(destroyMethod = "startGracefulShutdown")
    public Scheduler clientScheduler(SignalProcessorFactory signalProcessorFactory) {
        Region region = Region.of(AWS_REGION_NAME);
        KinesisAsyncClient kinesisClient = KinesisClientUtil.createKinesisAsyncClient(KinesisAsyncClient.builder().region(region));
        DynamoDbAsyncClient dynamoClient = DynamoDbAsyncClient.builder().region(region).build();
        CloudWatchAsyncClient cloudWatchClient = CloudWatchAsyncClient.builder().region(region).build();

        PollingConfig pollingConfig = new PollingConfig(AWS_KINESIS_STREAM_NAME, kinesisClient);

        ConfigsBuilder configsBuilder = new ConfigsBuilder(
                AWS_KINESIS_STREAM_NAME, AWS_KINESIS_STREAM_NAME,
                kinesisClient, dynamoClient, cloudWatchClient,
                UUID.randomUUID().toString(),
                signalProcessorFactory);

        return new Scheduler(
                configsBuilder.checkpointConfig(),
                configsBuilder.coordinatorConfig(),
                configsBuilder.leaseManagementConfig(),
                configsBuilder.lifecycleConfig(),
                configsBuilder.metricsConfig(),
                configsBuilder.processorConfig(),
                configsBuilder.retrievalConfig()
                        .retrievalSpecificConfig(pollingConfig)
        );
    }
}