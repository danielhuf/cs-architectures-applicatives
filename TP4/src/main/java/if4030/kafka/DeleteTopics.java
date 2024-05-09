package if4030.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.common.KafkaFuture;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class DeleteTopics {

    public static void main(String[] args) {

        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); 
        try (AdminClient admin = AdminClient.create(config)) {

            String[] topic_list = {
                "lines-stream",
                "words-stream",
                "tagged-words-stream",
                "command-topic"
            };

            DeleteTopicsResult result = admin.deleteTopics(Arrays.asList(topic_list));

            KafkaFuture<Void> future = result.all();
            future.get();

            System.out.println("Topics deleted successfully");
        } catch (ExecutionException e) {
            System.err.println("Error deleting topics: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted while deleting topics");
        }
    }
}
