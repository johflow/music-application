package com.model;

import java.util.List;
import java.util.UUID;

public record ParsedUser(User user, List<UUID> followedUsers, List<UUID> favoritedSongs) {

}