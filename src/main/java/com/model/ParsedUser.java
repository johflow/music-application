package com.model;

import java.util.List;
import java.util.UUID;

public class ParsedUser {

  private final User user;
  private final List<UUID> followedUsers;
  private final List<UUID> favoritedSongs;

  public ParsedUser(User user, List<UUID> followedUsers, List<UUID> favoritedSongs) {
    this.user = user;
    this.followedUsers = followedUsers;
    this.favoritedSongs = favoritedSongs;
  }

  public User getUser() {
    return user;
  }

  public List<UUID> getFollowedUsers() {
    return followedUsers;
  }

  public List<UUID> getFavoritedSongs() {
    return favoritedSongs;
  }
}
