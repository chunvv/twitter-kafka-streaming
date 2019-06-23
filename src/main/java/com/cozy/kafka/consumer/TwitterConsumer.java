package com.cozy.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

public class TwitterConsumer implements Consumer, Runnable {

    private Properties properties;

    private final String KAFKA_TOPIC = "twitter_tweets";

    public TwitterConsumer(Properties properties) {
        this.properties = properties;
    }

    @Override
    public KafkaConsumer<String, String> consume() {
        return new KafkaConsumer<>(this.properties);
    }

    @Override
    public void perform() {
        KafkaConsumer<String, String> consumer = consume();
        consumer.subscribe(Arrays.asList(KAFKA_TOPIC));

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(1000);
            for (ConsumerRecord<String, String> record : records) {
                // Handle with data: for testing, just printing to the console
                System.out.printf("%s [%d] offset=%d, key=%s, value=\"%s\"\n",
                        record.topic(), record.partition(),
                        record.offset(), record.key(), record.value());
            }
        }
    }

    @Override
    public void run() {
        perform();
    }
}
