package com.cozy.kafka.infrastructure.twitter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TwitterQueue {

    private final static int CAPACITY = 1000;

    public BlockingQueue<String> queue() {
        return new LinkedBlockingQueue<>(CAPACITY);
    }
}
