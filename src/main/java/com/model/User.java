package com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a user in the music application
 */
public class User {
    private final UUID id;
    private String email;
    private String username;
    private String password;
    private ArrayList<Song> favoriteSongs;
    private ArrayList<User> followedUsers;
    private ThemeColor themeColor;

    /**
     * Constructor for a new User
     * 
     * @param email User's email
     * @param username User's username
     * @param password User's password
     * @throws IllegalArgumentException if email or password don't meet requirements
     */
    public User(String email, String username, String password) throws IllegalArgumentException {
        this.id = UUID.randomUUID();
        
        isEmailValid(email);
        isPasswordValid(password);
        
        this.email = email;
        this.username = username;
        this.password = password;
        this.favoriteSongs = new ArrayList<>();
        this.followedUsers = new ArrayList<>();
        this.themeColor = ThemeColor.getDefault();
    }

    /**
     * Constructor with all parameters for User
     * 
     * @param id User's UUID
     * @param email User's email
     * @param username User's username
     * @param password User's password
     * @param themeColor User's theme color
     * @throws IllegalArgumentException if email or password don't meet requirements
     */
    public User(UUID id, String email, String username, String password, ThemeColor themeColor) throws IllegalArgumentException {
        this.id = id;
        
        isEmailValid(email);
        isPasswordValid(password);
        
        this.email = email;
        this.username = username;
        this.password = password;
        this.favoriteSongs = new ArrayList<>();
        this.followedUsers = new ArrayList<>();
        this.themeColor = themeColor;
    }

    public User(UUID id, String email, String username, String password) throws IllegalArgumentException {
        this.id = id;
        
        isEmailValid(email);
        isPasswordValid(password);
        
        this.email = email;
        this.username = username;
        this.password = password;
        this.themeColor = ThemeColor.getDefault();
        this.favoriteSongs = new ArrayList<>();
        this.followedUsers = new ArrayList<>();
    }

    /**
     * Helper to confirm correct email input.
     * 
     * @param email input email
     * @return True for valid, false for invalid
     * @throws IllegalArgumentException if email doesn't meet format requirements
     */
    public static boolean isEmailValid(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        
        // Regular expression to match valid email formats
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                            "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    
        // Compile the regex
        Pattern p = Pattern.compile(emailRegex);
      
        // Check if email matches the pattern
        if (!p.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        return true;
    }

    /**
     * Helper to confirm password meets regex requirements.
     * 
     * @param password input password
     * @return List of validation errors, empty if password is valid
     */
    public static List<String> getPasswordValidationErrors(String password) {
        List<String> errors = new ArrayList<>();
        
        if (password == null || password.length() < 8) {
            errors.add("Password must be at least 8 characters long");
        }
        
        // Check for at least one digit
        if (password == null || !Pattern.compile(".*\\d.*").matcher(password).matches()) {
            errors.add("Password must contain at least one digit");
        }
        
        // Check for at least one uppercase letter
        if (password == null || !Pattern.compile(".*[A-Z].*").matcher(password).matches()) {
            errors.add("Password must contain at least one uppercase letter");
        }
        
        // Check for at least one lowercase letter
        if (password == null || !Pattern.compile(".*[a-z].*").matcher(password).matches()) {
            errors.add("Password must contain at least one lowercase letter");
        }
        
        // Check for at least one special character
        if (password == null || !Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*").matcher(password).matches()) {
            errors.add("Password must contain at least one special character");
        }
        
        return errors;
    }

    /**
     * Helper to confirm password meets security requirements.
     * 
     * @param password input password
     * @return True for valid, false for invalid
     * @throws IllegalArgumentException if password doesn't meet requirements, with detailed error message
     */
    public static boolean isPasswordValid(String password) throws IllegalArgumentException {
        List<String> errors = getPasswordValidationErrors(password);
        
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(". ", errors));
        }
        
        return true;
    }

  /**
     * Gets the user's ID
     *
     * @return The user's UUID
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets the user's email
     * 
     * @return The user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email
     * 
     * @param email The new email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's username
     * 
     * @return The user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the user's username
     * 
     * @param username The new username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the user's password
     *
     * @return The user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password
     * 
     * @param password The new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the user's favorite songs
     * 
     * @return ArrayList of favorite songs
     */
    public ArrayList<Song> getFavoriteSongs() {
        return favoriteSongs;
    }

    /**
     * Adds a song to the user's favorites
     * 
     * @param song The song to add
     */
    public void addFavoriteSong(Song song) {
        if (!favoriteSongs.contains(song)) {
            favoriteSongs.add(song);
        }
    }

    /**
     * Removes a song from the user's favorites
     * 
     * @param song The song to remove
     */
    public void removeFavoriteSong(Song song) {
        favoriteSongs.remove(song);
    }

    /**
     * Gets the users that this user follows
     * 
     * @return ArrayList of followed users
     */
    public ArrayList<User> getFollowedUsers() {
        return followedUsers;
    }

    /**
     * Follows another user
     * 
     * @param user The user to follow
     */
    public void followUser(User user) {
        if (!followedUsers.contains(user) && !user.equals(this)) {
            followedUsers.add(user);
        }
    }

    /**
     * Unfollows a user
     * 
     * @param user The user to unfollow
     */
    public void unfollowUser(User user) {
        followedUsers.remove(user);
    }

    /**
     * Gets the user's theme color
     *
     * @return The theme color
     */
    public ThemeColor getThemeColor() {
        return themeColor;
    }

    /**
     * Sets the user's theme color
     * 
     * @param themeColor The new theme color
     */
    public void setThemeColor(ThemeColor themeColor) {
        this.themeColor = themeColor;
    }

    /**
     * Authenticates a user with the provided credentials
     * 
     * @param username The username to check
     * @param password The password to check
     * @return AuthResult indicating the result (SUCCESS, INVALID_USERNAME, INVALID_PASSWORD)
     * @throws IllegalArgumentException if inputs are null
     */
    public AuthResult authenticate(String username, String password) throws IllegalArgumentException {
        // Validate inputs
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        
        // Check if username matches
        if (!this.username.equals(username)) {
            return AuthResult.INVALID_USERNAME;
        }
        
        // Check if password matches
        if (!this.password.equals(password)) {
            return AuthResult.INVALID_PASSWORD;
        }
        
        return AuthResult.SUCCESS;
    }

    /**
     * Checks if the provided credentials match this user
     * 
     * @param email The email to check
     * @param username The username to check
     * @param password The password to check
     * @return AuthResult indicating the result (SUCCESS, INVALID_CREDENTIALS, INVALID_PASSWORD)
     * @throws IllegalArgumentException if inputs are null
     */
    public AuthResult authenticate(String email, String username, String password) throws IllegalArgumentException {
        // Validate inputs
        if ((email == null || email.isEmpty()) && (username == null || username.isEmpty())) {
            throw new IllegalArgumentException("Either email or username must be provided");
        }
        
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        
        // Check if username or email matches
        boolean usernameOrEmailMatches = (email != null && this.email != null && this.email.equals(email)) || 
                                        (username != null && this.username.equals(username));
                                        
        if (!usernameOrEmailMatches) {
            return AuthResult.INVALID_CREDENTIALS;
        }
        
        // Check if password matches
        if (!this.password.equals(password)) {
            return AuthResult.INVALID_PASSWORD;
        }
        
        return AuthResult.SUCCESS;
    }

    /**
     * Helper method to check if a user is null
     * 
     * @param user The user to check
     * @param errorMessage Optional custom error message
     * @return The user if not null
     * @throws IllegalArgumentException if the user is null
     */
    public static User validateNotNull(User user, String errorMessage) throws IllegalArgumentException {
        if (user == null) {
            throw new IllegalArgumentException(errorMessage != null ? errorMessage : "User cannot be null");
        }
        return user;
    }

    /**
     * Checks if this user is equal to another object
     * 
     * @param obj The object to compare to
     * @return True if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return id.equals(user.id);
    }

    /**
     * Returns a hash code value for the user
     * 
     * @return Hash code value for the user
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    /**
     * Returns a string representation of the user
     * 
     * @return String representation of the user
     */
    @Override
    public String toString() {
        return this.username;
    }
}
