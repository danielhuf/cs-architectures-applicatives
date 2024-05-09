// package if4030.kafka;

// import org.apache.kafka.clients.consumer.ConsumerConfig;
// import org.apache.kafka.common.serialization.Serdes;
// import org.apache.kafka.streams.KafkaStreams;
// import org.apache.kafka.streams.KeyValue;
// import org.apache.kafka.streams.StreamsBuilder;
// import org.apache.kafka.streams.StreamsConfig;
// import org.apache.kafka.streams.kstream.KStream;
// import org.apache.kafka.streams.kstream.KTable;
// import org.apache.kafka.streams.kstream.Produced;

// import java.io.FileInputStream;
// import java.io.IOException;
// import java.io.BufferedReader;
// import java.io.FileReader;
// import java.util.Dictionary;
// import java.util.HashMap;
// import java.util.Hashtable;
// import java.util.Arrays;
// import java.util.Locale;
// import java.util.Map;
// import java.util.Properties;
// import java.util.concurrent.CountDownLatch;
// import java.util.regex.Pattern;



// /**
//  * Demonstrates, using the high-level KStream DSL, how to implement the WordCount program
//  * that computes a simple word occurrence histogram from an input text.
//  * <p>
//  * In this example, the input stream reads from a topic named "streams-plaintext-input", where the values of messages
//  * represent lines of text; and the histogram output is written to topic "streams-wordcount-output" where each record
//  * is an updated count of a single word.
//  * <p>
//  * Before running this example you must create the input topic and the output topic (e.g. via
//  * {@code bin/kafka-topics.sh --create ...}), and write some data to the input topic (e.g. via
//  * {@code bin/kafka-console-producer.sh}). Otherwise you won't see any data arriving in the output topic.
//  */
// public final class TagWords {

//     public static final String INPUT_TOPIC = "words-stream";
//     public static final String OUTPUT_TOPIC = "tagged-words-stream";
//     public static final String LEXIQUE_PATH = "Lexique383.tsv";

//     static Properties getStreamsConfig(final String[] args) throws IOException {
//         final Properties props = new Properties();
//         if (args != null && args.length > 0) {
//             try (final FileInputStream fis = new FileInputStream(args[0])) {
//                 props.load(fis);
//             }
//             if (args.length > 1) {
//                 System.out.println("Warning: Some command line arguments were ignored. This demo only accepts an optional configuration file.");
//             }
//         }
//         props.putIfAbsent(StreamsConfig.APPLICATION_ID_CONFIG, "streams-tagger");
//         props.putIfAbsent(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//         props.putIfAbsent(StreamsConfig.STATESTORE_CACHE_MAX_BYTES_CONFIG, 0);
//         props.putIfAbsent(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
//         props.putIfAbsent(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

//         // setting offset reset to earliest so that we can re-run the demo code with the same pre-loaded data
//         // Note: To re-run the demo, you need to use the offset reset tool:
//         // https://cwiki.apache.org/confluence/display/KAFKA/Kafka+Streams+Application+Reset+Tool
//         props.putIfAbsent(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//         return props;
//     }

//     static void createTaggedStream(final StreamsBuilder builder) throws IOException {
//         final KStream<Object, Object> source = builder.stream(INPUT_TOPIC);

//         Dictionary<String,String> categoryMap = new Hashtable<String,String>();
//         Dictionary<String,String> singularInfinitiveMap = new Hashtable<String,String>();
//         BufferedReader TSVReader = new BufferedReader(new FileReader("./Lexique383.tsv"));
        
//         String line = null;
//         while((line = TSVReader.readLine()) != null) {
//             String[] words = line.split("\t");
//             categoryMap.put(words[0], words[3]);
//             singularInfinitiveMap.put(words[0], words[2]);
//         }

//         TSVReader.close();

//         final KStream<String, String> taggedWords = source
//             .map((key,value) -> new KeyValue<>(categoryMap.get(value),singularInfinitiveMap.get(value)));
        
//         taggedWords.peek(( key, value ) -> System.out.println( "key: " + key + " - value: " + value ));    
//         taggedWords.to(OUTPUT_TOPIC, Produced.with(Serdes.String(), Serdes.String()));
//     };
    

//     public static void main(final String[] args) throws IOException {
//         final Properties props = getStreamsConfig(args);

//         final StreamsBuilder builder = new StreamsBuilder();
//         createTaggedStream(builder);
//         final KafkaStreams streams = new KafkaStreams(builder.build(), props);
//         final CountDownLatch latch = new CountDownLatch(1);

//         // attach shutdown handler to catch control-c
//         Runtime.getRuntime().addShutdownHook(new Thread("streams-wordcount-shutdown-hook") {
//             @Override
//             public void run() {
//                 streams.close();
//                 latch.countDown();
//             }
//         });

//         try {
//             streams.start();
//             latch.await();
//         } catch (final Throwable e) {
//             System.exit(1);
//         }
//         System.exit(0);
//     }
// }


// /**
//  * 
//  * kafka-console-consumer.sh --bootstrap-server localhost:9092 \
//         --topic words-stream --from-beginning \
//         --formatter kafka.tools.DefaultMessageFormatter --property print.key=true \
//         --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
//         --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer

//         cat fichier.txt | kafka-console-producer.sh --bootstrap-server localhost:9092 \
//         --topic lines-stream

//         kafka-topics.sh --describe --bootstrap-server localhost:9092 \
//         --topic lines-stream


//         kafka-console-producer.sh --bootstrap-server localhost:9092 \
//         --topic lines-stream
//  */
package if4030.kafka;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

public class TagWords extends KafkaStreamProcessor {

    public static final String INPUT_TOPIC = "words-stream";
    public static final String OUTPUT_TOPIC = "tagged-words-stream";

    protected String getApplicationId() {
        return "streams-tagger";
    }

    @Override
    protected void createStream(final StreamsBuilder builder) throws IOException {
        final KStream<Object, Object> source = builder.stream(INPUT_TOPIC);

        Dictionary<String, String> categoryMap = new Hashtable<String, String>();
        Dictionary<String, String> singularInfinitiveMap = new Hashtable<String, String>();
        BufferedReader TSVReader = new BufferedReader(new FileReader("./Lexique383.tsv"));

        String line = null;
        while ((line = TSVReader.readLine()) != null) {
            String[] words = line.split("\t");
            categoryMap.put(words[0], words[3]);
            singularInfinitiveMap.put(words[0], words[2]);
        }

        TSVReader.close();

        final KStream<String, String> taggedWords = source
                .map((key, value) -> new KeyValue<>(categoryMap.get(value), singularInfinitiveMap.get(value)));

        taggedWords.peek((key, value) -> System.out.println("key: " + key + " - value: " + value));
        taggedWords.to(OUTPUT_TOPIC, Produced.with(Serdes.String(), Serdes.String()));
    }

    public static void main(final String[] args) throws IOException {
        new TagWords().runStream(args);
    }
}
