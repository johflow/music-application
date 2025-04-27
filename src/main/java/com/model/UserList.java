package com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.service.DataAssembler;
import com.service.DataWriter;

/**
 * Manages a collection of users in the music application
 */
public class UserList {
    private static final Logger LOGGER = Logger.getLogger(UserList.class.getName());
    private List<User> users;
    private static UserList instance;
    
    /**
     * Constructor for UserList
     */
    private UserList() {
        users = new ArrayList<>();
    }
    
    /**
     * Gets the singleton instance of UserList
     * 
     * @return The UserList instance
     */
    public static UserList getInstance() {
        if (instance == null) {
            instance = new UserList();
            instance.loadUsers();
        }
        return instance;
    }

    /**
     * Gets the list of users
     * 
     * @return List of users
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * Sets the list of users
     * 
     * @param users The new list of users
     */
    public void setUsers(List<User> users) {
        this.users = new ArrayList<>(users);
    }

    /**
     * Gets a user by username
     * 
     * @param username The username to search for
     * @return The user with the matching username, or null if not found
     */
    public User getUser(String username) {
        LOGGER.log(Level.INFO, "Searching for user with username: " + username);
        for (User user : users) {
            LOGGER.log(Level.INFO, "Checking user: " + user.getUsername());
            if (user.getUsername().equals(username)) {
                LOGGER.log(Level.INFO, "Found user: " + user.getUsername());
                return user;
            }
        }
        LOGGER.log(Level.INFO, "No user found with username: " + username);
        return null;
    }

    /**
     * Returns a list of usernames who follwo the given target
     * @param targetUser the user whose followers to find
     * @return list of follower usernames
     */
    public List<String> getFollowers(User targetUser) {
        List<String> followers = new ArrayList<>();
        if (targetUser == null) {
            return followers;
        }
        for (User user : getAllUsers()) {
            if (user.getFollowedUsers().contains(targetUser)) {
                followers.add(user.getUsername());
            }
        }
        return followers;
    }




    /**
     * Gets a user by username and password (for authentication)
     * 
     * @param username The username to search for
     * @param password The password to verify
     * @return The authenticated user, or null if authentication fails
     * @throws IllegalArgumentException if username is null/empty or password is null
     * @throws AuthException with specific error message based on auth failure reason
     */
    public User getUser(String username, String password) throws IllegalArgumentException, AuthException {
        // Username error handling
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        
        // Gets user
        LOGGER.log(Level.INFO, "Attempting login for username: " + username);
        User user = getUser(username);
        
        // User not found
        if (user == null) {
            LOGGER.log(Level.INFO, "No user found with username: " + username);
            throw new AuthException("No user found with username: " + username);
        }
        
        // Main authentication method
        LOGGER.log(Level.INFO, "User found, authenticating");
        AuthResult result = user.authenticate(username, password);
        
        switch (result) {
            case SUCCESS:
                LOGGER.log(Level.INFO, "Authentication successful");
                return user;
            case INVALID_PASSWORD:
                LOGGER.log(Level.INFO, "Password mismatch");
                throw new AuthException("Invalid password");
            default:
                LOGGER.log(Level.WARNING, "Unexpected authentication result: " + result);
                throw new AuthException("Authentication failed: " + result);
        }
    }

    /**
     * Gets a list of users by username
     * 
     * @param username The username to search for
     * @return List of users matching the username
     */
    public List<User> getUsersByUsername(String username) {
        List<User> matchingUsers = new ArrayList<>();
        
        if (username == null || username.isEmpty()) {
            return matchingUsers;
        }
        
        String lowerUsername = username.toLowerCase();
        for (User user : users) {
            if (user.getUsername().toLowerCase().contains(lowerUsername)) {
                matchingUsers.add(user);
            }
        }
        
        return matchingUsers;
    }

    /**
     * Checks if a username is already taken
     * 
     * @param username The username to check
     * @return true if the username is already taken, false otherwise
     */
    public boolean isUsernameTaken(String username) {
        return getUser(username) != null;
    }

    /**
     * Adds a new user to the list
     * 
     * @param user The user to add
     * @return true if the user was added successfully, false if the username is already taken
     */
    public boolean addUser(User user) {
        if (user == null || isUsernameTaken(user.getUsername())) {
            return false;
        }
        users.add(user);
        return true;
    }

    /**
     * Removes a user from the list
     * 
     * @param user The user to remove
     * @return True if the user was removed successfully, false otherwise
     */
    public boolean removeUser(User user) {
        boolean removed = users.remove(user);
        if (removed) {
            save(); // Save after removing a user
        }
        return removed;
    }

    /**
     * Registers a new user with the provided credentials
     *
     * @param email User's email
     * @param username User's username
     * @param password User's password
     * @return The newly created user
     * @throws IllegalArgumentException if validation fails (email, password) or username is taken
     */
    public User register(String email, String username, String password) throws IllegalArgumentException {
        // Check if username is already taken before creating User
        if (isUsernameTaken(username)) {
            throw new IllegalArgumentException("Username already taken");
        }
        
        User newUser = new User(email, username, password);
        addUser(newUser);
        save();
        return newUser;
    }

    /**
     * Handles the login process and returns appropriate AuthResult
     *
     * @param username User's username
     * @param password User's password
     * @return AuthResult indicating success or the specific failure reason
     * @throws IllegalArgumentException if username is null/empty or password is null
     */
    public AuthResult login(String username, String password) throws IllegalArgumentException {
        try {
            // This will throw AuthException if there's an issue
            getUser(username, password);
            return AuthResult.SUCCESS;
        } catch (AuthException e) {
            // Check the specific error message to determine the type of failure
            if (e.getMessage().contains("No user found")) {
                return AuthResult.INVALID_USERNAME;
            } else if (e.getMessage().contains("Invalid password")) {
                return AuthResult.INVALID_PASSWORD;
            } else {
                return AuthResult.INVALID_CREDENTIALS;
            }
        }
    }

    /**
     * Saves the user list
     * 
     * @return True if the save was successful, false otherwise
     */
    public boolean save() {
        return DataWriter.saveUsers(this.users);
    }
    
    /**
     * Loads users from persistent storage
     * 
     * @return True if loading was successful, false otherwise
     */
    public boolean loadUsers() {
        try {
            LOGGER.log(Level.INFO, "Loading users from storage");
            DataAssembler dataAssembler = new DataAssembler();
            List<User> loadedUsers = dataAssembler.getAssembledUsers();
            if (loadedUsers != null) {
                this.users = loadedUsers;
                LOGGER.log(Level.INFO, "Successfully loaded " + users.size() + " users");
                for (User user : users) {
                    LOGGER.log(Level.INFO, "Loaded user: " + user.getUsername());
                }
                return true;
            }
            LOGGER.log(Level.SEVERE, "Failed to load users - loadedUsers is null");
            // Initialize with empty list to prevent NPEs
            if (this.users == null) {
                this.users = new ArrayList<>();
            }
            return false;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading users: " + e.getMessage(), e);
            // Initialize with empty list to prevent NPEs
            if (this.users == null) {
                this.users = new ArrayList<>();
            }
            return false;
        }
    }

    /**
     * Gets all users.
     * 
     * @return List of all users
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    /**
     * Prints users to a String.
     * 
     * @return A String of Users
     */
    public String toString() {
        return users.toString();
    }

    /**
     * Custom exception for authentication failures with more detailed messages
     */
    public static class AuthException extends RuntimeException {
        public AuthException(String message) {
            super(message);
        }
    }
}

