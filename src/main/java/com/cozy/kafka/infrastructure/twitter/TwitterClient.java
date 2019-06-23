package com.cozy.kafka.infrastructure.twitter;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TwitterClient {

    private final static int CAPACITY = 1000;

    private Logger logger = LoggerFactory.getLogger(TwitterClient.class.getName());

    private void run() {
        BlockingQueue<String> queue = buildQueue();
        Client client = authentication(queue);
        client.connect();

        KafkaProducer<String, String> producer = createKafkaProducer();


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Stopping application...");
        }));

        logger.info("Starting streaming data");

        while (!client.isDone()) {
            String msg = null;
            try {
                msg = queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
                client.stop();
            }
            if (msg != null) {
                logger.info(msg);
                producer.send(new ProducerRecord<String, String>("twitter_tweets", null, msg), new Callback() {
                    public void onCompletion(RecordMetadata metadata, Exception exception) {
                        if (exception != null) {
                            logger.error("Something bad happened", exception);
                        }
                    }
                });
            }
        }

        logger.info("Ending streaming data");
    }

    private BlockingQueue<String> buildQueue() {
        return new LinkedBlockingQueue<String>(CAPACITY);
    }

    private Client authentication(BlockingQueue<String> queue) {
        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
        List<String> terms = Lists.newArrayList("bitcoin");
        hosebirdEndpoint.trackTerms(terms);
        Authentication hosebirdAuth = new OAuth1("psaUPm78lEpBBfrWcCgq9HqG4", "SeFxa87001YMPSzVQQxf7atrbyvL14lqWW9ZVBo9DNHTpKdV1W", "809787904985731072-pMxSa9o8bnhTNPfe966opCqAhTUfoZN", "Nrx5zTqUKQ5Ju6124xNzJtnUW5l4O2kDsjnH8d6ZxOXiC");
        ClientBuilder builder = new ClientBuilder()
                .name("Hosebird-Client-01")
                .hosts(hosebirdHosts)
                .authentication(hosebirdAuth)
                .endpoint(hosebirdEndpoint)
                .processor(new StringDelimitedProcessor(queue));
        return builder.build();
    }

    public static void main(String[] args) {
        TwitterClient client = new TwitterClient();
        client.run();
    }

    public KafkaProducer<String, String> createKafkaProducer() {
        String serializer = StringSerializer.class.getName();
        String deserializer = StringDeserializer.class.getName();
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "root" + "-consumer");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("auto.offset.reset", "earliest");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", deserializer);
        props.put("value.deserializer", deserializer);
        props.put("key.serializer", serializer);
        props.put("value.serializer", serializer);

        // Create safe Producer
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");

        // High  throughput producer
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        props.put(ProducerConfig.LINGER_MS_CONFIG, "20");
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32 * 1024));

        return new KafkaProducer<>(props);
    }
}
