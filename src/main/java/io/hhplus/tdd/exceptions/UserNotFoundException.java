package io.hhplus.tdd.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(long userId) {
        super("User not found: " + userId + " not found.");
    }
}
