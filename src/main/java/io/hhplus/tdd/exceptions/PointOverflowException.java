package io.hhplus.tdd.exceptions;

public class PointOverflowException extends RuntimeException {
    public PointOverflowException(long userId, long current, long added) {
        super("User " + userId + "'s point overflow. Current: " + current + ", Adding: " + added);
    }
}
