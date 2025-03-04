package com.model;

import com.model.Song;
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
    this.email = email;
    this.username = username;
    this.password = password;
  }

  public void addFavoriteSong(Song song) {
    favoriteSongs.add(song);
  }

  public void removeFavoriteSong(Song song) {
    favoriteSongs.remove(song);
  }

  public void followUser(User user) {
    followedUsers.add(user);
  }

  public void unfollowUser(User user) {
    followedUsers.remove(user);
  }

  public void changeTheme(String themeColor) {
    this.themeColor = themeColor;
  }

  @Override
  public String toString() {
    return this.username + this.email + this.password;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public ArrayList<Song> getFavoriteSongs() {
    return favoriteSongs;
  }

  public void setFavoriteSongs(ArrayList<Song> favoriteSongs) {
    this.favoriteSongs = favoriteSongs;
  }

  public ArrayList<User> getFollowedUsers() {
    return followedUsers;
  }

  public void setFollowedUsers(ArrayList<User> followedUsers) {
    this.followedUsers = followedUsers;
  }

  public String getThemeColor() {
    return themeColor;
  }

  public void setThemeColor(String themeColor) {
    this.themeColor = themeColor;
  }
}
