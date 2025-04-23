package com.model;

import java.util.ArrayList;
import java.util.List;
import org.jfugue.player.Player;

/**
 * Represents a musical chord composed of multiple notes played simultaneously.
 * A chord can optionally contain a lyric annotation and supports playback using JFugue.
 */
public class Chord implements MusicElement {
    private final String type = "chord";
    private String lyric;
    private List<Note> notes;
    private double tempo;

    //Constructors

    /**
     * Creates an empty chord with no lyric.
     */
    public Chord() {
        this("", new ArrayList<>());
    }

    /**
     * Creates a chord with a given lyric and no initial notes.
     *
     * @param lyric Lyric annotation for the chord (can be empty or null)
     */
    public Chord(String lyric) {
        this(lyric, new ArrayList<>());
    }

    /**
     * Creates a chord with a given lyric and a list of initial notes.
     *
     * @param lyric Lyric annotation for the chord (can be empty or null)
     * @param notes List of notes to include in the chord (can be empty or null)
     */
    public Chord(String lyric, List<Note> notes) {
        this.lyric = (lyric != null) ? lyric : "";
        this.notes = (notes != null) ? notes : new ArrayList<>();
    }

    //Core Methods

    /**
     * Adds a note to the chord using MIDI pitch, duration, and lyric.
     *
     * @param pitch    MIDI pitch (integer, 0–127)
     * @param duration Duration of the note (e.g., 0.25 for quarter note)
     * @param lyric    Lyric for the note (optional, can be null)
     */
    public void addNote(int pitch, double duration, String lyric) {
        Note note = new Note(pitch, duration, lyric);
        notes.add(note);
    }

    /**
     * Adds an existing Note object to the chord.
     *
     * @param note Note instance to add (ignored if null)
     */
    public void addNote(Note note) {
        if (note != null) notes.add(note);
    }

    /**
     * Removes a specific Note object from the chord.
     *
     * @param note The note to remove
     * @return true if the note was found and removed
     */
    public boolean removeNote(Note note) {
        return notes.remove(note);
    }

    /**
     * Removes a note at a specific index from the chord.
     *
     * @param index Index of the note to remove
     * @return The removed Note
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public Note removeNote(int index) {
        return notes.remove(index);
    }

    /**
     * Plays the chord using JFugue.
     * Notes are played simultaneously.
     */
    public void play() {
        new Player().play(toJfugueString());
    }

    /**
     * Converts the chord to a JFugue string representation.
     * Format: [C4q+E4q+G4q] (chord of three quarter notes)
     *
     * @return JFugue-compatible string of the chord
     */
    @Override
    public String toJfugueString() {
        StringBuilder jfugue = new StringBuilder("[");
        for (int i = 0; i < notes.size(); i++) {
            jfugue.append(notes.get(i).toJfugueString());
            if (i < notes.size() - 1) {
                jfugue.append("+");
            }
        }
        jfugue.append("]");
        return jfugue.toString();
    }

    @Override
    public double getTempo() {
        return tempo;
    }

    /**
     * Returns the type of this music element.
     *
     * @return "chord"
     */
    @Override
    public String getType() {
        return type;
    }

    //Getters & Setters

    /**
     * Returns the list of notes in this chord.
     *
     * @return List of Note objects
     */
    public List<Note> getNotes() {
        return notes;
    }

    /**
     * Replaces the notes in this chord.
     *
     * @param notes New list of notes (null results in empty list)
     */
    public void setNotes(List<Note> notes) {
        this.notes = (notes != null) ? notes : new ArrayList<>();
    }

    /**
     * Gets the lyric associated with this chord.
     *
     * @return Lyric string (may be empty)
     */
    public String getLyric() {
        return lyric;
    }

    /**
     * Sets the lyric annotation for this chord.
     *
     * @param lyric New lyric string (null becomes empty)
     */
    public void setLyric(String lyric) {
        this.lyric = (lyric != null) ? lyric : "";
    }

    /**
     * Returns a basic string representation of the chord for debugging.
     *
     * @return String with lyric and note count
     */
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        for (Note note : notes) {
            stringBuilder.append(note).append(" ");
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

}
