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
}
