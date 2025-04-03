package com.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class UserListTest {
    private UserList userList;

    @BeforeEach
    public void setUp() {
        userList = UserList.getInstance();
        userList.setUsers(List.of());
    }

    @Test
    public void testAddUserSuccessfully() {
        //Tests if a new user can be added successfully to the list
        User user = new User("test@example.com", "testuser", "pass");
        assertTrue(userList.addUser(user));
    }

    @Test
    public void testAddUserWithDuplicateUsernameFails() {
        //Ensures that adding a user with a duplicate username is rejected
        User user1 = new User("a@example.com", "sameuser", "pass1");
        User user2 = new User("b@example.com", "sameuser", "pass2");
        userList.addUser(user1);
        assertFalse(userList.addUser(user2));
    }

    @Test
    public void testGetUserByUsernameReturnsCorrectUser() {
        //Verifies that the correct user is returned by username lookup
        User user = new User("u@example.com", "uniqueuser", "pass");
        userList.addUser(user);
        assertEquals(user, userList.getUser("uniqueuser"));
    }

    @Test
    public void testGetUserByUsernameAndPasswordValidatesCorrectly() {
        // Confirms that user lookup by username and password works
        User user = new User("u@example.com", "secureuser", "securepass");
        userList.addUser(user);
        assertEquals(user, userList.getUser("secureuser", "securepass"));
    }

    @Test
    public void testRemoveUserSuccessfully() {
        // Tests that a user can be removed from the list
        User user = new User("del@example.com", "todelete", "pass");
        userList.addUser(user);
        assertTrue(userList.removeUser(user));
        assertNull(userList.getUser("todelete"));
    }

    @Test
    public void testGetUsersByPartialUsername() {
        // Ensures that users with usernames containing a partial match are returned
        User user1 = new User("a@example.com", "alice", "pass");
        User user2 = new User("b@example.com", "alicia", "pass");
        userList.addUser(user1);
        userList.addUser(user2);
        List<User> matches = userList.getUsersByUsername("ali");
        assertEquals(2, matches.size());
    }

    @Test
    public void testGetUsersByNullUsernameReturnsEmptyList() {
        // Checks that null or empty username input returns an empty result
        assertTrue(userList.getUsersByUsername(null).isEmpty());
    }
}
