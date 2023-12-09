package bob.geunrobeol.platform.tech.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
@Configuration
public class LocationConfig {
    private final String AWS_REGION_NAME = "us-east-1";
    private final String AWS_KINESIS_STREAM_NAME = "scan_ble";

    // Kalman Filter 관련 Constants
    public static final double KALMAN_PROCESS_NOISE = 1.0;
    public static final double KALMAN_MEASUREMENT_NOISE = 20.0;

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