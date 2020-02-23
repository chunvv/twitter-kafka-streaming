package com.cozy.kafka.infrastructure.grpc;

import com.cozy.kafka.infrastructure.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcChannel implements Channel<ManagedChannel> {

    @Override
    public ManagedChannel channel() {
        return  ManagedChannelBuilder.forAddress("localhost", 6565).usePlaintext().build();
    }
}
