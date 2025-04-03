package com.service;

import com.data.SongJsonParser;
import com.data.UserJsonParser;
import com.model.ParsedSong;
import com.model.ParsedUser;
import com.model.Song;
import com.model.User;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


public class DataAssemblerTest {


  private static final String TEST_USER_JSON = "{"
      + "\"users\": ["
      + "  {"
      + "    \"id\": \"11111111-1111-1111-1111-111111111111\","
      + "    \"email\": \"user1@example.com\","
      + "    \"username\": \"user1\","
      + "    \"password\": \"pass1\","
      + "    \"followedUsers\": [\"22222222-2222-2222-2222-222222222222\"],"
      + "    \"favoritedSongs\": [\"aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa\"]"
      + "  },"
      + "  {"
      + "    \"id\": \"22222222-2222-2222-2222-222222222222\","
      + "    \"email\": \"user2@example.com\","
      + "    \"username\": \"user2\","
      + "    \"password\": \"pass2\","
      + "    \"followedUsers\": [],"
      + "    \"favoritedSongs\": []"
      + "  }"
      + "]"
      + "}";


  private static final String TEST_SONG_JSON = "{"
      + "\"songs\": ["
      + "  {"
      + "    \"id\": \"aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa\","
      + "    \"title\": \"Test Song\","
      + "    \"composer\": \"Composer\","
      + "    \"publisher\": \"22222222-2222-2222-2222-222222222222\","
      + "    \"pickUp\": 4,"
      + "    \"sheetMusic\": ["
      + "      {"
      + "        \"instrument\": {"
      + "          \"instrumentName\": \"Piano\","
      + "          \"clefTypes\": [\"treble\"]"
      + "        },"
      + "        \"staves\": ["
      + "          {"
      + "            \"clefType\": \"treble\","
      + "            \"measures\": ["
      + "              {"
      + "                \"keySignature\": 0,"
      + "                \"timeSignatureNumerator\": 4,"
      + "                \"timeSignatureDenominator\": 4,"
      + "                \"tempo\": 120,"
      + "                \"musicElements\": []"
      + "              }"
      + "            ]"
      + "          }"
      + "        ]"
      + "      }"
      + "    ]"
      + "  }"
      + "]"
      + "}";


  private static class TestableDataAssembler extends DataAssembler {
    private final String userJson;
    private final String songJson;

    public TestableDataAssembler(String userJson, String songJson) {
      this.userJson = userJson;
      this.songJson = songJson;
    }

    @Override
    public List<User> getAssembledUsers() throws IOException, ParseException {

      UserJsonParser userParser = new UserJsonParser();
      SongJsonParser songParser = new SongJsonParser();
      List<ParsedUser> parsedUsers = userParser.getParsedUsers(userJson);
      List<ParsedSong> parsedSongs = songParser.getParsedSongs(songJson);


      Map<UUID, User> userMap = new HashMap<>();
      for (ParsedUser pu : parsedUsers) {
        userMap.put(pu.user().getId(), pu.user());
      }
      Map<UUID, Song> songMap = new HashMap<>();
      for (ParsedSong ps : parsedSongs) {
        songMap.put(ps.song().getId(), ps.song());
      }


      for (ParsedUser pu : parsedUsers) {
        for (UUID followId : pu.followedUsers()) {
          User followedUser = userMap.get(followId);
          if (followedUser != null) {
            pu.user().followUser(followedUser);
          }
        }
        for (UUID songId : pu.favoritedSongs()) {
          Song favSong = songMap.get(songId);
          if (favSong != null) {
            pu.user().addFavoriteSong(favSong);
          }
        }
      }
      return parsedUsers.stream().map(ParsedUser::user).collect(Collectors.toList());
    }

    @Override
    public List<Song> getAssembledSongs() throws IOException, ParseException {
      UserJsonParser userParser = new UserJsonParser();
      SongJsonParser songParser = new SongJsonParser();
      List<ParsedUser> parsedUsers = userParser.getParsedUsers(userJson);
      List<ParsedSong> parsedSongs = songParser.getParsedSongs(songJson);


      Map<UUID, User> userMap = new HashMap<>();
      for (ParsedUser pu : parsedUsers) {
        userMap.put(pu.user().getId(), pu.user());
      }

      Map<UUID, Song> songMap = new HashMap<>();
      for (ParsedSong ps : parsedSongs) {
        songMap.put(ps.song().getId(), ps.song());
      }


      for (ParsedSong ps : parsedSongs) {
        User publisher = userMap.get(ps.publisher());
        ps.song().setPublisher(publisher);
      }
      return parsedSongs.stream().map(ParsedSong::song).collect(Collectors.toList());
    }
  }

  @Test
  public void testGetAssembledUsers() throws IOException, ParseException {
    TestableDataAssembler assembler = new TestableDataAssembler(TEST_USER_JSON, TEST_SONG_JSON);
    List<User> users = assembler.getAssembledUsers();
    assertNotNull(users, "The assembled user list should not be null.");
    assertEquals(2, users.size(), "There should be two users assembled.");


    User user1 = users.stream()
        .filter(u -> u.getId().equals(UUID.fromString("11111111-1111-1111-1111-111111111111")))
        .findFirst()
        .orElse(null);
    User user2 = users.stream()
        .filter(u -> u.getId().equals(UUID.fromString("22222222-2222-2222-2222-222222222222")))
        .findFirst()
        .orElse(null);

    assertNotNull(user1, "User1 should be present.");
    assertNotNull(user2, "User2 should be present.");


    assertTrue(user1.getFollowedUsers().contains(user2), "User1 should follow User2.");


    assertEquals(1, user1.getFavoriteSongs().size(), "User1 should have one favorited song.");
    Song favoritedSong = user1.getFavoriteSongs().iterator().next();
    assertEquals(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"), favoritedSong.getId(),
        "The favorited song's ID should match.");
  }

  @Test
  public void testGetAssembledSongs() throws IOException, ParseException {
    TestableDataAssembler assembler = new TestableDataAssembler(TEST_USER_JSON, TEST_SONG_JSON);
    List<Song> songs = assembler.getAssembledSongs();
    assertNotNull(songs, "The assembled song list should not be null.");
    assertEquals(1, songs.size(), "There should be one assembled song.");

    Song song = songs.get(0);
    assertEquals(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"), song.getId(), "Song ID should match.");
    assertEquals("Test Song", song.getTitle(), "Song title should match.");


    assertNotNull(song.getPublisher(), "The song's publisher should not be null.");
    assertEquals(UUID.fromString("22222222-2222-2222-2222-222222222222"), song.getPublisher().getId(),
        "The publisher's ID should match User2's ID.");
  }
}
