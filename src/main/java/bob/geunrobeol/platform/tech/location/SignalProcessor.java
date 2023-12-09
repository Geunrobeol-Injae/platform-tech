package bob.geunrobeol.platform.tech.location;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import bob.geunrobeol.platform.tech.vo.raw.ScannerRecord;
import software.amazon.kinesis.exceptions.InvalidStateException;
import software.amazon.kinesis.exceptions.ShutdownException;
import software.amazon.kinesis.lifecycle.events.InitializationInput;
import software.amazon.kinesis.lifecycle.events.LeaseLostInput;
import software.amazon.kinesis.lifecycle.events.ProcessRecordsInput;
import software.amazon.kinesis.lifecycle.events.ShardEndedInput;
import software.amazon.kinesis.lifecycle.events.ShutdownRequestedInput;
import software.amazon.kinesis.processor.ShardRecordProcessor;
import software.amazon.kinesis.retrieval.KinesisClientRecord;

/**
 * AWS Kinesis에서 들어오는 데이터를 처리하는 class.
 * 대부분의 method는 기본 예제에서 가져왔다.
 * @see SignalProcessorFactory
 */
@Component
@Scope("prototype")
public class SignalProcessor implements ShardRecordProcessor {
    private static final Logger log = LoggerFactory.getLogger(SignalProcessor.class);
    private static final String SHARD_ID_MDC_KEY = "ShardId";
    private String shardId;

    @Autowired
    private LocationPublisher locationPublisher;

    @Autowired
    private ILocationPreprocessor locationPreprocessor;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void initialize(InitializationInput initializationInput) {
        shardId = initializationInput.shardId();
        MDC.put(SHARD_ID_MDC_KEY, shardId);
        try {
            log.info("Initializing @ Sequence: {}", initializationInput.extendedSequenceNumber());
        } finally {
            MDC.remove(SHARD_ID_MDC_KEY);
        }
    }

    /**
     * AWS Kinesis로부터 들어온 데이터를 처리한다.
     * @param processRecordsInput Provides the records to be processed as well as information and capabilities related
     *        to them (eg checkpointing).
     */
    @Override
    public void processRecords(ProcessRecordsInput processRecordsInput) {
        MDC.put(SHARD_ID_MDC_KEY, shardId);
        try {
            log.info("Processing {} record(s)", processRecordsInput.records().size());

            for (KinesisClientRecord r : processRecordsInput.records()) {
                // Read bytes
                byte[] bytes = new byte[r.data().remaining()];
                r.data().get(bytes);

                // Convert into Class
                ScannerRecord record = objectMapper.readValue(bytes, ScannerRecord.class);

                // Publish and push into preprocessor
                locationPublisher.publishScanner(record);
                locationPreprocessor.pushScanRecord(record);
            }

        } catch (Throwable t) {
            log.error("Caught throwable while processing records. Aborting.");
            Runtime.getRuntime().halt(1);
        } finally {
            MDC.remove(SHARD_ID_MDC_KEY);
        }
    }

    @Override
    public void leaseLost(LeaseLostInput leaseLostInput) {
        MDC.put(SHARD_ID_MDC_KEY, shardId);
        try {
            log.info("Lost lease, so terminating.");
        } finally {
            MDC.remove(SHARD_ID_MDC_KEY);
        }
    }

    @Override
    public void shardEnded(ShardEndedInput shardEndedInput) {
        MDC.put(SHARD_ID_MDC_KEY, shardId);
        try {
            log.info("Reached shard end checkpointing.");
            shardEndedInput.checkpointer().checkpoint();
        } catch (ShutdownException | InvalidStateException e) {
            log.error("Exception while checkpointing at shard end. Giving up.", e);
        } finally {
            MDC.remove(SHARD_ID_MDC_KEY);
        }
    }

    @Override
    public void shutdownRequested(ShutdownRequestedInput shutdownRequestedInput) {
        MDC.put(SHARD_ID_MDC_KEY, shardId);
        try {
            log.info("Scheduler is shutting down, checkpointing.");
            shutdownRequestedInput.checkpointer().checkpoint();
        } catch (ShutdownException | InvalidStateException e) {
            log.error("Exception while checkpointing at requested shutdown. Giving up.", e);
        } finally {
            MDC.remove(SHARD_ID_MDC_KEY);
        }
    }
}
