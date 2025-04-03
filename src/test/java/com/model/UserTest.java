package com.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    private User user;

    @BeforeEach
    public void setUp() {
        user = new User("test@example.com", "testuser", "password123");
    }

    @Test
    public void testAuthenticateWithCorrectCredentials() {
        // Should return true for correct email/username and password
        assertTrue(user.authenticate("test@example.com", "testuser", "password123"));
    }

    @Test
    public void testAuthenticateWithWrongPassword() {
        // Should return false when password does not match
        assertFalse(user.authenticate("test@example.com", "testuser", "wrongpass"));
    }

    @Test
    public void testAddFavoriteSongIgnoresDuplicates() {
        // Should only add a song once to favorites
        Song song = new Song("Song A", "Composer A");
        user.addFavoriteSong(song);
        user.addFavoriteSong(song);
        assertEquals(1, user.getFavoriteSongs().size());
    }

    @Test
    public void testFollowUserDoesNotAddSelf() {
        // Should not be able to follow yourself
        user.followUser(user);
        assertEquals(0, user.getFollowedUsers().size());
    }

    @Test
    public void testFollowUserOnlyOnce() {
        // Should not follow the same user more than once
        User other = new User("other@example.com", "otheruser", "password");
        user.followUser(other);
        user.followUser(other);
        assertEquals(1, user.getFollowedUsers().size());
    }

    @Test
    public void testSetThemeColor() {
        // Should update the user's theme color
        user.setThemeColor(ThemeColor.RED);
        assertEquals(ThemeColor.RED, user.getThemeColor());
    }
}
