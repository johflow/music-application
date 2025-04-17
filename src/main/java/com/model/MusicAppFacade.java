package com.model;

import java.util.ArrayList;

import com.service.SongPlayer;
import java.util.List;

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
        viewedSong.getSheetMusic().getFirst().getStaves().getFirst().getMeasures().getFirst().addMusicElement(musicElement);
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
        viewedSong.getSheetMusic().getFirst().getStaves().getFirst().addMeasure(measure);
    }

    /**
     * Adds a staff to the currently viewed song.
     *
     * @param staff the staff to be added.
     */
    public void addStaff(Staff staff) {
        viewedSong.getSheetMusic().getFirst().addStaff(staff);
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
     */
    public void register(String userName, String password, String email) {
        userList.addUser(new User(email, userName, password));
        save(); // Save after adding new user
    }

    /**
     * Logs in a user with the given username and password.
     *
     * @param userName The username of the user.
     * @param password The password of the user.
     * @return true if login was successful, false otherwise
     */
    public boolean login(String userName, String password) {
        User user = userList.getUser(userName, password);
        if (user != null) {
            this.user = user;
            return true;
        }
        return false;
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
}
