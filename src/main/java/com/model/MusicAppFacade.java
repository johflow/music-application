package com.model;
import java.util.ArrayList; 

/**
 * template
 * 
 * @author 
 */
public class MusicAppFacade {
    private User user;

    /**
     * template
     */
    private MusicAppFacade() {
        this.user = new User(null, null, null);
    }

    /**
     * template
     */
    public MusicAppFacade getInstance() {
        //TODO
        return new MusicAppFacade();
    }

    /**
     * template
     * 
     * @param title
     * @param composer
     * @param instrument
     * @param tempo
     * @param key
     * @param timeSignatureDenominator
     * @param timeSignatureNumerator
     * @param numberOfMeasures
     * @param pickup
     */
    public void createSong(String title, String composer, Instrument instrument, int tempo, KeySignature key, int timeSignatureNumerator, int timeSignatureDenominator, int numberOfMeasures, int pickup) {
        //TODO
    }

    /**
     * template
     * 
     * @param selectedNotes
     */
    public void deleteSelected(ArrayList<Note> selectedNotes) {
        //TODO
    }

    /**
     * template
     */
    public void playViewedSong() {
        //TODO
    }

    /**
     * template
     */
    public void pauseViewedSong() {
        //TODO
    }

    /**
     * template
     * 
     * @param username
     * @param password
     */
    public User login(String username, String password) {
        //TODO
        return new User(null, null, null);
    }

    /**
     * template
     * 
     * @param word
     */
    public Song getSongByKeyword(String word) {
        //TODO
        return new Song(null, null);
    }

    /**
     * template
     */
    public void logout() {
        //TODO
    }

    /**
     * template
     * 
     * @param song
     */
    public void addFavoriteSong(Song song) {
        //TODO  
    }

    /**
     * template
     * 
     * @param song
     */
    public void removeFavoriteSong(Song song) {
        //TODO
    }

    /**
     * template
     * 
     * @param user
     */
    public void followUser(User user) {
        //TODO
    }

    /**
     * template
     * 
     * @param user
     */
    public void unfollowUser(User user) {
        //TODO
    }

    /**
     * template
     */
    public void startMetronome() {
        //TODO
    }

    /**
     * template
     */
    public void stopMetronome() {
        //TODO
    }
}
