package com.service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
  public static boolean saveUsers(List<User> users) {
    // Create the root JSON object with "users" key
    JSONObject root = new JSONObject();
    JSONArray jsonUsers = new JSONArray();
    
    // Add each user to the JSON array
    for (User user : users) {
      jsonUsers.add(getUserJSON(user));
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
   * @return True or false depending on success of write.
   */
  public static boolean saveSongs(List<Song> songs) {
    JSONObject root = new JSONObject();
    JSONArray jsonSongs = new JSONArray();
    
    // Add each song to the JSON array
    for (Song song : songs) {
      jsonSongs.add(getSongJSON(song));
    }

    root.put("songs", jsonSongs);

    try (FileWriter file = new FileWriter(SONG_FILE_LOCATION)) {
      file.write(root.toJSONString());
      file.flush();
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
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
      favoriteSongs.add(favoriteSong.getId().toString());
    }    
    for (User followedUser : user.getFollowedUsers()) {
      followedUsers.add(followedUser.getId().toString());
    }
    
    userDetails.put(USER_ID, user.getId().toString());
    userDetails.put(USER_EMAIL, user.getEmail());
    userDetails.put(USER_NAME, user.getUsername());
    userDetails.put(USER_PASSWORD, user.getPassword());
    userDetails.put(USER_FAVORITE_SONGS, favoriteSongs);
    userDetails.put(USER_FOLLOWED_USERS, followedUsers);
    userDetails.put(USER_THEME_COLOR, user.getThemeColor().name());

    return userDetails;
  }

  /**
   * For each data variable, it gets added to a JSONObject to be written in songs.json
   * 
   * @param song The song to convert to JSON
   * @return A JSONObject representing the song
   */
  public static JSONObject getSongsJSON(Song song) {
    JSONObject songDetails = new JSONObject();
    JSONArray sheetMusicJSON = new JSONArray();
    
    // Basic song details
    songDetails.put(SONG_ID, song.getId().toString());
    songDetails.put(SONG_TITLE, song.getTitle());
    songDetails.put(SONG_COMPOSER, song.getComposer());
    songDetails.put(SONG_PUBLISHER, song.getPublisher());
    songDetails.put(SONG_PICK_UP, song.getPickUp());
    songDetails.put(SONG_SHEET_MUSIC, sheetMusicJSON);

    // add instrument to sheet music
    JSONObject instrumentJSON = new JSONObject();
    instrumentJSON.put(SONG_INSTRUMENT_NAME, song.getInstrumentName());
    
    // Add clef to sheet music
    JSONArray clefTypeJSON = new JSONArray();
    for (String clefType : song.getClefTypes()) {
        clefTypeJSON.add(clefType);
    }
    instrumentJSON.put(SONG_INSTRUMENT_CLEF_TYPES, clefTypeJSON);
    
    // Create staves section
    JSONArray stavesArray = new JSONArray();
    JSONObject staveJSON = new JSONObject();
    staveJSON.put(SONG_STAFF_CLEF_TYPE, song.getStaffClefType());
    
    // Create measures section
    JSONArray measuresArray = new JSONArray();
    JSONObject measureJSON = new JSONObject();
    measureJSON.put(SONG_MEASURES_KEY_SIGNATURE, song.getKeySignature());
    measureJSON.put(SONG_MEASURES_TIME_SIGNATURE_NUMERATOR, song.getTimeSignatureNumerator());
    measureJSON.put(SONG_MEASURES_TIME_SIGNATURE_DENOMINATOR, song.getTimeSignatureDenominator());
    measureJSON.put(SONG_MEASURES_TEMPO, song.getTempo());
    
    // Add music elements
    JSONArray musicElementsArray = new JSONArray();
    //TODO:here goes the music elements which i will wait until later to do
    measureJSON.put(SONG_MUSIC_ELEMENTS, musicElementsArray);
    
    // Builds hierarchy
    measuresArray.add(measureJSON);
    staveJSON.put(SONG_MEASURES, measuresArray);
    stavesArray.add(staveJSON);
    
    // Add instrument and staves to sheet music
    JSONObject sheetMusicEntry = new JSONObject();
    sheetMusicEntry.put(SONG_INSTRUMENT, instrumentJSON);
    sheetMusicEntry.put(SONG_STAVES, stavesArray);
    sheetMusicJSON.add(sheetMusicEntry);

    return songDetails;
  }

  public static void main(String[] args) {
    DataWriter.saveUsers(DataAssembler.getAssembledUsers());
  }
}
