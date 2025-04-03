package com.model;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

public class ChordTest {

    @Test
    public void TestAddNote() {
        Chord chord = new Chord();
        chord.addNote(127, 0.5, "");
        assertEquals(chord.getNotes().size(), 1);
    }

    @Test
    public void TestAddInvalidNote() {
        Chord chord = new Chord();
        /**
         * MIDI pitch should not surpass 127, so a note with MIDI 440 should not be added
         */
        chord.addNote(440, 99, "");
        assertEquals(chord.getNotes().size(), 0);
    }

    @Test
    public void TestAddNullNote() {
        Chord chord = new Chord();

        chord.addNote(null);
        assertEquals(chord.getNotes().size(), 0);
    }

    @Test
    public void TestRemoveNote() {
        List<Note> notes = new ArrayList<>();
        Note note = new Note(440.0, 0.5, "");
        notes.add(note);
        Chord chord = new Chord("", notes);

        chord.removeNote(note);
        assertEquals(chord.getNotes().size(), 0);
    }

    @Test
    public void TestRemoveInvalidNote() {
        List<Note> notes = new ArrayList<>();
        Note note = new Note(440.0, 0.5, "");
        notes.add(note);
        Chord chord = new Chord("", notes);

        // Note being removed doesn't exist so no notes should be removed
        chord.removeNote(new Note(120.0, 1.0, ""));
        assertEquals(chord.getNotes().size(), 1);
    }

}
