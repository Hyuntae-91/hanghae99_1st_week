package io.hhplus.tdd.validator;

import io.hhplus.tdd.config.PointProperties;
import io.hhplus.tdd.exceptions.NotEnoughPointException;
import org.springframework.stereotype.Component;

@Component
public class PointValidator {
    private final PointProperties properties;

    public PointValidator(PointProperties properties) {
        this.properties = properties;
    }

    public void validateAmount(long amount) {
        if (amount < 0 || amount > properties.getMaxAmount()) {
            throw new IllegalArgumentException("Invalid amount: " + amount);
        }
    }

    public void validateUsableBalance(long userPoint, long amount) {
        if (userPoint - amount < 0) {
            throw new NotEnoughPointException(amount);
        }
    }
}
