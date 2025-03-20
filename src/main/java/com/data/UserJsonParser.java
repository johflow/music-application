package com.data;

import com.model.DataConstants;
import com.model.ParsedUser;
import com.model.User;
import java.io.IOException;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.ArrayList;
import java.util.UUID;

public class UserJsonParser extends DataConstants {

  public List<ParsedUser> getParsedUsers(String jsonContent) throws ParseException {
    JSONArray usersJsonArray = getUsersJSONArray(jsonContent);

    return parseUsers(usersJsonArray);
  }

  private JSONArray getUsersJSONArray(String jsonContent) throws ParseException {
    JSONParser parser = new JSONParser();
    JSONObject jsonObject = (JSONObject) parser.parse(jsonContent);

    return (JSONArray) jsonObject.get(USER_OBJECT_KEY);
  }

  private List<ParsedUser> parseUsers(JSONArray usersJsonArray) {
    List<ParsedUser> parsedUsers = new ArrayList<>();

    for (Object userObj : usersJsonArray) {
      JSONObject userJson = (JSONObject) userObj;

      User user = getBaseUser(userJson);

      List<UUID> followedUserIds = getFollowedUserIds(userJson);

      List<UUID> favoritedSongIds = getFavoritedSongs(userJson);

      parsedUsers.add(new ParsedUser(user, followedUserIds, favoritedSongIds));
    }

    return parsedUsers;
  }

  private User getBaseUser(JSONObject userJson) {
    UUID id = UUID.fromString((String) userJson.get(USER_ID));
    String email = (String) userJson.get(USER_EMAIL);
    String username = (String) userJson.get(USER_USERNAME);
    String password = (String) userJson.get(USER_PASSWORD);

    return new User(id, email, username, password);
  }

  private List<UUID> getFollowedUserIds(JSONObject userJson) {
    return getIDList(userJson);
  }

  private List<UUID> getFavoritedSongs(JSONObject userJson) {
    return getIDList(userJson);
  }

  private List<UUID> getIDList(JSONObject userJson) {
    JSONArray IDJson = (JSONArray) userJson.get(USER_FAVORITE_SONGS);
    List<UUID> IDs = new ArrayList<>();
    if (IDJson != null) {
      for (Object idObj : IDJson) {
        IDs.add(UUID.fromString((String) idObj));
      }
    }
    return IDs;
  }

}

