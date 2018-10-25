package com.sean.test.demo.redisclustersubinit;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Cluster;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;

import redis.clients.jedis.JedisPool;

import com.google.common.base.Preconditions;


@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class SubscriberThreadConfig {

    private static final Logger logger = LoggerFactory.getLogger(SubscriberThreadConfig.class);

    public Map<String, JedisPool> jedisPoolMaps = new LinkedHashMap<String, JedisPool>();

    @Autowired
    RedisProperties redisProperties;

    @Bean
    public Map<String, JedisPool> getJedisPoolMaps() {
        logger.info("getJedisPoolMaps begin.");
        Cluster clusterProperties = this.redisProperties.getCluster();
        if (null != clusterProperties) {
            RedisClusterConfiguration config = new RedisClusterConfiguration(clusterProperties.getNodes());
            Preconditions.checkNotNull(config.getClusterNodes(), "redis configuration clusterNodes is null.");
            for (RedisNode address : config.getClusterNodes()) {
                logger.info("address = {}, host = {}, port = {}.", address, address.getHost(), address.getPort());
                JedisPool jedisPool = new JedisPool(address.getHost(), address.getPort());
                jedisPoolMaps.put(address.getHost() + ":" + address.getPort(), jedisPool);
            }
            logger.info("getJedisPoolMaps cluster success.");
            return jedisPoolMaps;
        }

        String host = redisProperties.getHost();
        int port = redisProperties.getPort();
        if (StringUtils.isNotBlank(host) && 0 != port) {
            JedisPool jedisPool = new JedisPool(host, port);
            jedisPoolMaps.put(host + ":" + port, jedisPool);
            logger.info("getJedisPoolMaps stand-alone success.");
        } else {
            logger.error("please config redis host or port correctly.");
        }
        return jedisPoolMaps;
    }
}
