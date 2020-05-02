package com.cozy.kafka.consumer.factory;


import org.json.JSONObject;
import src.main.proto.twitter.UserOuterClass;
import src.main.proto.twitter.UserOuterClass.User.Builder;

public class UserGrpcGenerator implements GrpcGenerator<String, Builder> {

    private final static UserGrpcGenerator INSTANCE = new UserGrpcGenerator();

    private UserGrpcGenerator() {
    }

    static UserGrpcGenerator instance() {
        return INSTANCE;
    }

    @Override
    public Builder generate(String s) {
        Builder builder = UserOuterClass.User.newBuilder();
        JSONObject json = new JSONObject(s);
        builder.setId(json.getLong("id"));
        builder.setIdStr(json.getString("id_str"));
        builder.setName(json.getString("name"));
        builder.setScreenName(json.getString("screen_name"));
        // builder.setLocation(json.getString("location"));
        // builder.setUrl(json.getString("url"));
        // builder.setDescription(json.getString("description"));
        // builder.setTranslatorType(json.getString("translator_type"));
        // TODO(Trung): Udate more
        return builder;
    }
}
