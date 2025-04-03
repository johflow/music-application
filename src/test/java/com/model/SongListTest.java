package com.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class SongListTest {
    private SongList songList;

    @BeforeEach
    public void setUp() {
        songList = SongList.getInstance();
        songList.setSongs(new ArrayList<>()); // Reset for each test
    }

    @Test
    public void testAddSongSuccessfully() {
        // Should add song to list
        Song song = new Song("New Song", "Composer");
        assertTrue(songList.addSong(song));
    }

    @Test
    public void testAddDuplicateSongFails() {
        // Should not add duplicate song
        Song song = new Song("Title", "Composer");
        songList.addSong(song);
        assertFalse(songList.addSong(song));
    }

    @Test
    public void testAddSongWithParamsCreatesSong() {
        // Should create and return a new song
        Song song = songList.addSong("Title", "Composer",
                new Instrument(Arrays.asList("treble"), "Violin"), 100, 4, 4, 1, 0);
        assertNotNull(song);
        assertEquals("Title", song.getTitle());
    }

    @Test
    public void testSearchSongsByTitleOrComposer() {
        // Should return songs matching query
        songList.addSong(new Song("Symphony", "Mozart"));
        List<Song> results = songList.searchSongs("symph");
        assertEquals(1, results.size());
    }

    @Test
    public void testSearchSongsWithEmptyQueryReturnsEmptyList() {
        // Should return empty list for empty query
        assertTrue(songList.searchSongs("").isEmpty());
    }

    @Test
    public void testSearchSongReturnsFirstMatch() {
        // Should return first song that matches
        songList.addSong(new Song("A", "Composer"));
        songList.addSong(new Song("B", "Composer"));
        Song result = songList.searchSong("composer");
        assertNotNull(result);
    }

    @Test
    public void testRemoveSongSuccessfully() {
        // Should remove song from list
        Song song = new Song("RemoveMe", "Composer");
        songList.addSong(song);
        assertTrue(songList.removeSong(song));
    }

    @Test
    public void testSortSongsByTitle() {
        // Should sort songs alphabetically by title
        Song s1 = new Song("Zebra", "Comp1");
        Song s2 = new Song("Apple", "Comp2");
        songList.setSongs(new ArrayList<>(List.of(s1, s2)));
        songList.sortSongs("title");
        assertEquals("Apple", songList.getSongs().get(0).getTitle());
    }

    @Test
    public void testSortSongsWithInvalidCriteriaDoesNothing() {
        // Invalid sort should not throw or change order
        Song song = new Song("Test", "Composer");
        songList.setSongs(new ArrayList<>(List.of(song)));
        songList.sortSongs("invalid");
        assertEquals(song, songList.getSongs().get(0));
    }
}