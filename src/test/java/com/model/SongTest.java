package com.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.UUID;
import java.util.Arrays;

public class SongTest {
    private Song song;
    private User user;

    @BeforeEach
    public void setUp() {
        user = new User("test@example.com", "user1", "password");
        song = new Song("Test Title", "Test Composer", user);
    }

    @Test
    public void testSongMatchesTitleQuery() {
        // Should return true if query matches title
        assertTrue(song.matchesQuery("test"));
    }

    @Test
    public void testSongMatchesComposerQuery() {
        // Should return true if query matches composer
        assertTrue(song.matchesQuery("composer"));
    }

    @Test
    public void testSongDoesNotMatchInvalidQuery() {
        // Should return false for non-matching query
        assertFalse(song.matchesQuery("randomword"));
    }

    @Test
    public void testAddSheetMusicIncreasesListSize() {
        // Should add sheet music to the song
        Instrument instrument = new Instrument(Arrays.asList("treble"), "Piano");
        SheetMusic sheet = new SheetMusic(instrument);
        song.addSheetMusic(sheet);
        assertEquals(1, song.getSheetMusic().size());
    }

    @Test
    public void testSetPickUpUpdatesValue() {
        // Should update pickup value
        song.setPickUp(2);
        assertEquals(2, song.getPickUp());
    }

    @Test
    public void testSetPublisherUpdatesReference() {
        // Should change publisher of song
        User publisher = new User("pub@example.com", "publisher", "pass");
        song.setPublisher(publisher);
        assertEquals(publisher, song.getPublisher());
    }

    @Test
    public void testEqualsReturnsTrueForSameId() {
        // Two songs with same UUID should be equal
        UUID id = UUID.randomUUID();
        Song s1 = new Song(id, "T1", "C1", user, List.of(), 0);
        Song s2 = new Song(id, "T2", "C2", user, List.of(), 1);
        assertEquals(s1, s2);
    }
}
