package com.cozy.kafka.bootstrap;

import com.cozy.kafka.infrastructure.kafka.KafkaProperty;
import com.cozy.kafka.producer.TwitterrProducer;

public class Bootstrap {

    public static void main(String[] args) {
        new TwitterrProducer(KafkaProperty.properties()).perform();
    }
}
