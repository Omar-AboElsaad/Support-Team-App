package com.Support.SupportTeam.CustomExceptions;

public class UserDeletedException extends RuntimeException {
    public UserDeletedException(String message) {
        super(message);
    }
}
