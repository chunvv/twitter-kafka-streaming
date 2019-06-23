package com.cozy.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;

public interface Producer<T> {

    KafkaProducer<T, T> producer();

    void perform();
}
