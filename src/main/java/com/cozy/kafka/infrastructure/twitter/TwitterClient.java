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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TwitterClient {

    private final static int CAPACITY = 1000;

    private Logger logger = LoggerFactory.getLogger(TwitterClient.class.getName());

    private void run() {
        BlockingQueue<String> queue = buildQueue();
        Client client = authentication(queue);
        client.connect();

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
}
