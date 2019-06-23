package com.cozy.kafka.infrastructure.twitter;

import com.twitter.hbc.core.Client;

import java.util.concurrent.BlockingQueue;

public class TwitterClient {

    public Client client(BlockingQueue<String> queue) {
        return new TwitterAuthenticator(queue).authenticator().build();
    }
}
