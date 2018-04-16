package tutorial.schema.async;

import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tutorial.schema.Tweet;
import tutorial.schema.TweetSchema;

import java.util.stream.IntStream;

public class AsyncSchemaProducerTutorial {
    private static final Logger log = LoggerFactory.getLogger(AsyncSchemaProducerTutorial.class);
    private static final String SERVICE_URL = "pulsar://localhost:6650";
    private static final String TOPIC_NAME = "tutorial-topic";
    private static final int NUM_TO_PRODUCE = 1000;

    public static void main(String[] args) {
        try {
            PulsarClient client = PulsarClient.builder()
                    .serviceUrl(SERVICE_URL)
                    .build();

            log.info("Created a client for the Pulsar cluster running at {}", SERVICE_URL);

            client.newProducer(new TweetSchema())
                    .topic(TOPIC_NAME)
                    .createAsync()
                    .thenAccept((Producer<Tweet> tweetProducer) -> {
                        log.info("Producer created asynchronously for the topic {}", TOPIC_NAME);

                        IntStream.range(1, NUM_TO_PRODUCE + 1).forEach(i -> {
                            Tweet tweet = new Tweet("elonmusk123", String.format("This is tweet %d", i));
                            tweetProducer.sendAsync(tweet)
                                    .thenAccept((MessageId msgId) -> {
                                        log.info("Successfully sent tweet message with an ID of {}", msgId);
                                    })
                                    .exceptionally(ex -> {
                                        log.error(ex.toString());
                                        return null;
                                    });
                        });
                    })
                    .exceptionally(ex -> {
                        log.error(ex.toString());
                        return null;
                    });
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
    }
}
