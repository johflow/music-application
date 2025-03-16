package com.model;

import java.util.ArrayList;

import com.service.DataAssembler;
import com.service.DataWriter;

/**
 * Manages a collection of users in the music application
 */
public class UserList {
    private static UserList userList;
    private ArrayList<User> users;

    /**
     * Constructor for UserList
     */
    private UserList() {
        users = DataAssembler.getUsers();
        if (users == null) {
            users = new ArrayList<>();
        }
    }

    public static UserList getInstance() {
        if (userList == null) {
            userList = new UserList();
        }
        return userList;
    }

    /**
     * Gets the list of users
     * 
     * @return ArrayList of users
     */
    public ArrayList<User> getUsers() {
        return users;
    }

    /**
     * Sets the list of users
     * 
     * @param users The new list of users
     */
    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    /**
     * Gets a user by username
     * 
     * @param username The username to search for
     * @return The user with the matching username, or null if not found
     */
    public User getUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Gets a user by username and password (for authentication)
     * 
     * @param username The username to search for
     * @param password The password to verify
     * @return The authenticated user, or null if authentication fails
     */
    public User getUser(String username, String password) {
        User user = getUser(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    /**
     * Gets a list of users by username
     * 
     * @return ArrayList of users
     */
    public ArrayList<User> getUsers(String username) {
        ArrayList<User> matchingUsers = new ArrayList<>();
        
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
     * Adds a user to the list
     * 
     * @param user The user to add
     * @return True if the user was added successfully, false otherwise
     */
    public boolean addUser(User user) {
        if (user == null) {
            return false;
        }
        
        // Check if a user with the same username already exists
        for (User existingUser : users) {
            if (existingUser.getUsername().equals(user.getUsername())) {
                return false;
            }
        }
        
        users.add(user);
        save(); // Save after adding a user
        return true;
    }

    /**
     * Removes a user from the list
     * 
     * @param user The user to remove
     * @return True if the user was removed successfully, false otherwise
     */
    public boolean removeUser(User user) {
        return users.remove(user);
    }

    /**
     * Saves the user list
     * 
     * @return True if the save was successful, false otherwise
     */
    public boolean save() {
        DataWriter.saveUsers();
        return true;
    }

    public ArrayList<User> getAllUsers() {
        return new ArrayList<>(users);
    }
}
