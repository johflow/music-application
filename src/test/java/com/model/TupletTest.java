package com.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.model.Tuplet;

import java.util.ArrayList;
import java.util.List;

public class TupletTest {
    private Tuplet tuplet;

    @BeforeEach
    public void setUp() {
        tuplet = new Tuplet(3, 2, 1.5, new ArrayList<>());

    }

    @Test
    public void testSetAndGetSubdivisions() {
        // Should update subdivisions
        tuplet.setSubdivisions(4);
        assertEquals(4, tuplet.getSubdivisions());
    }

    @Test
    public void testSetAndGetImpliedDivision() {
        // Should update implied division
        tuplet.setImpliedDivision(5);
        assertEquals(5, tuplet.getImpliedDivision());
    }

    @Test
    public void testSetAndGetDuration() {
        // Should update and retrieve duration
        tuplet.setDuration(2.5);
        assertEquals(2.5, tuplet.getDuration());
    }

    @Test
    public void testAddNoteIncreasesListSize() {
        // Should add a note to the tuplet
        Tuplet t = new Tuplet();
        t.addNote(60, 1.0, "lyric");
        assertEquals(1, t.getElements().size());
    }

    @Test
    public void testToJfugueStringContainsPitch() {
        // Should include note pitch in JFugue string
        Tuplet t = new Tuplet();
        t.addNote(64, 1.0, "do");
        assertTrue(t.toJfugueString().contains("64"));
    }

    @Test
    public void testToStringIncludesTiming() {
        // Should include timing info in toString
        tuplet.addNote(67, 1.0, "fa");
        assertTrue(tuplet.toString().contains("*3:2"));
    }
}
