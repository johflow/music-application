package com.model;

/**
 * Represents the result of an authentication attempt
 * Can be used throughout the system for consistent authentication handling
 */
public enum AuthResult {
    /**
     * Authentication was successful
     */
    SUCCESS,
    
    /**
     * The provided username was invalid or not found
     */
    INVALID_USERNAME,
    
    /**
     * The provided password was incorrect
     */
    INVALID_PASSWORD,
    
    /**
     * The provided credentials (username/email) were invalid
     */
    INVALID_CREDENTIALS
} 