package com.model;

/**
 * Interface representing a musical element such as a note, rest, chord, or tuplet.
 * All musical elements must provide a type identifier and be able to convert 
 * themselves to JFugue string notation for playback.
 */
public interface MusicElement {

    /**
     * Gets the type of music element (e.g., "Note", "Rest", "Chord", "Tuplet")
     * 
     * @return A string identifying the type of music element
     */
    String getType();
    
    /**
     * Converts the music element to a JFugue-compatible string
     * representation for playback.
     * 
     * @return A string in JFugue notation format
     */
    String toJfugueString();
}