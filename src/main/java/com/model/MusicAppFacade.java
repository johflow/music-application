package com.model;

import com.service.DataWriter;

public class MusicAppFacade {
    private User user;
    private MusicAppFacade instance;
    private SongList songList;
    private UserList userList;

    private MusicAppFacade() {

    }

    public MusicAppFacade getInstance() {
        return instance;
    }

    public createSong(String title, String composer, Instrument instrument, int tempo, KeySignature key, int timeSignatureNumerator, int timeSignatureDenominator, int numberOfMeasures, int pickup = 0) {

    }

    public playViewedSong() {

    }

    public pauseViewedSong() {

    }

    public User register(String userName, String password, String email) {
        userList.addUser(new User(email, userName, password));
    }

    public User login(String userName, String password) {
        user = userList.getUser(userName, password);
    }

    public void logout() {
        user = null;
    }

    public Song getSongByKeyWord(String word) {
        
    }

    public void addFavoriteSong(Song song) {
        user.addFavoriteSong(song);
    }

    public void removeFavoriteSong(Song song) {
        user.removeFavoriteSong(song);
    }

    public void followUser(User user) {
        this.user.followUser(user);
    }

    public void unfollowUser(User user) {
        this.user.unfollowUser(user);
    }

}