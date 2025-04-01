package com.data;

import com.model.DataConstants;
import com.model.ParsedUser;
import com.model.ThemeColor;
import com.model.User;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.ArrayList;
import java.util.UUID;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Parser for converting JSON content into ParsedUser objects.
 */
public class UserJsonParser extends DataConstants {

  private static final Logger logger = Logger.getLogger(UserJsonParser.class.getName());

  /**
   * Parses the given JSON content and returns a list of ParsedUser objects.
   *
   * @param jsonContent the JSON content as a String
   * @return a list of ParsedUser objects
   * @throws ParseException if parsing the JSON fails
   */
  public List<ParsedUser> getParsedUsers(String jsonContent) throws ParseException {

    if (jsonContent == null || jsonContent.trim().isEmpty()) {
      logger.severe("Invalid JSON file content: cannot be null or empty!");
      return List.of();
    }

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

    try {
      JSONObject jsonObject = (JSONObject) parser.parse(jsonContent);
      return (JSONArray) jsonObject.get(USER_OBJECT_KEY);

    } catch (ParseException e) {
      logger.log(Level.SEVERE, "Error parsing users JSON for JSONArray!", e);
      return new JSONArray();
    }


  }

  /**
   * Converts a JSONArray of users into a list of ParsedUser objects.
   *
   * @param usersJsonArray the JSONArray containing user objects
   * @return a list of ParsedUser objects
   */
  private List<ParsedUser> parseUsers(JSONArray usersJsonArray) {
    List<ParsedUser> parsedUsers = new ArrayList<>();

    if (usersJsonArray.isEmpty()) {
      logger.info("User JSON Array is empty.");
      return List.of();
    }

    for (Object userObj : usersJsonArray) {
      JSONObject userJson = (JSONObject) userObj;

      User user = getBaseUser(userJson);
      if(user == null) continue;
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
    try {
      UUID id = UUID.fromString(getValue(userJson, USER_ID, String.class));
      String email = getValue(userJson, USER_EMAIL, String.class);
      String username = getValue(userJson, USER_USERNAME, String.class);
      String password = getValue(userJson, USER_PASSWORD, String.class);
      return new User(id, email, username, password);

    } catch (IllegalArgumentException e) {
      return null;
    }
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
    JSONArray IDJson = getValue(userJson, idKey, JSONArray.class);
    List<UUID> IDs = new ArrayList<>();
    if (IDJson != null) {
      for (Object idObj : IDJson) {
        IDs.add(UUID.fromString((String) idObj));
      }
    }
    return IDs;
  }

  /**
   * Retrieves a value from a JSON object with the specified key and casts it to the desired class.
   *
   * @param <T>   the type of the expected value
   * @param object the JSON object from which to retrieve the value
   * @param key    the key for the value
   * @param clazz  the Class object corresponding to the desired type
   * @return the value associated with the key cast to the desired type
   * @throws IllegalArgumentException if the key is missing or the value is not of the expected type
   */
  private <T> T getValue(JSONObject object, String key, Class<T> clazz) {
    Object value = object.get(key);
    if(value == null)
      throw new IllegalArgumentException("Missing key: " + key);
    try {
      return clazz.cast(value);
    } catch (ClassCastException e) {
      throw new IllegalArgumentException("Expected key '" + key + "' to be of type "
              + clazz.getName() + " but found " + value.getClass().getName(), e);
    }
  }

}
