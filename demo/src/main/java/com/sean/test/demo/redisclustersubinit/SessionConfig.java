package com.sean.test.demo.redisclustersubinit;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.session.data.redis.RedisFlushMode;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.events.SessionCreatedEvent;
import org.springframework.session.events.SessionExpiredEvent;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


@Configuration
@EnableRedisHttpSession(redisFlushMode = RedisFlushMode.IMMEDIATE)
public class SessionConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionConfig.class);

    private static final String CHANNEL_NAME = "__keyevent@*:expired";

    private Map<String, Boolean> subscriberFlags = new LinkedHashMap<String, Boolean>();

    @Value("${server.session.timeout}")
    private Integer maxInactiveIntervalInSeconds;

    @Autowired
    private RedisOperationsSessionRepository sessionRepository;

    @Autowired
    ThreadPoolTaskExecutor subscriberRedisTaskExecutor;

    @Autowired
    SubscriberMessageRec subscriberMessageRec;

    @Autowired
    Map<String, JedisPool> jedisPoolMaps;

    @PostConstruct
    private void afterPropertiesSet() {
        sessionRepository.setDefaultMaxInactiveInterval(maxInactiveIntervalInSeconds);
        Set<String> addresses = jedisPoolMaps.keySet();
        for (String address : addresses) {
            LOGGER.info("address for jedisPool = {}.", address);
            subscriberFlags.put(address, true);
        }
    }

    /**
     * Redis内session过期事件监听
     */
    @EventListener
    public void onSessionExpired(SessionExpiredEvent expiredEvent) {
        LOGGER.info("onSessionExpired begin.");
        LOGGER.info("onSessionExpired end.");
    }


    /**
     * Redis内session创建事件监听
     */
    @EventListener
    public void onSessionCreated(SessionCreatedEvent createdEvent) {
        LOGGER.info("onSessionCreated begin. Created sessionId = {},maxInactiveIntervalInSeconds = {}.",
                createdEvent.getSessionId(), maxInactiveIntervalInSeconds);

        Set<String> flagAddresses = subscriberFlags.keySet();
        for (String flagAddress : flagAddresses) {
            if (subscriberFlags.get(flagAddress)) {
                LOGGER.info("Address = {} is subscribing.", flagAddress);
                subscriberRedisTaskExecutor.execute(new Runnable() {
                    public void run() {
                        Jedis subscriberJedis = jedisPoolMaps.get(flagAddress).getResource();
                        try {
                            LOGGER.info("Subscribing expired event. the thread will be blocked.");
                            subscriberFlags.put(flagAddress, false);
                            subscriberJedis.psubscribe(subscriberMessageRec, CHANNEL_NAME);
                            LOGGER.info("Subscription ended.");
                        } catch (Exception e) {
                            LOGGER.error("Subscribing failed.", e);
                            subscriberFlags.put(flagAddress, true);
                        } finally {
                            subscriberMessageRec.punsubscribe();
                            jedisPoolMaps.get(flagAddress).returnResourceObject(subscriberJedis);
                        }
                    }
                });
            } else {
                LOGGER.info("SUBSCRIBE_FLAG is false, need not subscribe again. address = {}.", flagAddress);
            }
        }
        LOGGER.info("onSessionCreated end.");
    }
}