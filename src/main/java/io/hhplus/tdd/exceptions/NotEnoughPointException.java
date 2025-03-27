package io.hhplus.tdd.exceptions;

public class NotEnoughPointException extends RuntimeException {
    public NotEnoughPointException(long userId) {
        super("User " + userId + " has not enough points.");
    }
}
