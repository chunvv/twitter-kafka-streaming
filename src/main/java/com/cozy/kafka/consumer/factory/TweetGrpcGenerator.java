package com.cozy.kafka.consumer.factory;


import org.json.JSONObject;
import src.main.proto.twitter.TweetOuterClass;
import src.main.proto.twitter.TweetOuterClass.Tweet.Builder;

public class TweetGrpcGenerator implements GrpcGenerator<String, Builder> {

    private final static TweetGrpcGenerator INSTANCE = new TweetGrpcGenerator();

    private TweetGrpcGenerator() {
    }

    public static TweetGrpcGenerator instance() {
        return INSTANCE;
    }

    @Override
    public Builder generate(String s) {
        Builder builder = TweetOuterClass.Tweet.newBuilder();
        JSONObject json = new JSONObject(s);
        builder.setCreatedAt(json.getString("created_at"));
        builder.setId(json.getLong("id"));
        builder.setIdStr(json.getString("id_str"));
        builder.setText(json.getString("text"));
        builder.setSource(json.getString("source"));
        builder.setTruncated(json.getBoolean("truncated"));

        builder.setUser(UserGrpcGenerator.instance().generate(json.getJSONObject("user").toString()));

        return builder;
    }
}
