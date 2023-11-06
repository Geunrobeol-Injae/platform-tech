package bob.geunrobeol.platform.tech.location;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import software.amazon.kinesis.processor.ShardRecordProcessor;
import software.amazon.kinesis.processor.ShardRecordProcessorFactory;

@Component
public class SignalProcessorFactory implements ShardRecordProcessorFactory {
    @Autowired
    ObjectProvider<SignalProcessor> positionProcessorProvider;

    @Override
    public ShardRecordProcessor shardRecordProcessor() {
        return positionProcessorProvider.getObject();
    }
}
