package com.cozy.kafka.consumer;

import com.cozy.kafka.consumer.factory.TweetGrpcGenerator;
import com.cozy.kafka.infrastructure.grpc.GrpcChannel;
import io.grpc.stub.StreamObserver;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import src.main.proto.twitter.TweetOuterClass;
import src.main.proto.twitter.TweetServiceGrpc;

import java.util.Arrays;
import java.util.Properties;

public class TwitterConsumer implements Consumer, Runnable {

    private Properties properties;

    private final String KAFKA_TOPIC = "twitter_tweets";

    public TwitterConsumer(Properties properties) {
        this.properties = properties;
    }

    @Override
    public KafkaConsumer<String, String> consume() {
        return new KafkaConsumer<>(this.properties);
    }

    @Override
    public void perform() {
        KafkaConsumer<String, String> consumer = consume();
        consumer.subscribe(Arrays.asList(KAFKA_TOPIC));

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(1000);
            for (ConsumerRecord<String, String> record : records) {
                TweetServiceGrpc.TweetServiceStub service = TweetServiceGrpc.newStub(new GrpcChannel().channel());
                TweetOuterClass.Tweet data = TweetGrpcGenerator.instance().generate(record.value()).build();

                StreamObserver<TweetOuterClass.Tweet> response = service.receive(new StreamObserver<TweetOuterClass.TweetResponse>() {
                    @Override
                    public void onNext(TweetOuterClass.TweetResponse tweetResponse) {
                        System.out.println("Response from server:" + "Code: " + tweetResponse.getCode()+ "Message: " + tweetResponse.getMessage());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        System.out.println("Error:" + throwable.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("Completed sending data to Grpc server");
                    }
                });

                response.onNext(data);
                response.onCompleted();
            }
        }
    }

    @Override
    public void run() {
        perform();
    }
}
