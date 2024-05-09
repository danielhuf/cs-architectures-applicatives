package if4030.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class CreateTopics {

    public static void main(String[] args) {

        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); 
        try (AdminClient admin = AdminClient.create(config)) {

            List<NewTopic> topics = new ArrayList<>();
            topics.add(new NewTopic("lines-stream", 1, (short) 1)); 
            topics.add(new NewTopic("words-stream", 1, (short) 1));
            topics.add(new NewTopic("tagged-words-stream", 1, (short) 1));
            topics.add(new NewTopic("command-topic", 1, (short) 1));

            KafkaFuture<Void> futures = admin.createTopics(topics).all();

            futures.get();

            System.out.println("Topics created successfully");
        } catch (ExecutionException e) {
            System.err.println("Error creating topics: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted while creating topics");
        }
    }
}
