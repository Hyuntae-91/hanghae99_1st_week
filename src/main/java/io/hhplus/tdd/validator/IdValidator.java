package io.hhplus.tdd.validator;

import io.hhplus.tdd.config.PointProperties;
import org.springframework.stereotype.Component;

@Component
public class IdValidator {
    private final PointProperties properties;

    public IdValidator(PointProperties properties) {
        this.properties = properties;
    }

    public void validateUserId(long id) {
        if (id < 1 || id > properties.getMaxId()) {
            throw new IllegalArgumentException("Id must be > 1. But was: " + id);
        }
    }
}
