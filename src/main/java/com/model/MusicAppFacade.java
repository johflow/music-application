package com.model;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.service.DataAssembler;
import com.service.DataWriter;

/**
 * MusicAppFacade serves as the main interface for the music application,
 * implementing the Facade design pattern to simplify client interactions
 * with the system's subsystems.
 */
public class MusicAppFacade {
    private User user;
    private static MusicAppFacade instance;
    private DataWriter dataWriter;
    private DataAssembler dataAssembler;
    private SongList songList;
    private UserList userList;

    /**
     * Constructor for the MusicAppFacade class
     */
    private MusicAppFacade() {
        this.dataWriter = new DataWriter();
        this.dataAssembler = new DataAssembler();
        this.songList = SongList.getInstance();
        this.userList = UserList.getInstance();
        this.user = null; // Initialize as null until login
    }

    /**
     * Returns the instance of MusicAppFacade
     * 
     * @return The instance
     */
    public static MusicAppFacade getInstance() {
        if (instance == null) {
            instance = new MusicAppFacade();
        }
        return instance;
    }

    /**
     * Creates a new song with the specified parameters
     * 
     * @param title                     The title of the song
     * @param composer                  The composer of the song
     * @param instrument                The instrument used in the song
     * @param tempo                     The tempo of the song
     * @param key                       The key signature of the song
     * @param timeSignatureNumerator    The numerator of the time signature
     * @param timeSignatureDenominator  The denominator of the time signature
     * @param numberOfMeasures          The number of measures in the song
     * @param pickup                    The pickup value
     */
    public void createSong(String title, String composer, Instrument instrument, int tempo, int timeSignatureNumerator, int timeSignatureDenominator, int numberOfMeasures, int pickup) {
        if (user == null) {
            return; // User must be logged in to create a song
        }
        
        Song song = new Song(title, composer);
        song.setPickUp(pickup);
        
        // Add the song to the song list
        songList.addSong(song);
        
        // Save the song to persistent storage
        dataWriter.saveSongs();
    }

    /**
     * Plays the currently viewed song
     */
    public void playViewedSong() {
        // Implementation would depend on how song playback is handled
        // This is a placeholder implementation
        if (user == null) {
            return;
        }
        
        // Actual implementation would need to track the currently viewed song
        // and use some audio playback mechanism
    }

    /**
     * Pauses the currently playing song
     */
    public void pauseViewedSong() {
        // Implementation would depend on how song playback is handled
        // This is a placeholder implementation
        if (user == null) {
            return;
        }
        
        // Actual implementation would need to interact with the audio playback mechanism
    }

    /**
     * Authenticates a user with the given credentials
     * 
     * @param username The username
     * @param password The password
     * @return The authenticated User object if successful
     */
    public User login(String username, String password) {
        User authenticatedUser = userList.getUser(username, password);
        if (authenticatedUser != null) {
            this.user = authenticatedUser;
        }
        return authenticatedUser;
    }

    /**
     * Searches for a song by keyword
     * 
     * @param word The keyword to search for
     * @return The found Song object
     */
    public Song getSongByKeyword(String word) {
        return songList.searchSong(word);
    }

    /**
     * Logs out the current user
     */
    public void logout() {
        this.user = null;
    }

    /**
     * Adds a song to the current user's favorites
     * 
     * @param song The song to add to favorites
     */
    public void addFavoriteSong(Song song) {
        if (user == null || song == null) {
            return;
        }
        
        user.addFavoriteSong(song);
    }

    /**
     * Removes a song from the current user's favorites
     * 
     * @param song The song to remove from favorites
     */
    public void removeFavoriteSong(Song song) {
        if (user == null || song == null) {
            return;
        }
        
        user.removeFavoriteSong(song);
    }

    /**
     * Follows another user
     * 
     * @param userToFollow The user to follow
     */
    public void followUser(User userToFollow) {
        if (user == null || userToFollow == null) {
            return;
        }
        
        user.followUser(userToFollow);
    }

    /**
     * Unfollows a user
     * 
     * @param userToUnfollow The user to unfollow
     */
    public void unfollowUser(User userToUnfollow) {
        if (user == null || userToUnfollow == null) {
            return;
        }
        
        user.unfollowUser(userToUnfollow);
    }

    /**
     * Starts the metronome
     */
    public void startMetronome() {
        // Implementation would depend on how the metronome is handled
        // This is a placeholder implementation
        // Actual implementation would need to create and start a metronome
    }

    /**
     * Stops the metronome
     */
    public void stopMetronome() {
        // Implementation would depend on how the metronome is handled
        // This is a placeholder implementation
        // Actual implementation would need to stop the metronome
    }
    
    /**
     * Signs up a new user
     * 
     * @param username The username for the new user
     * @param password The password for the new user
     * @return The newly created User object
     */
    public User signUp(String username, String password) {
        // Check if username already exists
        if (userList.getUser(username) != null) {
            return null; // Username already taken
        }
        
        // Create new user
        User newUser = new User(null, username, password);
        userList.addUser(newUser);
        return newUser;
    }
    
    /**
     * Gets the current user
     * 
     * @return The current user
     */
    public User getUser() {
        return user;
    }
    
    /**
     * Gets all songs from the song list
     * 
     * @return ArrayList of all songs
     */
    public ArrayList<Song> getSongs() {
        return songList.getSongs();
    }
    
    /**
     * Gets all users from the user list
     * 
     * @return ArrayList of all users
     */
    public ArrayList<User> getUsers() {
        return userList.getUsers();
    }
    
    /**
     * Gets all available theme colors
     * 
     * @return List of all theme colors
     */
    public List<ThemeColor> getAvailableThemeColors() {
        return Arrays.asList(ThemeColor.values());
    }
    
    /**
     * Sets the theme color for the current user
     * 
     * @param themeColor The new theme color
     * @return True if the theme color was set successfully, false otherwise
     */
    public boolean setUserThemeColor(ThemeColor themeColor) {
        if (user == null || themeColor == null) {
            return false;
        }
        
        user.setThemeColor(themeColor);
        return true;
    }
    
    /**
     * Sets the theme color for the current user by color name
     * 
     * @param colorName The name of the color
     * @return True if the theme color was set successfully, false otherwise
     */
    public boolean setUserThemeColorByName(String colorName) {
        if (user == null || colorName == null || colorName.isEmpty()) {
            return false;
        }
        
        try {
            ThemeColor themeColor = ThemeColor.valueOf(colorName.toUpperCase());
            return setUserThemeColor(themeColor);
        } catch (IllegalArgumentException e) {
            // If the color name is invalid, try to find it by display name
            ThemeColor themeColor = ThemeColor.getByName(colorName);
            return setUserThemeColor(themeColor);
        }
    }
}
