package com.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

public class StaffTest {
    private Staff staff;

    @BeforeEach
    public void setUp() {
        staff = new Staff("treble", new ArrayList<>());
    }

    @Test
    public void testGetClefType() {
        // Should return the clef type
        assertEquals("treble", staff.getClefType());
    }

    @Test
    public void testAddMeasureIncreasesSize() {
        // Should increase the number of measures
        Measure measure = new Measure(0, 4, 4, 120, new ArrayList<>());
        staff.addMeasure(measure);
        assertEquals(1, staff.getMeasures().size());
    }

    @Test
    public void testToStringIncludesClefType() {
        // Should include clef type in string representation
        assertTrue(staff.toString().contains("treble"));
    }

    @Test
    public void testToJfuguePatternReturnsNotNull() {
        // Should not return null from toJfuguePattern
        assertNotNull(staff.toJfuguePattern());
    }
}
