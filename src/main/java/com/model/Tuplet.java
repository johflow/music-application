package com.model;

import java.util.ArrayList;
import java.util.List;

public class Tuplet implements MusicElement {
    private String type;
    private int subdivisions;
    private int impliedDivision;
    private double duration;
    private List<MusicElement> elements;

    // Constructor
    public Tuplet() {
        this.type = "Tuplet";
        this.elements = new ArrayList<>();
    }

    // Method to add a note to the tuplet
    public void addNote(int pitch, double duration, String lyric) {
        Note note = new Note(pitch, 0, "", duration, ' ', 0, false, lyric);
        elements.add(note);
    }

    // Implement MusicElement interface method
    @Override
    public String getType() {
        return type;
    }

    // Convert tuplet to JFugue string representation
    @Override
    public String toJfugueString() {
        StringBuilder jfugueString = new StringBuilder();
        for (MusicElement element : elements) {
            jfugueString.append(element.toJfugueString()).append(" ");
        }
        return jfugueString.toString().trim();
    }

    // Getters and Setters
    public int getSubdivisions() {
        return subdivisions;
    }

    public void setSubdivisions(int subdivisions) {
        this.subdivisions = subdivisions;
    }

    public int getImpliedDivision() {
        return impliedDivision;
    }

    public void setImpliedDivision(int impliedDivision) {
        this.impliedDivision = impliedDivision;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public List<MusicElement> getElements() {
        return elements;
    }
}