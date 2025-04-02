package com.model;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class NoteTest {

    @Test
    public void TestComputePitch() {
        Note note = new Note("A4", 'w', "");
        assertEquals(note.getPitch(), 440.0, 0);
    }

    @Test
    public void TestComputeName() {
        Note note = new Note(440.0, 1, "");
        assertEquals(note.getNoteName(), "A4");
    }

    @Test
    public void TestComputeDuration() {
        Note note = new Note(440.0, 'h', "");
        assertEquals(note.getDuration(), 0.5, 0);
    }

    @Test
    public void TestComputeDurationChar() {
        Note note = new Note(440.0, 1.0, "");
        assertEquals(note.getDurationChar(), 'w');
    }

    @Test
    public void TestCapitalDurationChar() {
        Note note = new Note(440.0, 'H', "");
        assertEquals(note.getDuration(), 0.5, 0);
    }

    @Test
    public void TestInvalidDurationChar() {
        Note note = new Note(440.0, 'a', "");
        assertEquals(note.getDuration(), null);
    }

}
