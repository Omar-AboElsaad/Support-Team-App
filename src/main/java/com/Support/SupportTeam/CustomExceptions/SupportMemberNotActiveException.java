package com.Support.SupportTeam.CustomExceptions;

public class SupportMemberNotActiveException extends RuntimeException {
    public SupportMemberNotActiveException(String message) {
        super(message);
    }
}
