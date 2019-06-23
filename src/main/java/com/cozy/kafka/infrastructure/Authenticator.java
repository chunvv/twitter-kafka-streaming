package com.cozy.kafka.infrastructure;

public interface Authenticator<T> {

    T authenticator();
}
