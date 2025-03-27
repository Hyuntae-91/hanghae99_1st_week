package io.hhplus.tdd.exceptions;

public class InvalidUserIdException extends RuntimeException {
    public InvalidUserIdException(long amount) {
        super("Invalid amount: " + amount);
    }
}
