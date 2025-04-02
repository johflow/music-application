package com.model;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

public class MeasureTest {
    
    @Test
    public void TestAddTuplet() {
        Tuplet tup = new Tuplet();
        List<MusicElement> mus = new ArrayList<>();
        Measure m = new Measure(1, 5, 4, 1, mus);

        m.addMusicElement(tup);
        assertEquals(1, mus.size());
    }

    @Test
    public void TestAddNote() {
        Note note = new Note(440.0, 1.0, "");
        List<MusicElement> mus = new ArrayList<>();
        Measure m = new Measure(1, 5, 4, 1, mus);

        m.addMusicElement(note);
        assertEquals(1, mus.size());
    }

    @Test
    public void TestAddChord() {
        Chord chord = new Chord();
        List<MusicElement> mus = new ArrayList<>();
        Measure m = new Measure(1, 5, 4, 1, mus);

        m.addMusicElement(chord);
        assertEquals(1, mus.size());
    }

    @Test 
    public void TestInvalidTimeSignature() {
        List<MusicElement> mus = new ArrayList<>();
        /**
         * Testing time sig of 5/3, which shouldn't be possible since the denominator must be a power of 2
         * time sig should be set to default - 4/4
         */
        Measure m = new Measure(1, 5, 3, 1, mus);
        assertEquals((m.getTimeSignatureNumerator() + "/" + m.getTimeSignatureDenominator()), "4/4");
    }

    @Test
    public void TestInvalidKeySignature() {
        List<MusicElement> mus = new ArrayList<>();
        /**
         * Testing key sig of 9, which shouldn't be possible because key sigs can't be larger than 7
         * key sig should be set to default - 0
         */
        Measure m = new Measure(9, 5, 4, 1, mus);
        assertEquals(m.getKeySignature(), 0);
    }
}
