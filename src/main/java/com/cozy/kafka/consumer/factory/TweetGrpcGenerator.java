package com.cozy.kafka.consumer.factory;


import src.main.proto.twitter.TweetOuterClass;
import src.main.proto.twitter.TweetOuterClass.Tweet.Builder;

public class TweetGrpcGenerator implements GrpcGenerator<String, Builder> {

    private final static TweetGrpcGenerator INSTANCE = new TweetGrpcGenerator();

    private TweetGrpcGenerator() {}

    public static TweetGrpcGenerator instance() {
        return INSTANCE;
    }

    @Override
    public Builder generate(String s) {
        Builder builder = TweetOuterClass.Tweet.newBuilder();
        builder.setCreatedAt("alo 1 2 3 4");
        return builder;
    }
}
