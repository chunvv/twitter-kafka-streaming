package com.cozy.kafka.infrastructure.twitter;

import com.cozy.kafka.infrastructure.Topic;
import com.google.common.collect.Lists;

import java.util.List;

public class TwitterTopic implements Topic {

    @Override
    public List<String> topics() {
        return Lists.newArrayList("bitcoin");
    }
}
