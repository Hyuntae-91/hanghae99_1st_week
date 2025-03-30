package io.hhplus.tdd.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "point")
public class PointProperties {
    private long maxId;
    private long maxAmount;

    public long getMaxId() {
        return maxId;
    }

    public long getMaxAmount() {
        return maxAmount;
    }

    public void setMaxId(long maxId) {
        this.maxId = maxId;
    }

    public void setMaxAmount(long maxAmount) {
        this.maxAmount = maxAmount;
    }
}
