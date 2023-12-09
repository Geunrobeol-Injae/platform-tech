package bob.geunrobeol.platform.tech.location;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import software.amazon.kinesis.processor.ShardRecordProcessor;
import software.amazon.kinesis.processor.ShardRecordProcessorFactory;

/**
 * SignalProcessor를 생성하는 Factory class.
 * @see SignalProcessor
 */
@Component
public class SignalProcessorFactory implements ShardRecordProcessorFactory {
    @Autowired
    ObjectProvider<SignalProcessor> positionProcessorProvider;

    /**
     * {@link ObjectProvider}를 통해 Prototype의 Bean을 생성한다.
     * @return SignalProcessor instance
     */
    @Override
    public ShardRecordProcessor shardRecordProcessor() {
        return positionProcessorProvider.getObject();
    }
}