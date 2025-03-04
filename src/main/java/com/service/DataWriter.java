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
  public boolean saveUsers() {
    // these two lines will need to be changed to accomodate for user class once completed
		UserList users = UserList.getInstance();
    ArrayList<User> userList = users.getUserList();

    // creates a new JSON array and fills it
    JSONArray jsonUsers = new JSONArray();
    for (int i = 0; i < userList.size(); i++) {
			jsonUsers.add(getUserJSON(userList.get(i)));
		}
    
    // writes JSON file to its location
    try (FileWriter file = new FileWriter(USER_FILE_LOCATION)) {
      file.write(jsonUsers.toJSONString());
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
   * @param user
   * @return
   */
  public static JSONObject getUserJSON(User user) {
    JSONObject userDetails = new JSONObject();
    userDetails.put(USER_ID, user.getId());
    userDetails.put(USER_EMAIL, user.getEmail());
    userDetails.put(USER_NAME, user.getUsername());
    userDetails.put(USER_PASSWORD, user.getPassword()); // i feel like this is unsafe
    userDetails.put(USER_FAVORITE_SONGS, user.getFavoriteSongs());
    userDetails.put(USER_FOLLOWED_USERS, user.getFollowedUsers());
    userDetails.put(USER_THEME_COLOR, user.getThemeColor());

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
    songDetails.put(null, null); // added null needs to be fixed
    //TODO
    return songDetails;
  }

//  public static void main(String[] args){
//		DataWriter.saveUsers();
//	}
}
