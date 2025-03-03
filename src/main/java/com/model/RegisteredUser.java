public class RegisteredUser extends User{
  
  private UUID id;
  private String email;
  private String username;
  private String password;
  private ArrayList<Song> favoriteSongs;
  private ArrayList<RegisteredUser> followedUsers;
  private String themeColor;

  public RegisteredUser(String email, String username, String password) {
    this.email = email;
    this.username = username;
    this.password = password;
  }

  public void addFavoriteSong(Song song) {
    favoriteSongs.add(song);
  }

  public void removeFavoriteSong(Song song) {
    favoriteSongs.remove(song);
  }

  public void followUser(User user) {
    followedUsers.add(user);
  }

  public void unfollowUser(User user) {
    followedUsers.remove(user);
  }

  public void changeTheme(String themeColor) {
    this.themeColor = themeColor;
  }
}
