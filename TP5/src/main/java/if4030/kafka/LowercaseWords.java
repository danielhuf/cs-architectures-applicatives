// /*
//  * Licensed to the Apache Software Foundation (ASF) under one or more
//  * contributor license agreements. See the NOTICE file distributed with
//  * this work for additional information regarding copyright ownership.
//  * The ASF licenses this file to You under the Apache License, Version 2.0
//  * (the "License"); you may not use this file except in compliance with
//  * the License. You may obtain a copy of the License at
//  *
//  *    http://www.apache.org/licenses/LICENSE-2.0
//  *
//  * Unless required by applicable law or agreed to in writing, software
//  * distributed under the License is distributed on an "AS IS" BASIS,
//  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  * See the License for the specific language governing permissions and
//  * limitations under the License.
//  */
// //package org.apache.kafka.streams.examples.wordcount;
// package if4030.kafka;

// import org.apache.kafka.clients.consumer.ConsumerConfig;
// import org.apache.kafka.common.serialization.Serdes;
// import org.apache.kafka.streams.KafkaStreams;
// import org.apache.kafka.streams.StreamsBuilder;
// import org.apache.kafka.streams.StreamsConfig;
// import org.apache.kafka.streams.kstream.KStream;
// import org.apache.kafka.streams.kstream.KTable;
// import org.apache.kafka.streams.kstream.Produced;

// import java.io.FileInputStream;
// import java.io.IOException;
// import java.util.Arrays;
// import java.util.Locale;
// import java.util.Properties;
// import java.util.concurrent.CountDownLatch;


// import java.util.regex.Pattern;
// import org.apache.kafka.streams.kstream.ValueMapper;



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
// public final class LowercaseWords {

//     public static final String INPUT_TOPIC = "lines-stream";
//     public static final String OUTPUT_TOPIC = "words-stream";

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
//         props.putIfAbsent(StreamsConfig.APPLICATION_ID_CONFIG, "streams-lowercase");
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

//     static void createWordStream(final StreamsBuilder builder) {
//         final KStream<String, String> source = builder.stream(INPUT_TOPIC);

//         source
//             .flatMapValues(
//                 value -> Arrays.asList(
//                     Pattern
//                         .compile("\\W+", Pattern.UNICODE_CHARACTER_CLASS)
//                         .split(value.toLowerCase(Locale.getDefault()))
//                     )
//             )
//             .filter((key, word) -> !word.isEmpty() && Pattern.matches("[a-z-]+", word))
//             .to(OUTPUT_TOPIC, Produced.with(Serdes.String(), Serdes.String()));
//     }

//     public static void main(final String[] args) throws IOException {
//         final Properties props = getStreamsConfig(args);

//         final StreamsBuilder builder = new StreamsBuilder();
//         createWordStream(builder);
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
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

public class LowercaseWords extends KafkaStreamProcessor {

    public static final String INPUT_TOPIC = "lines-stream";
    public static final String OUTPUT_TOPIC = "words-stream";

    @Override
    protected String getApplicationId() {
        return "streams-lowercase";
    }

    @Override
    protected void createStream(final StreamsBuilder builder) {
        final KStream<String, String> source = builder.stream(INPUT_TOPIC);

        final KStream<String, String> words = source
                .flatMapValues(
                        value -> Arrays.asList(
                                Pattern
                                        .compile("\\W+", Pattern.UNICODE_CHARACTER_CLASS)
                                        .split(value.toLowerCase(Locale.getDefault()))))
                .filter((key, word) -> !word.isEmpty() && Pattern.matches("([a-z]|-)+", word));

        words.peek((key, value) -> System.out.println("key: " + key + " - value: " + value));

        words.to(OUTPUT_TOPIC, Produced.with(Serdes.String(), Serdes.String()));
    }

    public static void main(final String[] args) throws IOException {
        new LowercaseWords().runStream(args);
    }
}
