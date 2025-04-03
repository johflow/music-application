package com.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.model.Rest;

public class RestTest {
    Rest rest = new Rest(1.0, 'q', 0, false, "lyric");

    @Test
    public void testToJfugueString() {
        // Should return correct JFugue string
        assertEquals("Restq", rest.toJfugueString());
    }

    @Test
    public void testSetDurationValid() {
        // Should set a valid duration
        rest.setDuration(2.0);
        assertEquals(2.0, rest.getDuration());
    }

    @Test
    public void testSetDurationInvalidThrows() {
        // Should throw for negative duration
        assertThrows(IllegalArgumentException.class, () -> rest.setDuration(-1));
    }

    @Test
    public void testSetDottedNegativeThrows() {
        // Should throw if dotted is negative
        assertThrows(IllegalArgumentException.class, () -> rest.setDotted(-1));
    }

    @Test
    public void testToStringIncludesDotsAndTie() {
        // Should show dots and tie
        rest.setTied(true);
        rest.setDotted(2);
        assertEquals("Rq..-", rest.toString());
    }

    @Test
    public void testGetDottedString() {
        // Should return correct dot string
        rest.setDotted(3);
        assertEquals("...", rest.getDottedString());
    }

    @Test
    public void testLyricGetterSetter() {
        // Should set and get lyric
        rest.setLyric("pause");
        assertEquals("pause", rest.getLyric());
    }
}
