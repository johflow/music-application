package com.model;
import java.util.ArrayList;
import java.util.List;

public class Chord implements MusicElement {
    private String lyric;
    private List<Note> notes;

    public Chord() {
        //TODO
    }

    public Chord(String lyric, List<Note> notes) {
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



}
