package com.model;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

import com.service.DataAssembler;
import com.service.DataWriter;

/**
 * Manages a collection of songs in the music application
 */
public class SongList {
    private static SongList instance;
    private List<Song> songs;

    /**
     * Constructor for SongList
     */
    private SongList() {
        this.songs = new ArrayList<>();
    }

    /**
     * Gets the singleton instance of SongList
     *
     * @return The singleton instance
     */
    public static SongList getInstance() {
        if (instance == null) {
            instance = new SongList();
        }
        return instance;
    }

    /**
     * Gets the list of songs
     *
     * @return ArrayList of songs
     */
    public List<Song> getSongs() {
        return songs;
    }

    /**
     * Sets the list of songs
     * 
     * @param songs The new list of songs
     */
    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    /**
     * Adds a song to the list
     * 
     * @param song The song to add
     * @return True if the song was added successfully, false otherwise
     */
    public boolean addSong(Song song) {
        if (song == null) {
            return false;
        }
        
        if (!songs.contains(song)) {
            songs.add(song);
            return true;
        }
        
        return false;
    }

    /**
     * Creates and adds a new song with the specified parameters
     *
     * @param title The title of the song
     * @param composer The composer of the song
     * @param instrument The instrument used in the song
     * @param tempo The tempo of the song
     * @param timeSignatureNumerator The numerator of the time signature
     * @param timeSignatureDenominator The denominator of the time signature
     * @param numberOfMeasures The number of measures in the song
     * @param pickup The pickup value
     * @return The newly created song
     */
    public Song addSong(String title, String composer, Instrument instrument, int tempo, int timeSignatureNumerator, int timeSignatureDenominator, int numberOfMeasures, int pickup) {
        Song newSong = new Song(title, composer);
        newSong.setPickUp(pickup);

        
        songs.add(newSong);
        return newSong;
    }

    /**
     * Removes a song from the list
     *
     * @param song The song to remove
     * @return True if the song was removed successfully, false otherwise
     */
    public boolean removeSong(Song song) {
        return songs.remove(song);
    }

    /**
     * Searches for songs by criteria
     * 
     * @param searchQuery The search query
     * @return ArrayList of songs that match the criteria
     */
    public ArrayList<Song> searchSongs(String searchQuery) {
        ArrayList<Song> results = new ArrayList<>();
        
        if (searchQuery == null || searchQuery.isEmpty()) {
            return results;
        }
        
        for (Song song : songs) {
            if (song.matchesQuery(searchQuery)) {
                results.add(song);
            }
        }
        
        return results;
    }

    /**
     * Searches for a single song by criteria
     *
     * @param searchQuery The search query
     * @return The first song that matches the criteria, or null if none found
     */
    public Song searchSong(String searchQuery) {
        ArrayList<Song> results = searchSongs(searchQuery);
        
        if (results.isEmpty()) {
            return null;
        }
        
        return results.get(0);
    }

    /**
     * Sorts the songs by criteria
     * 
     * @param criteria The sorting criteria
     */
    public void sortSongs(String criteria) {
        // Implementation would depend on the sorting criteria
        // This is a placeholder implementation
        if (criteria == null || criteria.isEmpty()) {
            return;
        }
        
        // Example: Sort by title
        if (criteria.equalsIgnoreCase("title")) {
            songs.sort((s1, s2) -> s1.getTitle().compareToIgnoreCase(s2.getTitle()));
        }
        // Example: Sort by composer
        else if (criteria.equalsIgnoreCase("composer")) {
            songs.sort((s1, s2) -> s1.getComposer().compareToIgnoreCase(s2.getComposer()));
        }
    }

    public boolean loadSongs() {
        try {
            DataAssembler dataAssembler = new DataAssembler();
            List<Song> loadedSongs = dataAssembler.getAssembledSongs();
            if (loadedSongs != null) {
                this.songs = loadedSongs;
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error loading songs: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Saves the song list
     * 
     * @return True if the save was successful, false otherwise
     */
    public boolean save() {
        // This would typically interact with a persistence layer
        // For now, we'll just return true
        return true;
    }
}
