package com.Support.SupportTeam.ExceptionHandler;

import com.Support.SupportTeam.Response.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.SignatureException;

@RestControllerAdvice
public class SecurityExceptionsHandler {


    // -------------🔐 Authentication Exceptions-------------------------------

    //User is authenticated but does not have permission
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse("Access Denied: You do not have permission to access this resource", null));
    }

    //No authentication provided (e.g., missing token)
    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ApiResponse> handleInsufficientAuthenticationException(InsufficientAuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse("Authentication Required: Please log in to access this resource", null));
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ApiResponse> handleInternalAuthServiceException(InternalAuthenticationServiceException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse("Authentication Failed: " + ex.getMessage(), null));
    }

    //--------------🔒 Authorization Exceptions------------------------
//    Wrong password entered
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse("Authentication Failed: Incorrect email or password", null));
    }

//    Email/user not found
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse("User not found: " + ex.getMessage(), null));
    }

//    User account has expired
    @ExceptionHandler(AccountExpiredException.class)
    public ResponseEntity<ApiResponse> handleAccountExpiredException(AccountExpiredException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse("Authentication Failed: Your account has expired", null));
    }

//    User account is disabled
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiResponse> handleDisabledException(DisabledException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse("Authentication Failed: Your account is disabled", null));
    }

//    User account is locked
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiResponse> handleLockedException(LockedException ex) {
        return ResponseEntity.status(HttpStatus.LOCKED)
                .body(new ApiResponse("Authentication Failed: Your account is locked", null));
    }

    //------------------- 🔑 JWT Token Issues ---------------------------------------------
    //    JWT token is expired
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponse> handleExpiredJwtException(ExpiredJwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse("Token Expired: Please log in again", null));
    }

//    JWT token is not properly formatted
    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ApiResponse> handleMalformedJwtException(MalformedJwtException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse("Invalid Token: Format is incorrect", null));
    }

//    JWT signature is invalid
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ApiResponse> handleSignatureException(SignatureException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse("Invalid Token: Signature verification failed", null));
    }

//    Generic JWT error
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse> handleJwtException(JwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse("JWT Error: " + ex.getMessage(), null));
    }
}

