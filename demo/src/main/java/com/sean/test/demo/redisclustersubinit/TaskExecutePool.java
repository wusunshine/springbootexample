package com.sean.test.demo.redisclustersubinit;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableConfigurationProperties(SpringSessionPoolProperties.class)
public class TaskExecutePool {

    private static final String THREADNAME_PREFIX = "MySpringSessionExecutor-";
    
    private static final String THREADNAME_PREFIX2 = "SubscriberSessionExecutor-";

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskExecutePool.class);

    @Bean("springSessionRedisTaskExecutor")
    @Autowired
    public Executor mySpringSessionTaskAsyncPool(SpringSessionPoolProperties springSessionPoolProperties) {

        LOGGER.info(
                "mySpringSessionTaskAsyncPool begin. corePoolSize = {}, maxPoolSize = {}, queueCapacity = {}, keepaliveSeconds = {}.",
                springSessionPoolProperties.getCorePoolSize(), springSessionPoolProperties.getMaxPoolSize(),
                springSessionPoolProperties.getQueueCapacity(), springSessionPoolProperties.getKeepaliveSeconds());

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程池大小
        executor.setCorePoolSize(springSessionPoolProperties.getCorePoolSize());
        // 最大线程数
        executor.setMaxPoolSize(springSessionPoolProperties.getMaxPoolSize());
        // 队列容量
        executor.setQueueCapacity(springSessionPoolProperties.getQueueCapacity());
        // 活跃时间
        executor.setKeepAliveSeconds(springSessionPoolProperties.getKeepaliveSeconds());
        // 线程名字前缀
        executor.setThreadNamePrefix(THREADNAME_PREFIX);

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();

        LOGGER.info("mySpringSessionTaskAsyncPool end.");
        return executor;
    }
    
    @Bean("subscriberRedisTaskExecutor")
    @Autowired
    public ThreadPoolTaskExecutor subscriberThreadExecutor(SpringSessionPoolProperties springSessionPoolProperties) {
        LOGGER.info(
                "subscriberThreadExecutor begin. corePoolSize = {}, maxPoolSize = {}, queueCapacity = {}, keepaliveSeconds = {}.",
                springSessionPoolProperties.getCorePoolSize(), springSessionPoolProperties.getMaxPoolSize(),
                springSessionPoolProperties.getQueueCapacity(), springSessionPoolProperties.getKeepaliveSeconds());

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程池大小
        executor.setCorePoolSize(springSessionPoolProperties.getCorePoolSize());
        // 最大线程数
        executor.setMaxPoolSize(springSessionPoolProperties.getMaxPoolSize());
        // 队列容量
        executor.setQueueCapacity(springSessionPoolProperties.getQueueCapacity());
        // 活跃时间
        executor.setKeepAliveSeconds(springSessionPoolProperties.getKeepaliveSeconds());
        // 线程名字前缀
        executor.setThreadNamePrefix(THREADNAME_PREFIX2);

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        LOGGER.info("subscriberThreadExecutor end.");
        return executor;
    }
}
