package com.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a musical tuplet which is a grouping of notes played in the duration of a different number
 * of notes. This class implements the {@link MusicElement} interface.
 */
public class Tuplet implements MusicElement {
    private final String type = "tuplet";
    private int subdivisions;
    private int impliedDivision;
    private double duration;
    private List<MusicElement> elements;

    /**
     * Constructs a {@code Tuplet} with the specified parameters.
     *
     * @param subdivisions the number of subdivisions in the tuplet
     * @param impliedDivision the implied division of the tuplet
     * @param duration the duration of the tuplet
     * @param elements the list of music elements contained in the tuplet
     */
    public Tuplet(int subdivisions, int impliedDivision, double duration, List<MusicElement> elements) {
        this.subdivisions = subdivisions;
        this.impliedDivision = impliedDivision;
        this.duration = duration;
        this.elements = elements;
    }

    /**
     * Default constructor that initializes the {@code Tuplet} with type "tuplet" and an empty list of elements.
     */
    public Tuplet() {
        this.elements = new ArrayList<>();
    }

    /**
     * Adds a note to the tuplet.
     *
     * @param pitch the pitch of the note
     * @param duration the duration of the note
     * @param lyric the lyric associated with the note
     */
    public void addNote(int pitch, double duration, String lyric) {
        Note note = new Note(pitch, 0, "", duration, ' ', 0, false, lyric);
        elements.add(note);
    }

    /**
     * Returns the type of this music element.
     *
     * @return the type of the tuplet
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Converts the tuplet into its JFugue string representation by concatenating the string
     * representations of its elements.
     *
     * @return the JFugue string representation of the tuplet
     */
    @Override
    public String toJfugueString() {
        StringBuilder jfugueString = new StringBuilder();
        for (MusicElement element : elements) {
            jfugueString.append(element.toJfugueString()).append(" ");
        }
        return jfugueString.toString().trim();
    }

    /**
     * Returns the number of subdivisions in the tuplet.
     *
     * @return the subdivisions
     */
    public int getSubdivisions() {
        return subdivisions;
    }

    /**
     * Sets the number of subdivisions in the tuplet.
     *
     * @param subdivisions the new number of subdivisions
     */
    public void setSubdivisions(int subdivisions) {
        this.subdivisions = subdivisions;
    }

    /**
     * Returns the implied division of the tuplet.
     *
     * @return the implied division
     */
    public int getImpliedDivision() {
        return impliedDivision;
    }

    /**
     * Sets the implied division of the tuplet.
     *
     * @param impliedDivision the new implied division
     */
    public void setImpliedDivision(int impliedDivision) {
        this.impliedDivision = impliedDivision;
    }

    /**
     * Returns the duration of the tuplet.
     *
     * @return the duration
     */
    public double getDuration() {
        return duration;
    }

    /**
     * Sets the duration of the tuplet.
     *
     * @param duration the new duration
     */
    public void setDuration(double duration) {
        this.duration = duration;
    }

    /**
     * Returns the list of music elements contained in the tuplet.
     *
     * @return the list of elements
     */
    public List<MusicElement> getElements() {
        return elements;
    }

    /**
     * Returns a string representation of the tuplet, which includes each music element and
     * the subdivisions and implied division.
     *
     * @return a string representation of the tuplet
     */
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (MusicElement element : elements) {
            stringBuilder.append(element).append("*").append(subdivisions).append(":").append(impliedDivision).append(" ");
        }
        return stringBuilder.toString();
    }
}
