package com.data;

import com.model.DataConstants;
import com.model.ParsedUser;
import com.model.ThemeColor;
import com.model.User;
import java.io.IOException;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Parser for converting JSON content into ParsedUser objects.
 */
public class UserJsonParser extends DataConstants {

  /**
   * Parses the given JSON content and returns a list of ParsedUser objects.
   *
   * @param jsonContent the JSON content as a String
   * @return a list of ParsedUser objects
   * @throws ParseException if parsing the JSON fails
   */
  public List<ParsedUser> getParsedUsers(String jsonContent) throws ParseException {
    JSONArray usersJsonArray = getUsersJSONArray(jsonContent);

    return parseUsers(usersJsonArray);
  }

  /**
   * Parses the JSON content to extract the users JSONArray.
   *
   * @param jsonContent the JSON content as a String
   * @return a JSONArray containing user objects
   * @throws ParseException if parsing the JSON fails
   */
  private JSONArray getUsersJSONArray(String jsonContent) throws ParseException {
    JSONParser parser = new JSONParser();
    JSONObject jsonObject = (JSONObject) parser.parse(jsonContent);

    return (JSONArray) jsonObject.get(USER_OBJECT_KEY);
  }

  /**
   * Converts a JSONArray of users into a list of ParsedUser objects.
   *
   * @param usersJsonArray the JSONArray containing user objects
   * @return a list of ParsedUser objects
   */
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

  /**
   * Parses a JSON object representing a user and returns a User object.
   *
   * @param userJson the JSON object for the user
   * @return a User object with base user details
   */
  private User getBaseUser(JSONObject userJson) {
    UUID id = UUID.fromString((String) userJson.get(USER_ID));
    String email = (String) userJson.get(USER_EMAIL);
    String username = (String) userJson.get(USER_USERNAME);
    String password = (String) userJson.get(USER_PASSWORD);

    return new User(id, email, username, password);
  }

  /**
   * Retrieves a list of UUIDs representing the followed users from the JSON object.
   *
   * @param userJson the JSON object for the user
   * @return a list of UUIDs for followed users
   */
  private List<UUID> getFollowedUserIds(JSONObject userJson) {
    return getIDList(userJson, USER_FOLLOWED_USERS);
  }

  /**
   * Retrieves a list of UUIDs representing the favorited songs from the JSON object.
   *
   * @param userJson the JSON object for the user
   * @return a list of UUIDs for favorited songs
   */
  private List<UUID> getFavoritedSongs(JSONObject userJson) {
    return getIDList(userJson, USER_FAVORITED_SONGS);
  }

  /**
   * Retrieves a list of UUIDs from a JSON object for a given key.
   *
   * @param userJson the JSON object for the user
   * @param idKey    the key that maps to the JSONArray of UUID strings
   * @return a list of UUIDs
   */
  private List<UUID> getIDList(JSONObject userJson, String idKey) {
    JSONArray IDJson = (JSONArray) userJson.get(idKey);
    List<UUID> IDs = new ArrayList<>();
    if (IDJson != null) {
      for (Object idObj : IDJson) {
        IDs.add(UUID.fromString((String) idObj));
      }
    }
    return IDs;
  }

}
