package com.cozy.kafka.consumer;

import org.apache.kafka.clients.consumer.KafkaConsumer;

public interface Consumer<T> {

    KafkaConsumer<T, T> consume();

    void perform();

}
