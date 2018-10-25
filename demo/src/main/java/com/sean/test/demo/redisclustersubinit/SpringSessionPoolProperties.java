package com.sean.test.demo.redisclustersubinit;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "spring.sessionpoolconfig")
@Component
public class SpringSessionPoolProperties {
    private int corePoolSize;
    
    private int maxPoolSize;
    
    private int queueCapacity;
    
    private int keepaliveSeconds;

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public int getKeepaliveSeconds() {
        return keepaliveSeconds;
    }

    public void setKeepaliveSeconds(int keepaliveSeconds) {
        this.keepaliveSeconds = keepaliveSeconds;
    }
}
