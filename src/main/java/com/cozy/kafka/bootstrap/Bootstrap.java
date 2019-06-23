package com.cozy.kafka.bootstrap;

import com.cozy.kafka.consumer.TwitterConsumer;
import com.cozy.kafka.infrastructure.kafka.KafkaProperty;
import com.cozy.kafka.producer.TwitterProducer;

public class Bootstrap {

    public static void main(String[] args) {
        new Thread(new TwitterProducer(KafkaProperty.properties())).start();
        new Thread(new TwitterConsumer(KafkaProperty.properties())).start();
    }
}
