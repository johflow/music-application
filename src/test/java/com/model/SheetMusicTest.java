package com.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Arrays;

public class SheetMusicTest {
    private SheetMusic sheet;
    private Instrument instrument;

    @BeforeEach
    public void setUp() {
        instrument = new Instrument(Arrays.asList("bass", "treble"), "Piano");
        sheet = new SheetMusic(instrument);
    }

    @Test
    public void testAddStaffIncreasesList() {
        // Should increase staff list size
        Staff staff = new Staff("bass", new ArrayList<>());
        sheet.addStaff(staff);
        assertEquals(1, sheet.getStaves().size());
    }

    @Test
    public void testGetInstrumentNameCorrect() {
        // Should return the correct instrument name
        assertEquals("Piano", sheet.getInstrument().getInstrumentName());
    }

    @Test
    public void testToJfuguePatternNotNull() {
        // Should return a valid (non-null) pattern object
        assertNotNull(sheet.toJfuguePattern());
    }

    @Test
    public void testToStringIncludesClefType() {
        // Should contain clef info in string output
        Staff staff = new Staff("treble", new ArrayList<>());
        sheet.addStaff(staff);
        assertTrue(sheet.toString().contains("treble"));
    }
}
