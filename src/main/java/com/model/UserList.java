package com.model;

import java.util.ArrayList;
import com.service.DataLoader;
import com.service.DataWriter;

public class UserList {
    private static UserList userList;
    private ArrayList<User> users;

    private UserList() {
        users = DataLoader.getUser();
        if (users == null) {
            users = new ArrayList<>();
        }
    }

    public static UserList getInstance() {
        if (userList == null) {
            userList = new UserList();
        }
        return userList;
    }

    public User getUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
    
    public void addUser(User user) {
        if (user != null && getUser(user.getUsername()) == null) {
            users.add(user);
            save(); // Save after adding a user
        }
    }
    
    public void removeUser(User user) {
        if (users.remove(user)) {
            save(); // Save after removing a user
        }
    }

    public void save() {
        DataWriter.save(users);
    }

    public ArrayList<User> getAllUsers() {
        return new ArrayList<>(users);
    }
}
