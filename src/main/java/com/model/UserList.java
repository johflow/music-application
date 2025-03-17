package com.model;

import com.service.DataAssembler;
import java.io.IOException;
import java.util.ArrayList;
import com.service.DataWriter;
import org.json.simple.parser.ParseException;

public class UserList {
    private static UserList userList;
    private ArrayList<User> users;

    private UserList() throws IOException, ParseException {
//        users = DataAssembler
//        if (users == null) {
//            users = new ArrayList<>();
//        }
    }

    public static UserList getInstance() throws IOException, ParseException {
        if (userList == null) {
            userList = new UserList();
        }
        return userList;
    }

    public ArrayList<User> getUserList() {
        return users;
      }

    public User getUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public ArrayList<User> getUsers() {
        return new ArrayList<>(users);
    }

    public void addUser(User user) throws IOException, ParseException {
        if (user != null && getUser(user.getUsername()) == null) {
            users.add(user);
            save(); // Save after adding a user
        }
    }

    public void save() throws IOException, ParseException {
        DataWriter.saveUsers();
    }

    public ArrayList<User> getAllUsers() {
        return new ArrayList<>(users);
    }
}
