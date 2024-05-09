package if4030.kafka;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public abstract class KafkaStreamProcessor {

    protected Properties getStreamsConfig(final String[] args) throws IOException {
        final Properties props = new Properties();
        props.putIfAbsent(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.putIfAbsent(StreamsConfig.STATESTORE_CACHE_MAX_BYTES_CONFIG, 0);
        props.putIfAbsent(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.putIfAbsent(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.putIfAbsent(StreamsConfig.APPLICATION_ID_CONFIG, getApplicationId());

        return props;
    }

    protected abstract String getApplicationId();

    protected abstract void createStream(final StreamsBuilder builder) throws IOException;

    public void runStream(final String[] args) throws IOException {
        final Properties props = getStreamsConfig(args);

        final StreamsBuilder builder = new StreamsBuilder();
        createStream(builder);
        final KafkaStreams streams = new KafkaStreams(builder.build(), props);

        // Add shutdown hook to catch control-c
        final CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (final Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }
}
