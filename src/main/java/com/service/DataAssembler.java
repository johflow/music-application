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

public class DataAssembler extends DataConstants {

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

  private void resolveParsedSongs(List<ParsedSong> parsedSongs, Map<UUID, User> userMap, Map<UUID, Song> songMap) {
    for (ParsedSong parsedSong : parsedSongs) {
        parsedSong.song().setPublisher(userMap.get(parsedSong.publisher()));
    }
  }

  public static void main(String[] args) throws IOException, ParseException {
    DataAssembler dataAssembler = new DataAssembler();
    System.out.println(dataAssembler.getAssembledSongs());
  }





}
