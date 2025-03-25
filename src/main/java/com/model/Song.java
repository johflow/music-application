package com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a song in the music application
 */
public class Song {
    private UUID id;
    private String title;
    private String composer;
    private User publisher;
    private List<SheetMusic> sheetMusic;
    private int pickUp;

     /**
     * Constructor for new Song
     *
     * @param title The title of the song
     * @param composer The composer of the song
     */
    public Song(String title, String composer, User publisher) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.composer = composer;
        this.publisher = publisher;
        this.sheetMusic = new ArrayList<>();
        this.pickUp = 0;
    }

     /**
     * Constructor for a Song
     *
     * @param title The title of the song
     * @param composer The composer of the song
     */
    public Song(UUID id, String title, String composer, User publisher, List<SheetMusic> sheetMusic,
        int pickUp) {
        this.id = id;
        this.title = title;
        this.composer = composer;
        this.publisher = publisher;
        this.sheetMusic = sheetMusic;
        this.pickUp = pickUp;
    }



    /**
     * Adds sheet music to the song
     *
     * @param sheet The sheet music to add
     */
    public void addSheetMusic(SheetMusic sheet) {
        sheetMusic.add(sheet);
    }

    /**
     * Checks if the song matches a search query
     *
     * @param query The search query
     * @return True if the song matches the query, false otherwise
     */
    public boolean matchesQuery(String query) {
        if (query == null || query.isEmpty()) {
            return false;
        }

        String lowerQuery = query.toLowerCase();
        return title.toLowerCase().contains(lowerQuery) ||
               composer.toLowerCase().contains(lowerQuery);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Song song = (Song) obj;
        return id.equals(song.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Saves the song
     *
     * @return True if the save was successful, false otherwise
     */
    public boolean save() {
        //TODO
        return true;
    }

    /**
     * Gets the song's ID
     *
     * @return The song's UUID
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets the song's title
     *
     * @return The song's title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the song's composer
     *
     * @return The song's composer
     */
    public String getComposer() {
        return composer;
    }

    /**
     * Gets the song's publisher
     *
     * @return The song's publisher
     */
    public User getPublisher() {
        return publisher;
    }

    public List<SheetMusic> getSheetMusic() {
        return sheetMusic;
    }

    /**
     * Gets the song's pickup value
     *
     * @return The song's pickup value
     */
    public int getPickUp() {
        return pickUp;
    }

    /**
     * Sets the song's title
     *
     * @param title The new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the song's composer
     *
     * @param composer The new composer
     */
    public void setComposer(String composer) {
        this.composer = composer;
    }

    /**
     * Sets the song's pickup value
     *
     * @param pickUp The new pickup value
     */
    public void setPickUp(int pickUp) {
        this.pickUp = pickUp;
    }

    /**
     * Sets the song's publisher
     *
     * @param publisher The new publisher
     */
    public void setPublisher(User publisher) {
        this.publisher = publisher;
    }

  @Override
  public String toString() {
    return "Song{" +
        "sheetMusic=" + sheetMusic +
        '}';
  }
}

