package com.service;

import com.data.FileReaderUtil;
import com.data.SongJsonParser;
import com.data.UserJsonParser;
import com.model.DataConstants;
import com.model.ParsedSong;
import com.model.ParsedUser;
import com.model.Song;
import com.model.User;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.xml.crypto.Data;
import org.json.simple.parser.ParseException;

/**
 * The DataAssembler class is responsible for reading and parsing JSON data for users and songs,
 * resolving the relationships between them, and assembling fully-populated lists of {@link User} and {@link Song} objects.
 */
public class DataAssembler extends DataConstants {

  /**
   * Reads user and song JSON files, parses the data, resolves user follow relationships and song favorites,
   * and returns a list of assembled {@link User} objects.
   *
   * @return A list of fully assembled users.
   * @throws IOException if there is an issue reading the files.
   * @throws ParseException if there is an issue parsing the JSON data.
   */
  public List<User> getAssembledUsers() throws IOException, ParseException {
    FileReaderUtil fileReaderUtil = new FileReaderUtil();
    SongJsonParser songJsonParser = new SongJsonParser();
    UserJsonParser userJsonParser = new UserJsonParser();

    String userJSONContent = fileReaderUtil.readFile(USER_FILE_LOCATION);
    String songJSONContent = fileReaderUtil.readFile(SONG_FILE_LOCATION);

    List<ParsedUser> parsedUsers = userJsonParser.getParsedUsers(userJSONContent);
    List<ParsedSong> parsedSongs = songJsonParser.getParsedSongs(songJSONContent);

    Map<UUID, User> userMap = new HashMap<>();
    for (ParsedUser parsedUser : parsedUsers) {
      userMap.put(parsedUser.user().getId(), parsedUser.user());
    }

    Map<UUID, Song> songMap = new HashMap<>();
    for (ParsedSong parsedSong : parsedSongs) {
      songMap.put(parsedSong.song().getId(), parsedSong.song());
    }

    resolveParsedUsers(parsedUsers, userMap, songMap);

    return parsedUsers.stream()
        .map(ParsedUser::user)
        .toList();
  }

  /**
   * Reads user and song JSON files, parses the data, resolves song publisher information,
   * and returns a list of assembled {@link Song} objects.
   *
   * @return A list of fully assembled songs.
   * @throws IOException if there is an issue reading the files.
   * @throws ParseException if there is an issue parsing the JSON data.
   */
  public List<Song> getAssembledSongs() throws IOException, ParseException {
    FileReaderUtil fileReaderUtil = new FileReaderUtil();
    SongJsonParser songJsonParser = new SongJsonParser();
    UserJsonParser userJsonParser = new UserJsonParser();

    String userJSONContent = fileReaderUtil.readFile(USER_FILE_LOCATION);
    String songJSONContent = fileReaderUtil.readFile(SONG_FILE_LOCATION);

    List<ParsedUser> parsedUsers = userJsonParser.getParsedUsers(userJSONContent);
    List<ParsedSong> parsedSongs = songJsonParser.getParsedSongs(songJSONContent);

    Map<UUID, User> userMap = new HashMap<>();
    for (ParsedUser parsedUser : parsedUsers) {
      userMap.put(parsedUser.user().getId(), parsedUser.user());
    }

    Map<UUID, Song> songMap = new HashMap<>();
    for (ParsedSong parsedSong : parsedSongs) {
      songMap.put(parsedSong.song().getId(), parsedSong.song());
    }

    resolveParsedSongs(parsedSongs, userMap, songMap);

    return parsedSongs.stream()
        .map(ParsedSong::song)
        .toList();
  }

  /**
   * Resolves the relationships for parsed users by linking each user with their followed users and favorited songs.
   *
   * @param parsedUsers A list of parsed users.
   * @param userMap A map of user IDs to {@link User} objects.
   * @param songMap A map of song IDs to {@link Song} objects.
   */
  private void resolveParsedUsers(List<ParsedUser> parsedUsers, Map<UUID, User> userMap, Map<UUID, Song> songMap) {
    for (ParsedUser parsedUser : parsedUsers) {
      for (UUID id : parsedUser.followedUsers()) {
        parsedUser.user().followUser(userMap.get(id));
      }
      for (UUID id : parsedUser.favoritedSongs()) {
        parsedUser.user().addFavoriteSong(songMap.get(id));
      }
    }
  }

  /**
   * Resolves the publisher relationship for each parsed song by setting the publisher field based on the user map.
   *
   * @param parsedSongs A list of parsed songs.
   * @param userMap A map of user IDs to {@link User} objects.
   * @param songMap A map of song IDs to {@link Song} objects.
   */
  private void resolveParsedSongs(List<ParsedSong> parsedSongs, Map<UUID, User> userMap, Map<UUID, Song> songMap) {
    for (ParsedSong parsedSong : parsedSongs) {
      parsedSong.song().setPublisher(userMap.get(parsedSong.publisher()));
    }
  }

  /**
   * The main method demonstrating the usage of the DataAssembler class.
   * It assembles songs and prints the list of assembled songs to the console.
   *
   * @param args Command-line arguments.
   * @throws IOException if there is an issue reading the files.
   * @throws ParseException if there is an issue parsing the JSON data.
   */
  public static void main(String[] args) throws IOException, ParseException {
    DataAssembler dataAssembler = new DataAssembler();
    System.out.println(dataAssembler.getAssembledSongs());
    System.out.println(dataAssembler.getAssembledUsers());
  }
}
