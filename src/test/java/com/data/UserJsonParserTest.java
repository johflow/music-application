package com.data;

import com.model.ParsedUser;
import com.model.User;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;

/**
 * JUnit tests for the UserJsonParser class.
 */
public class UserJsonParserTest {

  // Sample JSON string with one valid user.
  private static final String VALID_JSON = "{ \"users\": [ " +
      "{ " +
      "\"id\": \"11111111-1111-1111-1111-111111111111\", " +
      "\"email\": \"user@example.com\", " +
      "\"username\": \"user1\", " +
      "\"password\": \"pass\", " +
      "\"followedUsers\": [\"22222222-2222-2222-2222-222222222222\", \"33333333-3333-3333-3333-333333333333\"], " +
      "\"favoritedSongs\": [\"44444444-4444-4444-4444-444444444444\"] " +
      "} " +
      "] }";

  @Test
  public void testGetParsedUsersWithValidJson() throws ParseException {
    UserJsonParser parser = new UserJsonParser();
    List<ParsedUser> users = parser.getParsedUsers(VALID_JSON);
    assertNotNull(users);
    assertEquals(1, users.size());

    ParsedUser parsedUser = users.getFirst();
    User user = parsedUser.user();
    assertEquals(UUID.fromString("11111111-1111-1111-1111-111111111111"), user.getId());
    assertEquals("user@example.com", user.getEmail());
    assertEquals("user1", user.getUsername());
    assertEquals("pass", user.getPassword());

    List<UUID> followedUsers = parsedUser.followedUsers();
    assertEquals(2, followedUsers.size());
    assertTrue(followedUsers.contains(UUID.fromString("22222222-2222-2222-2222-222222222222")));
    assertTrue(followedUsers.contains(UUID.fromString("33333333-3333-3333-3333-333333333333")));

    List<UUID> favoritedSongs = parsedUser.favoritedSongs();
    assertEquals(1, favoritedSongs.size());
    assertTrue(favoritedSongs.contains(UUID.fromString("44444444-4444-4444-4444-444444444444")));
  }

  @Test
  public void testGetParsedUsersWithEmptyString() throws ParseException {
    UserJsonParser parser = new UserJsonParser();
    List<ParsedUser> users = parser.getParsedUsers("");
    assertNotNull(users);
    assertTrue(users.isEmpty());
  }

  @Test
  public void testGetParsedUsersWithNull() throws ParseException {
    UserJsonParser parser = new UserJsonParser();
    List<ParsedUser> users = parser.getParsedUsers(null);
    assertNotNull(users);
    assertTrue(users.isEmpty());
  }

  @Test
  public void testGetParsedUsersWithMalformedJson() throws ParseException {
    UserJsonParser parser = new UserJsonParser();
    // Pass an invalid JSON string.
    List<ParsedUser> users = parser.getParsedUsers("{ invalid json }");
    assertNotNull(users);
    // A parsing error should lead to an empty JSONArray and then an empty list.
    assertTrue(users.isEmpty());
  }

  @Test
  public void testUserWithMissingRequiredFields() throws ParseException {
    // JSON where the user object is missing a required field (e.g., id).
    String jsonMissingId = "{ \"users\": [ " +
        "{ " +
        "\"email\": \"user@example.com\", " +
        "\"username\": \"user1\", " +
        "\"password\": \"pass\" " +
        "} " +
        "] }";
    UserJsonParser parser = new UserJsonParser();
    List<ParsedUser> users = parser.getParsedUsers(jsonMissingId);
    // The user should be skipped due to the missing id.
    assertNotNull(users);
    assertTrue(users.isEmpty());
  }
}
