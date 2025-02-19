package com.Support.SupportTeam.CustomExceptions;

public class NoProjectsFoundException extends RuntimeException {
    public NoProjectsFoundException(String message) {
        super(message);
    }

}
