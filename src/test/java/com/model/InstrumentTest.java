package com.model;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

public class InstrumentTest {
    
    @Test
    public void TestAddClefType() {
        List<String> clefs = new ArrayList<>();
        Instrument instrument = new Instrument(clefs, "piano");

        instrument.addClefType("C");
        assertEquals(instrument.getClefTypes().size(), 1);
    }

    @Test
    public void TestRemoveClefType() {
        List<String> clefs = new ArrayList<>();
        clefs.add("C");
        Instrument instrument = new Instrument(clefs, "piano");

        instrument.removeClefType("C");
        assertEquals(instrument.getClefTypes().size(), 0);
    }

    @Test
    public void TestRemoveInvalidClefType() {
        List<String> clefs = new ArrayList<>();
        clefs.add("C");
        Instrument instrument = new Instrument(clefs, "piano");

        try {
            instrument.removeClefType("D");  // Attempt to remove an invalid clef type
        } catch (Exception e) {
            System.out.println("Exception caught: " + e.getMessage());
        }

        // Assert that the clef types list size remains the same after trying to remove an invalid clef type
        assertEquals(instrument.getClefTypes().size(), 1);
    }

}
