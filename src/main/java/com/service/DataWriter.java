package com.service;

import com.model.User;
import com.model.UserList;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.model.DataConstants;
import com.model.Song;
import com.model.User;
import com.model.UserList;
/**
 * Class that writes data to JSON.
 * 
 * @author
 */
public class DataWriter extends DataConstants {
  /**
   * Writes user data to JSON.
   * 
   * @return True or false depending on success of write.
   */
  public static boolean saveUsers() {
    UserList users = UserList.getInstance();
    ArrayList<User> userList = DataLoader.getUsers(); //TODO: change this to users whenever we are ready for the data

    // Create the root JSON object with "users" key
    JSONObject root = new JSONObject();
    JSONArray jsonUsers = new JSONArray();
    
    for (int i = 0; i < userList.size(); i++) {
      jsonUsers.add(getUserJSON(userList.get(i)));
    }
    
    // Add the users array to the root object
    root.put("users", jsonUsers);
    
    try (FileWriter file = new FileWriter(USER_FILE_LOCATION)) {
      file.write(root.toJSONString());
      file.flush();
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Writes song data to JSON.
   * 
   * @return
   */
  public boolean saveSongs() {
    //TODO
    return true;
  }

  /**
   * For each data variable, it gets added to a JSONObject to be written in users.json
   * 
   * @param user user that is saving their data
   * @return     a JSONObject of user details
   */
  public static JSONObject getUserJSON(User user) {
    JSONObject userDetails = new JSONObject();
    JSONArray favoriteSongs = new JSONArray();
    JSONArray followedUsers = new JSONArray();
    
    for (Song favoriteSong : user.getFavoriteSongs()) {
      favoriteSongs.add(favoriteSong.getSongId().toString());
    }    
    for (User followedUser : user.getFollowedUsers()) {
      followedUsers.add(followedUser.getUserId().toString());
    }
    
    userDetails.put(USER_ID, user.getUserId().toString());
    userDetails.put(USER_EMAIL, user.getEmail());
    userDetails.put(USER_NAME, user.getUsername());
    userDetails.put(USER_PASSWORD, user.getPassword());
    userDetails.put(USER_FAVORITE_SONGS, favoriteSongs);
    userDetails.put(USER_FOLLOWED_USERS, followedUsers);
    userDetails.put(USER_THEME_COLOR, user.getThemeColor()); // still need to implement theme color system

    return userDetails;
  }

  /**
   * For each data variable, it gets added to a JSONObject to be written in songs.json
   * 
   * @param song
   * @return
   */
  public static JSONObject getSongsJSON(Song song) {
    JSONObject songDetails = new JSONObject();
    songDetails.put(null, null); // added null to compile but still needs to be filled
    //TODO
    return songDetails;
  }

  public static void main(String[] args) {
    DataWriter.saveUsers();
  }
}
