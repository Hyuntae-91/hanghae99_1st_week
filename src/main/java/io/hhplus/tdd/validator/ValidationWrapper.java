package io.hhplus.tdd.validator;

import org.springframework.stereotype.Component;

@Component
public class ValidationWrapper {
    private final IdValidator idValidator;
    private final PointValidator pointValidator;

    public ValidationWrapper(IdValidator idValidator, PointValidator pointValidator) {
        this.idValidator = idValidator;
        this.pointValidator = pointValidator;
    }

    public void validateId(long id) {
        idValidator.validateUserId(id);
    }

    public void validateAmount(long amount) {
        pointValidator.validateAmount(amount);
    }

    public void validateUsableBalance(long userPoint, long amount) {
        pointValidator.validateUsableBalance(userPoint, amount);
    }
}
