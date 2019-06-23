package com.cozy.kafka.infrastructure.twitter;

import com.cozy.kafka.infrastructure.Authenticator;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.OAuth1;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

public class TwitterAuthenticator implements Authenticator {

    private BlockingQueue<String> queue;

    private static final String CLIENT_NAME = "Hosebird-Client-01";
    private String CONSUMER_KEY;
    private String CONSUMER_SECRET;
    private String TOKEN;
    private String TOKEN_SECRET;


    public TwitterAuthenticator(BlockingQueue<String> queue) {
        this.queue = queue;
        setProperties();
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

    private void setProperties() {
        Properties properties = new Properties();
        try {
            properties.load(TwitterAuthenticator.class.getResourceAsStream("/development.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        CONSUMER_KEY = properties.getProperty("CONSUMER_KEY");
        CONSUMER_SECRET = properties.getProperty("CONSUMER_SECRET");
        TOKEN = properties.getProperty("TOKEN");
        TOKEN_SECRET = properties.getProperty("TOKEN_SECRET");
    }
}
