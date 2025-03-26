package com.model;

import java.util.ArrayList;
import java.util.UUID;

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
     */
    public User(String email, String username, String password) {
        this.id = UUID.randomUUID();
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
     */
    public User(UUID id, String email, String username, String password, ThemeColor themeColor) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.favoriteSongs = new ArrayList<>();
        this.followedUsers = new ArrayList<>();
        this.themeColor = themeColor;
    }

  public User(UUID id, String email, String username, String password) {
    this.id = id;
    this.email = email;
    this.username = username;
    this.password = password;
    this.themeColor = ThemeColor.getDefault();
    this.favoriteSongs = new ArrayList<>();
    this.followedUsers = new ArrayList<>();
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
     * Checks if the provided credentials match this user
     * 
     * @param email The email to check
     * @param username The username to check
     * @param password The password to check
     * @return True if credentials match, false otherwise
     */
    public boolean authenticate(String email, String username, String password) {
        return (this.email.equals(email) || this.username.equals(username)) && this.password.equals(password);
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
        return this.username + " " + this.email + " " + this.id + " " + this.password + " " + getFavoriteSongs() + " " + getFollowedUsers() + " " + getThemeColor();
    }
}
