package bob.geunrobeol.platform.tech.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

@Configuration
public class LocationConfig {
    // TODO inject from external file(s).
    private final String AWS_REGION_NAME = "us-east-1";
    private final String AWS_KINESIS_STREAM_NAME = "scan_ble";
    public static final String TOPIC_POSITION = "/loc/pos";

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