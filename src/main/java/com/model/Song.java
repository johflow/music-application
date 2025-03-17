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
     * Constructor for Song
     *
     * @param title The title of the song
     * @param composer The composer of the song
     */
    public Song(String title, String composer) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.composer = composer;
        this.sheetMusic = new ArrayList<>();
        this.tempo = 120; // Default tempo
        this.key = KeySignature.C_MAJOR; // Default key
        this.pickUp = 0; // Default pickup
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
     * Sets the song's title
     *
     * @param title The new title
     */
    public void setTitle(String title) {
        this.title = title;
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
     * Sets the song's composer
     *
     * @param composer The new composer
     */
    public void setComposer(String composer) {
        this.composer = composer;
    }

    /**
     * Gets the song's instrument
     *
     * @return The song's instrument
     */
    public Instrument getInstrument() {
        return instrument;
    }

    /**
     * Sets the song's instrument
     *
     * @param instrument The new instrument
     */
    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    /**
     * Gets the song's tempo
     *
     * @return The song's tempo
     */
    public int getTempo() {
        return tempo;
    }

  public Song(UUID id, String title, String composer, int pickUp, List<SheetMusic> sheetMusic) {
    this.id = id;
    this.title = title;
    this.composer = composer;
    this.sheetMusic = sheetMusic;
    this.pickUp = pickUp;
  }

    /**
     * Sets the song's tempo
     *
     * @param tempo The new tempo
     */
    public void setTempo(int tempo) {
        this.tempo = tempo;
    }

    /**
     * Gets the song's key signature
     *
     * @return The song's key signature
     */
    public KeySignature getKey() {
        return key;
    }

    /**
     * Sets the song's key signature
     *
     * @param key The new key signature
     */
    public void setKey(KeySignature key) {
        this.key = key;
    public List<SheetMusic> getSheetMusics() {
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
     * Sets the song's pickup value
     *
     * @param pickUp The new pickup value
     */
    public void setPickUp(int pickUp) {
        this.pickUp = pickUp;
    }

    /**
     * Gets the song's sheet music
     *
     * @return ArrayList of sheet music
     */
    public ArrayList<SheetMusic> getSheetMusic() {
        return sheetMusic;
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
     * Gets the song's publisher
     *
     * @return The song's publisher
     */
    public User getPublisher() {
        return publisher;
    }

    /**
     * Sets the song's publisher
     *
     * @param publisher The new publisher
     */
    public void setPublisher(User publisher) {
        this.publisher = publisher;
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
        // This would typically interact with a persistence layer
        // For now, we'll just return true
        return true;
    }
}

