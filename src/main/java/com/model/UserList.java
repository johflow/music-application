package com.model;

import com.model.User;
import java.util.ArrayList;

public class UserList {

  private ArrayList<User> users;
  private static UserList userList;

  private UserList() {
    //TODO
  }

  public static UserList getInstance() {
    //TODO
    return userList;
  }

  public ArrayList<User> getUserList() {
    return users;
  }

  public User getUser(String username) {
    //TODO
    return new User(null, null, null);
  }

  public void save() {
    //TODO
  }
}
