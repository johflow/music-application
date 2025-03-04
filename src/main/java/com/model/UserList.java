
import java.util.ArrayList;
import com.service.DataLoader;
import com.service.DataWriter;

public class UserList {
    private static UserList userList;
    private ArrayList<User> users;

    private UserList() {
        users = DataLoader.getUsers();
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

    public void addUser(User user) {
        if (user != null && getUser(user.getUsername()) == null) {
            users.add(user);
            save(); // Save after adding a user
        }
    }

    public void save() {
        DataWriter.saveUsers;
    }

    public ArrayList<User> getAllUsers() {
        return new ArrayList<>(users);
    }
}
