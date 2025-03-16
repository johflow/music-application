package com.service;

import com.model.DataConstants;
import com.model.Song;
import com.model.ThemeColor;
import com.model.User;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Assembles data from persistent storage into objects
 */
public class DataAssembler extends DataConstants {

  public ArrayList<Song> getSongs() {
    //TODO
    return new ArrayList<Song>();
  }

  public static ArrayList<User> getUsers() {
    ArrayList<User> users = new ArrayList<User>();
    JSONParser parser = new JSONParser();
    HashMap<User, ArrayList<UUID>> userFollowers = new HashMap<>();
    HashMap<User, ArrayList<UUID>> userSongFavorites = new HashMap<>();

    try(FileReader reader = new FileReader(USER_FILE_LOCATION)) {

      JSONObject jsonObject = (JSONObject) parser.parse(reader);
      JSONArray peopleJSON = (JSONArray) jsonObject.get("users");

      for (Object o : peopleJSON) {
        JSONObject personJSON = (JSONObject) o;
        UUID id = UUID.fromString((String) personJSON.get(USER_ID));
        String email = (String) personJSON.get(USER_EMAIL);
        String userName = (String) personJSON.get(USER_NAME);
        String password = (String) personJSON.get(USER_PASSWORD);
        JSONArray favoriteSongs; //TODO
        ArrayList<UUID> favoriteSongsUUIDs = new ArrayList<>();
        JSONArray followedUsersJSON = (JSONArray) personJSON.get(USER_FOLLOWED_USERS);
        ArrayList<UUID> followedUsersUUIDs = new ArrayList<>();

        // Converts JSONArray to ArrayList for hashmap
        if (followedUsersJSON != null)
          for (Object userId: followedUsersJSON)
            followedUsersUUIDs.add(UUID.fromString((String)userId));

        User user = new User(id, email, userName, password);
        userFollowers.put(user, followedUsersUUIDs);
        users.add(user);
      }
      HashMap<UUID, User> userMap = new HashMap<>();
      for (User user: users) {
        userMap.put(user.getId(), user);
      }

      userFollowers.forEach((user, followedUUIDs) -> {
        for (UUID followedId : followedUUIDs) {
          User followedUser = userMap.get(followedId);
          user.followUser(followedUser);
        }
      });
      return users;
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
    } catch (IOException | ParseException e) {
      throw new RuntimeException(e);
    }
    return users;
  }


  public static void main(String[] args) {
    for (User user: getUsers()) {
      System.out.print(user + " Follows: ");
          for(User userFollowed: user.getFollowedUsers()) {
            System.out.println(userFollowed.getUsername());
          }
      System.out.println();
    }
  }

}
