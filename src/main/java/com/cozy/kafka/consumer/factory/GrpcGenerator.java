package com.cozy.kafka.consumer.factory;

public interface GrpcGenerator<T, Y> {


    Y generate(T t);
}
