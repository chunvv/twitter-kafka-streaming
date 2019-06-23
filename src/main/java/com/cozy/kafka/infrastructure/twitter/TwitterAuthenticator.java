package com.cozy.kafka.infrastructure.twitter;

import com.cozy.kafka.infrastructure.Authenticator;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.OAuth1;

import java.util.concurrent.BlockingQueue;

public class TwitterAuthenticator implements Authenticator {

    private BlockingQueue<String> queue;

    private static final String CLIENT_NAME = "Hosebird-Client-01";
    private static final String CONSUMER_KEY = "psaUPm78lEpBBfrWcCgq9HqG4";
    private static final String CONSUMER_SECRET = "SeFxa87001YMPSzVQQxf7atrbyvL14lqWW9ZVBo9DNHTpKdV1W";
    private static final String TOKEN = "809787904985731072-pMxSa9o8bnhTNPfe966opCqAhTUfoZN";
    private static final String TOKEN_SECRET = "Nrx5zTqUKQ5Ju6124xNzJtnUW5l4O2kDsjnH8d6ZxOXiC";


    public TwitterAuthenticator(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    @Override
    public ClientBuilder authenticator() {
        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
        hosebirdEndpoint.trackTerms(new TwitterTopic().topics());

        return new com.twitter.hbc.ClientBuilder()
                .name(CLIENT_NAME)
                .hosts(hosebirdHosts)
                .authentication(oAuth())
                .endpoint(hosebirdEndpoint)
                .processor(new StringDelimitedProcessor(queue));
    }

    private OAuth1 oAuth() {
        return new OAuth1(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
    }
}
