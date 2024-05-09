package if4030.kafka;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CategoryCount extends KafkaStreamProcessor {

    public static final String INPUT_TOPIC = "tagged-words-stream";
    public static final String END_TOPIC = "command-topic";

    private static Map<String, Long> countMap = new HashMap<>();

    private static String categoryToCount;

    protected String getApplicationId() {
        return "streams-categorycount";
    }

    @Override
    protected void createStream(final StreamsBuilder builder) {
        final KStream<String, String> words = builder.stream(INPUT_TOPIC);
        final KStream<String, String> command = builder.stream(END_TOPIC);

        words.peek((key, value) -> countWords(key, value));
        command.peek((key, value) -> processCommand(value));
    }

    static void processCommand(String command) {
        switch (command.toUpperCase()) {
            case "END":
                printTopNWords(20);
                System.out.println("Exiting...");
                System.exit(0);
                break;
            case "DISPLAY 10":
                printTopNWords(10);
                break;
            case "RESET":
                resetMap();
                break;
            default:
                System.out.println("Unknown command: " + command);
                break;
        }
    }

    static void resetMap() {
        countMap.clear();
        System.out.println("Maps reset.");
    }

    static void countWords(String category, String word) {
        if (category == null)
            return;

        if (category.equals(categoryToCount)) {
            countMap.merge(word, 1L, Long::sum);
        }
    }

    public static void printTopNWords(int topN) {
        System.out.println("Top " + topN + " " + categoryToCount + ":");
        countMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(topN)
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
        System.out.println("----------");
    }

    public static void main(final String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Please specify a category to count.");
            System.exit(1);
        }
        categoryToCount = args[0];
        new CategoryCount().runStream(args);
    }
}
