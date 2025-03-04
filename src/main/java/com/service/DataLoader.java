package com.service;

import com.model.DataConstants;
import com.model.Song;
import com.model.User;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.UUID;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class DataLoader extends DataConstants {

  public ArrayList<Song> getSongs() {
    //TODO
    return new ArrayList<Song>();
  }

  public static ArrayList<User> getUsers() {
    ArrayList<User> users = new ArrayList<User>();

    try {
      FileReader reader = new FileReader(USER_FILE_LOCATION);
      JSONParser parser = new JSONParser();
      JSONObject jsonObject = (JSONObject) parser.parse(reader);
      JSONArray peopleJSON = (JSONArray) jsonObject.get("users");


      for (int i = 0; i < peopleJSON.size(); i++) {
        JSONObject personJSON = (JSONObject) peopleJSON.get(i);
        UUID id = UUID.fromString((String) personJSON.get(USER_ID));
        String email = (String) personJSON.get(USER_EMAIL);
        String userName = (String) personJSON.get(USER_NAME);
        String password = (String) personJSON.get(USER_PASSWORD);
        users.add(new User(email, userName, password));
      }

      return users;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return users;
  }

}
