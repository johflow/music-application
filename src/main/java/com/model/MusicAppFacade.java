package com.model;

import java.util.ArrayList;
import java.util.List;

import com.service.SongPlayer;

public class MusicAppFacade {
    private User user;
    private static MusicAppFacade instance;
    private SongList songList;
    private UserList userList;
    private Song viewedSong;

    /**
     * Private constructor to enforce singleton pattern.
     */
    private MusicAppFacade() {
        this.user = null;
        this.songList = SongList.getInstance();
        songList.loadSongs();
        this.userList = UserList.getInstance();
        userList.loadUsers();
        this.viewedSong = null;
    }

    /**
     * Returns the singleton instance of the MusicAppFacade.
     *
     * @return the singleton instance
     */
    public static MusicAppFacade getInstance() {
        if (instance == null) {
            instance = new MusicAppFacade();
        }
        return instance;
    }

    /**
     * Returns the top search result for a song that matches a keyword
     *
     * @param searchQuery the keyword used in the song search
     * @return the song found in the search
     */
    public Song searchForSong(String searchQuery) {
        return songList.searchSong(searchQuery);
    }

    /**
     * Searches for songs based off a query.
     * 
     * @param searchQuery the query
     * @return a ArrayList of songs matching the query
     */
    public ArrayList<Song> searchForSongs(String searchQuery) { 
        return songList.searchSongs(searchQuery); 
    }

    /**
     * Gets the stored list of songs
     */
    public SongList getSongList() {
        return songList;
    }

    /**
     * Sets the currently viewed song to a song input
     *
     * @param song the song being set as currently viewed
     */
    public void setViewedSong(Song song) {
        this.viewedSong = song;
    }

    /**
     * Creates a new song and adds it to the song list.
     *
     * @param title    The title of the song.
     * @param composer The composer of the song.
     */
    public void createSong(String title, String composer) {
        Song song = new Song(title, composer, user);
        songList.addSong(song);
    }

    /**
     * Adds a music element to the currently viewed song.
     *
     * @param musicElement The music element to be added.
     */
    public void addMusicElement(MusicElement musicElement) {
        viewedSong.getSheetMusic().get(0).getStaves().get(0).getMeasures().get(0).addMusicElement(musicElement);
    }

    /**
     * Adds a sheet music to the viewed song.
     * 
     * @param sheetMusic The sheet music object
     */
    public void addSheetMusic(SheetMusic sheetMusic) {
        viewedSong.addSheetMusic(sheetMusic);
    }

    /**
     * Adds a measure to the currently viewed song.
     *
     * @param measure The measure to be added.
     */
    public void addMeasure(Measure measure) {
        viewedSong.getSheetMusic().get(0).getStaves().get(0).addMeasure(measure);
    }

    /**
     * Adds a staff to the currently viewed song.
     *
     * @param staff the staff to be added.
     */
    public void addStaff(Staff staff) {
        viewedSong.getSheetMusic().get(0).addStaff(staff);
    }

    /**
     * Plays the currently viewed song.
     */
    public void playViewedSong() {
        SongPlayer player = new SongPlayer();
        player.play(viewedSong);
    }

    /**
     * Pauses the currently viewed song.
     */
    public void pauseViewedSong() {
        // Implementation for pausing the viewed song
    }

    /**
     * Prints the currently viewed song to the console
     */
    public void printViewedSong() {
        System.out.println(viewedSong.toString());
    }

    /**
     * Registers a new user.
     *
     * @param userName The username of the new user.
     * @param password The password for the new user.
     * @param email    The email of the new user.
     * @throws IllegalArgumentException if validation fails (email, password) or username is taken
     */
    public void register(String userName, String password, String email) throws IllegalArgumentException {
        userList.register(email, userName, password);
    }

    /**
     * Logs in a user with the given username and password.
     *
     * @param userName The username of the user.
     * @param password The password of the user.
     * @return AuthResult indicating success or specific failure reason
     * @throws IllegalArgumentException if the username is null/empty or password is null
     */
    public AuthResult login(String userName, String password) throws IllegalArgumentException {
        AuthResult result = userList.login(userName, password);
        
        if (result == AuthResult.SUCCESS) {
            this.user = userList.getUser(userName);
        }
        
        return result;
    }

    /**
     * Takes a song and exports it to a text document
     *
     */
    public void exportViewedSongToFile() {
        viewedSong.outputToFile();
    }

    /**
     * Prints title of song
     */
    public void printSongTitles(List<Song> songs) {
        for (Song song : songs) {
            System.out.println(song.getTitle());
        }
    }

    /**
     * Returns the currently logged in user.
     *
     * @return the current user, or null if no user is logged in
     */
    public User getUser() {
        return user;
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        save();
        this.user = null;
    }

    /**
     * Adds a song to the user's list of favorite songs.
     *
     * @param song The song to be added to favorites.
     */
    public void addFavoriteSong(Song song) {
        user.addFavoriteSong(song);
    }

    /**
     * Removes a song from the user's list of favorite songs.
     *
     * @param song The song to be removed from favorites.
     */
    public void removeFavoriteSong(Song song) {
        user.removeFavoriteSong(song);
    }

    /**
     * Follows another user.
     *
     * @param user The user to follow.
     */
    public void followUser(User user) {
        this.user.followUser(user);
    }

    /**
     * Unfollows another user.
     *
     * @param user The user to unfollow.
     */
    public void unfollowUser(User user) {
        this.user.unfollowUser(user);
    }

    /**
     * Saves user list and song list.
     * 
     * @return True if both user and song save correctly
     */
    public boolean save() {
        return userList.save() && songList.save();
    }
    
    /**
     * Validates if an email is in the correct format.
     * 
     * @param email The email to validate
     * @throws IllegalArgumentException if the email doesn't meet format requirements
     */
    public void validateEmail(String email) throws IllegalArgumentException {
        User.isEmailValid(email);
    }

    /**
     * Validates if a password meets security requirements.
     * 
     * @param password The password to validate
     * @throws IllegalArgumentException if the password doesn't meet security requirements
     */
    public void validatePassword(String password) throws IllegalArgumentException {
        User.isPasswordValid(password);
    }

    /**
     * Gets a list of validation errors for a password.
     * 
     * @param password The password to validate
     * @return List of validation error messages, empty if password is valid
     */
    public List<String> getPasswordValidationErrors(String password) {
        return User.getPasswordValidationErrors(password);
    }

    /**
     * Returns the username of the logged-in user.
     * 
     * @return the username, or empty string if no user
     */
    public String getLoggedInUsername() {
        return user != null ? user.getUsername() : "";
    }

    /**
     * Returns the email of the logged-in user.
     * 
     * @return the email, or empty string if no user
     */
    public String getLoggedInEmail() {
        return user != null ? user.getEmail() : "";
    }

    /**
     * Returns the theme color of the logged-in user as a string.
     * 
     * @return the theme color, or "DEFAULT" if none
     */
    public String getThemeColor() {
        return user != null && user.getThemeColor() != null
            ? user.getThemeColor().toString()
            : "DEFAULT";
    }

    /**
     * Checks if a user is currently logged in
     * 
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return user != null;
    }
    
    /**
     * Gets the bio of the currently logged-in user
     * 
     * @return the bio, or empty string if no user or no bio
     */
    public String getUserBio() {
        return user != null ? user.getBio() : "";
    }
    
    /**
     * Gets the profile picture path of the currently logged-in user
     * 
     * @return the profile picture path, or empty string if no user
     */
    public String getUserProfilePicturePath() {
        return user != null ? user.getProfilePicturePath() : "";
    }

    /**
     * Returns a list of favorite song titles and composers for display.
     * 
     * @return list of formatted song titles
     */
    public List<String> getFavoriteSongTitles() {
        List<String> titles = new ArrayList<>();
        if (user != null) {
            for (Song song : user.getFavoriteSongs()) {
                titles.add(song.getTitle() + " - " + song.getComposer());
            }
        }
        return titles;
    }

    /**
     * Returns a list of usernames the user is following.
     * 
     * @return list of followed usernames
     */
    public List<String> getFollowedUsernames() {
        List<String> usernames = new ArrayList<>();
        if (user != null) {
            for (User followed : user.getFollowedUsers()) {
                usernames.add(followed.getUsername());
            }
        }
        return usernames;
    }

    /**
     * Updates the current user's username.
     * 
     * @param newUsername The new username
     * @return true if the update was successful, false otherwise
     */
    public boolean updateUsername(String newUsername) {
        User.validateNotNull(user);
        user.setUsername(newUsername);
        return true;
    }
    
    /**
     * Updates the current user's password.
     * 
     * @param newPassword The new password
     * @return true if the update was successful, false otherwise
     */
    public boolean updatePassword(String newPassword) {
        User.validateNotNull(user);
        user.setPassword(newPassword);
        return true;
    }
    
    /**
     * Updates the current user's email.
     * 
     * @param newEmail The new email
     * @return true if the update was successful, false otherwise
     */
    public boolean updateEmail(String newEmail) {
        User.validateNotNull(user);
        user.setEmail(newEmail);
        return true;
    }
    
    /**
     * Updates the current user's theme color.
     * 
     * @param themeColor The new theme color
     * @return true if the update was successful, false otherwise
     */
    public boolean updateThemeColor(ThemeColor themeColor) {
        User.validateNotNull(user);
        user.setThemeColor(themeColor);
        return true;
    }
    
    /**
     * Updates the current user's bio.
     * 
     * @param bio The new bio
     * @return true if the update was successful, false otherwise
     */
    public boolean updateBio(String bio) {
        User.validateNotNull(user);
        user.setBio(bio);
        return true;
    }
    
    /**
     * Updates the current user's profile picture.
     * 
     * @param profilePicturePath The path to the new profile picture
     * @return true if the update was successful, false otherwise
     */
    public boolean updateProfilePicture(String profilePicturePath) {
        User.validateNotNull(user);
        user.setProfilePicturePath(profilePicturePath);
        return true;
    }
    
    /**
     * Saves all user settings changes.
     * 
     * @return true if the save was successful, false otherwise
     */
    public boolean saveUserSettings() {
        return userList.save();
    }

    /**
     * Gets the default theme color for the application
     * 
     * @return The default ThemeColor
     */
    public ThemeColor getDefaultTheme() {
        return ThemeColor.getDefault();
    }
}
