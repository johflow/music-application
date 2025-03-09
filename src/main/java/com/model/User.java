package com.model;

import java.util.ArrayList;
import java.util.UUID;

public class User {

  private UUID id;
  private String email;
  private String username;
  private String password;
  private ArrayList<Song> favoriteSongs;
  private ArrayList<User> followedUsers;
  private String themeColor;


  public User(String email, String username, String password) {
    this.id = UUID.randomUUID();
    this.email = email;
    this.username = username;
    this.password = password;
    this.favoriteSongs = new ArrayList<>();
    this.followedUsers = new ArrayList<>();
    this.themeColor = "default";
  }
  public User(UUID id, String email, String username, String password) {
    this.id = id;
    this.email = email;
    this.username = username;
    this.password = password;
    this.favoriteSongs = new ArrayList<>();
    this.followedUsers = new ArrayList<>();
    this.themeColor = "default";
  }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getThemeColor() {
        return themeColor;
    }

    public ArrayList<Song> getFavoriteSongs() {
        return new ArrayList<>(favoriteSongs);
    }

    public ArrayList<User> getFollowedUsers() {
        return followedUsers;
    }

    // Methods to modify favorite songs
    public void addFavoriteSong(Song song) {
        if (song != null && !favoriteSongs.contains(song)) {
            favoriteSongs.add(song);
        }
    }

    public void removeFavoriteSong(Song song) {
        favoriteSongs.remove(song);
    }

    // Methods to follow/unfollow users
    public void followUser(User user) {
        if (user != null) {
            followedUsers.add(user);
        }
    }

    public void unfollowUser(User user) {
        followedUsers.remove(user);
    }

    // Change theme color
    public void changeTheme(String color) {
        if (color != null && !color.isEmpty()) {
            this.themeColor = color;
        }
    }
    @Override
    public String toString() {
        return this.username + " " + this.email + " " +  " " + this.password;
    }
}
