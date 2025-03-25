package com.model;
import java.util.ArrayList;
import java.util.List;

public class Chord implements MusicElement {
    private String type;
    private String lyric;
    private List<Note> notes;

    public Chord() {
        //TODO
    }

    public Chord(String type, String lyric, List<Note> notes) {
        this.type = type;
        this.lyric = lyric;
        this.notes = notes;
    }

    public void addNote(int pitch, double duration) {
        //TODO
    }

    public Chord select() {
        //TODO
        return new Chord();
    }

    public void delete() {
        //TODO
    }

    public void play() {
        //TODO
    }

    public String toJFugueString() {
        //TODO
        return "";
    }

    /**
     * Gets the type of the chord
     * 
     * @return The type of the chord
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the lyric of the chord
     * 
     * @return The lyric of the chord
     */
    public String getLyric() {
        return lyric;
    }

    /**
     * Gets the notes of the chord
     * 
     * @return The notes of the chord
     */
    public List<Note> getNotes() {
        return notes;
    }

    @Override
    public String toString() {
        return "Chord{" +
            "notes=" + notes +
            '}';
    }
}