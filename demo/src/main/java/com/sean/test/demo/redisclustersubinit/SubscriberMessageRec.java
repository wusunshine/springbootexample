package com.sean.test.demo.redisclustersubinit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import redis.clients.jedis.JedisPubSub;

@Component
public class SubscriberMessageRec extends JedisPubSub {
    public static final Logger LOGGER = LoggerFactory.getLogger(SubscriberMessageRec.class);

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        super.onPMessage(pattern, channel, message);
        LOGGER.info("pattern = {}, channel = {}, message = {}.", pattern, channel, message);
        if (!message.startsWith("spring:session:sessions:expires")) {
            return;
        }

        if (channel.endsWith(":expired")) {
            int beginIndex = message.lastIndexOf(":") + 1;
            int endIndex = message.length();
            String sessionId = message.substring(beginIndex, endIndex);
            LOGGER.info("expired sessionId = {}", sessionId);
        }
    }
}
