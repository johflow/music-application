package com.service;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.model.*;

public class DataWriterTest {
    
    private List<User> users;
    private List<Song> songs;
    private User testUser;
    private Song testSong;
    private static final String TEST_SONG_FILE = "src/test/resources/data/songs.json";
    private static final String TEST_USER_FILE = "src/test/resources/data/users.json";

    // <-------------------BEFORE AND AFTER SETUP------------------->
    @Before
    public void setUp() {
        // Set up test data
        testUser = new User("test@example.com", "testuser", "password");
        testSong = createTestSong();
        

    }
    
    @After
    public void tearDown() {
        // Clean up test files
        new File(TEST_SONG_FILE).delete();
        new File(TEST_USER_FILE).delete();
    }

    // <-------------------TESTS NORMAL USER AND SONG CASES------------------->
    @Test
    public void testSaveUsers() {
        // Set up test users
        List<User> users = new ArrayList<>();
        users.add(testUser);
        users.add(new User("user2@example.com", "user2", "pass2"));
        
        // Save users
        boolean result = DataWriter.saveUsers(users, TEST_USER_FILE);
        assertTrue(result);
        
        // Verify file exists
        File userFile = new File(TEST_USER_FILE);
        assertTrue(userFile.exists());
        
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(new FileReader(TEST_USER_FILE));
            JSONArray jsonUsers = (JSONArray) json.get("users");
            
            // Verify user count
            assertEquals(2, jsonUsers.size());
            
            // Verify user details
            boolean foundTestUser = false;
            boolean foundUser2 = false;
            
            for (Object obj : jsonUsers) {
                JSONObject jsonUser = (JSONObject) obj;
                String email = (String) jsonUser.get("email");
                
                if ("test@example.com".equals(email)) {
                    foundTestUser = true;
                    assertEquals("testuser", jsonUser.get("username"));
                } else if ("user2@example.com".equals(email)) {
                    foundUser2 = true;
                    assertEquals("user2", jsonUser.get("username"));
                }
            }
            
            assertTrue("Test user not found in JSON", foundTestUser);
            assertTrue("User2 not found in JSON", foundUser2);
            
        } catch (Exception e) {
            fail("Exception reading saved user file: " + e.getMessage());
        }
    }

    @Test
    public void testSaveSongs() {
        // Create a list with our test song
        ArrayList<Song> songs = new ArrayList<>();
        songs.add(testSong);
        
        // Save the songs to the test file
        boolean saveResult = DataWriter.saveSongs(songs, TEST_SONG_FILE);
        assertTrue("Failed to save songs", saveResult);
        
        // Verify file exists
        File songFile = new File(TEST_SONG_FILE);
        assertTrue("Song file was not created", songFile.exists());
        assertEquals("Incorrect number of songs", 1, songs.size());
        
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(new FileReader(TEST_SONG_FILE));
            JSONArray jsonSongs = (JSONArray) json.get("songs");
            
            assertNotNull("No songs array in JSON", jsonSongs);
            assertEquals("Wrong number of songs in JSON", 1, jsonSongs.size());
            
            JSONObject jsonSong = (JSONObject) jsonSongs.get(0);
            
            assertEquals("Song title doesn't match", testSong.getTitle(), jsonSong.get("title"));
            assertEquals("Song composer doesn't match", testSong.getComposer(), jsonSong.get("composer"));
            
            assertEquals("Song pickup doesn't match", 
                        String.valueOf(testSong.getPickUp()), 
                        String.valueOf(jsonSong.get("pickUp")));
            
            assertEquals("Song ID doesn't match", 
                        testSong.getId().toString(), 
                        String.valueOf(jsonSong.get("id")));
            
            if (testSong.getPublisher() != null) {
                Object publisherObj = jsonSong.get("publisher");
                assertNotNull("Publisher missing in JSON", publisherObj);
                
                if (publisherObj instanceof JSONObject) {
                    JSONObject jsonPublisher = (JSONObject) publisherObj;
                    assertEquals("Publisher email doesn't match", 
                                testSong.getPublisher().getEmail(), 
                                jsonPublisher.get("email"));
                } else {
                    System.out.println("Publisher is not a JSONObject: " + publisherObj);
                }
            }
            
            if (testSong.getSheetMusic() != null && !testSong.getSheetMusic().isEmpty()) {
                Object sheetMusicObj = jsonSong.get("sheetMusic");
                assertNotNull("Sheet music missing in JSON", sheetMusicObj);
                
                if (sheetMusicObj instanceof JSONArray) {
                    JSONArray jsonSheetMusic = (JSONArray) sheetMusicObj;
                    assertEquals("Sheet music count doesn't match", 
                                testSong.getSheetMusic().size(), 
                                jsonSheetMusic.size());
                } else {
                    System.out.println("SheetMusic is not a JSONArray: " + sheetMusicObj);
                }
            }
            
        } catch (Exception e) {
            fail("Exception reading saved song file: " + e.getMessage());
        }
    }
    
    // <-------------------TESTS NULL AND EMPTY CASES------------------->
    @Test
    public void testSaveEmptyLists() {
        // Test empty song list
        assertTrue(DataWriter.saveSongs(new ArrayList<>()));
        
        // Test empty user list
        assertTrue(DataWriter.saveUsers(new ArrayList<>()));
    }
    
    @Test
    public void testSaveNullLists() {
        assertFalse(DataWriter.saveSongs(null));
        assertFalse(DataWriter.saveUsers(null));
    }
    
    // <-------------------TESTS UNIQUE STRANGE INPUTS------------------->
    @Test
    public void testSaveSongWithAllMusicElements() {
        Song song = createTestSong();
        
        assertTrue(DataWriter.saveSongs(List.of(song), TEST_SONG_FILE));
        
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(new FileReader(TEST_SONG_FILE));
            JSONArray jsonSongs = (JSONArray) json.get("songs");
            assertEquals(1, jsonSongs.size());
        } catch (Exception e) {
            fail("Exception reading saved song file: " + e.getMessage());
        }
    }
    
    @Test
    public void testSaveSongWithNullTitle() {
        // Create song with null title
        Song song = createTestSong();
        song.setTitle(null);
        
        assertTrue(DataWriter.saveSongs(List.of(song), TEST_SONG_FILE));
        
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(new FileReader(TEST_SONG_FILE));
            JSONArray jsonSongs = (JSONArray) json.get("songs");
            
            assertEquals(1, jsonSongs.size());
            JSONObject jsonSong = (JSONObject) jsonSongs.get(0);
            assertNull(jsonSong.get("title"));
        } catch (Exception e) {
            fail("Exception reading saved song file: " + e.getMessage());
        }
    }

    @Test
    public void testSaveSongWithNullComposer() {
        // Create song with null composer
        Song song = createTestSong();
        song.setComposer(null);
        
        assertTrue(DataWriter.saveSongs(List.of(song), TEST_SONG_FILE));
        
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(new FileReader(TEST_SONG_FILE));
            JSONArray jsonSongs = (JSONArray) json.get("songs");
            
            assertEquals(1, jsonSongs.size());
            JSONObject jsonSong = (JSONObject) jsonSongs.get(0);
            assertNull(jsonSong.get("composer"));
        } catch (Exception e) {
            fail("Exception reading saved song file: " + e.getMessage());
        }
    }

    @Test
    public void testSaveSongWithNullPublisher() {
        // Create song with null publisher
        Song song = createTestSong();
        song.setPublisher(null);
        
        assertTrue(DataWriter.saveSongs(List.of(song), TEST_SONG_FILE));
        
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(new FileReader(TEST_SONG_FILE));
            JSONArray jsonSongs = (JSONArray) json.get("songs");
            
            assertEquals(1, jsonSongs.size());
            JSONObject jsonSong = (JSONObject) jsonSongs.get(0);
            assertNull(jsonSong.get("publisher"));
        } catch (Exception e) {
            fail("Exception reading saved song file: " + e.getMessage());
        }
    }

    @Test
    public void testSaveSongWithEmptySheetMusic() {
        // Create song with empty sheet music list
        Song song = new Song("Empty Sheet Music", "Test Composer", testUser);
        
        assertTrue(DataWriter.saveSongs(List.of(song), TEST_SONG_FILE));
        
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(new FileReader(TEST_SONG_FILE));
            JSONArray jsonSongs = (JSONArray) json.get("songs");
            
            assertEquals(1, jsonSongs.size());
            JSONObject jsonSong = (JSONObject) jsonSongs.get(0);
            JSONArray sheetMusic = (JSONArray) jsonSong.get("sheetMusic");
            assertEquals(0, sheetMusic.size());
        } catch (Exception e) {
            fail("Exception reading saved song file: " + e.getMessage());
        }
    }

    @Test
    public void testSaveUserWithEmptyFollowedUsers() {
        // Create user with empty followed users
        User user = new User("test@example.com", "testuser", "password");
        
        assertTrue(DataWriter.saveUsers(List.of(user), TEST_USER_FILE));
        
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(new FileReader(TEST_USER_FILE));
            JSONArray jsonUsers = (JSONArray) json.get("users");
            
            assertEquals(1, jsonUsers.size());
            JSONObject jsonUser = (JSONObject) jsonUsers.get(0);
            JSONArray followedUsers = (JSONArray) jsonUser.get("followedUsers");
            assertEquals(0, followedUsers.size());
        } catch (Exception e) {
            fail("Exception reading saved user file: " + e.getMessage());
        }
    }

    @Test
    public void testSaveUserWithFollowedUsers() {
        // Create users
        User user1 = new User("user1@example.com", "user1", "password1");
        User user2 = new User("user2@example.com", "user2", "password2");
        
        user1.followUser(user2);
        
        assertTrue(DataWriter.saveUsers(List.of(user1, user2), TEST_USER_FILE));
        
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(new FileReader(TEST_USER_FILE));
            JSONArray jsonUsers = (JSONArray) json.get("users");
            
            assertEquals(2, jsonUsers.size());
            
            // Find user1 in the JSON
            JSONObject jsonUser1 = null;
            for (Object obj : jsonUsers) {
                JSONObject jsonUser = (JSONObject) obj;
                if ("user1@example.com".equals(jsonUser.get("email"))) {
                    jsonUser1 = jsonUser;
                    break;
                }
            }
            
            assertNotNull("User1 not found in JSON", jsonUser1);
            JSONArray followedUsers = (JSONArray) jsonUser1.get("followedUsers");
            assertEquals(1, followedUsers.size());
        } catch (Exception e) {
            fail("Exception reading saved user file: " + e.getMessage());
        }
    }

    @Test
    public void testSaveSongWithExtremelyLongTitle() {
        // Create a song with a very long title (100,000 characters)
        StringBuilder longTitle = new StringBuilder();
        for (int i = 0; i < 100000; i++) {
            longTitle.append("a");
        }
        
        Song song = new Song(longTitle.toString(), "Test Composer", testUser);
        
        assertTrue(DataWriter.saveSongs(List.of(song), TEST_SONG_FILE));
        
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(new FileReader(TEST_SONG_FILE));
            JSONArray jsonSongs = (JSONArray) json.get("songs");
            assertEquals(1, jsonSongs.size());
            
            JSONObject jsonSong = (JSONObject) jsonSongs.get(0);
            assertEquals(longTitle.toString(), jsonSong.get("title"));
        } catch (Exception e) {
            fail("Exception with long title: " + e.getMessage());
        }
    }

    @Test
    public void testSaveSongWithSpecialCharactersInTitle() {
        // Create a song with special characters in the title
        String specialTitle = "Test <>&\"'\\/ Song with 你好 emoji 🎵🎶";
        Song song = new Song(specialTitle, "Test Composer", testUser);
        
        assertTrue(DataWriter.saveSongs(List.of(song), TEST_SONG_FILE));
        
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(new FileReader(TEST_SONG_FILE));
            JSONArray jsonSongs = (JSONArray) json.get("songs");
            
            JSONObject jsonSong = (JSONObject) jsonSongs.get(0);
            assertEquals(specialTitle, jsonSong.get("title"));
        } catch (Exception e) {
            fail("Exception with special characters: " + e.getMessage());
        }
    }

    @Test
    public void testSaveSongWithNegativeTempo() {
        // Create a song with a negative tempo
        Song song = createTestSong();
        
        // Get the first measure and set a negative tempo
        SheetMusic sheetMusic = song.getSheetMusic().get(0);
        Staff staff = sheetMusic.getStaves().get(0);
        Measure measure = staff.getMeasures().get(0);
        measure.setTempo(-120);
        
        assertTrue(DataWriter.saveSongs(List.of(song), TEST_SONG_FILE));
        
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(new FileReader(TEST_SONG_FILE));
            JSONArray jsonSongs = (JSONArray) json.get("songs");
            
            JSONObject jsonSong = (JSONObject) jsonSongs.get(0);
            JSONArray jsonSheetMusic = (JSONArray) jsonSong.get("sheetMusic");
            JSONObject firstSheet = (JSONObject) jsonSheetMusic.get(0);
            JSONArray jsonStaffs = (JSONArray) firstSheet.get("staffs");
            JSONObject firstStaff = (JSONObject) jsonStaffs.get(0);
            JSONArray jsonMeasures = (JSONArray) firstStaff.get("measures");
            JSONObject firstMeasure = (JSONObject) jsonMeasures.get(0);
            
            assertEquals(-120L, firstMeasure.get("tempo"));
        } catch (Exception e) {
            fail("Exception with negative tempo: " + e.getMessage());
        }
    }

    @Test
    public void testSaveSongWithCircularReference() {
        // Create a situation where User A follows User B who follows User A
        User userA = new User("userA@example.com", "userA", "password");
        User userB = new User("userB@example.com", "userB", "password");
        
        userA.followUser(userB);
        userB.followUser(userA);
        
        // Save users with circular reference
        assertTrue(DataWriter.saveUsers(List.of(userA, userB), TEST_USER_FILE));
        
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(new FileReader(TEST_USER_FILE));
            JSONArray jsonUsers = (JSONArray) json.get("users");
            assertEquals(2, jsonUsers.size());
        } catch (Exception e) {
            fail("Exception with circular reference: " + e.getMessage());
        }
    }

    // <-------------------TESTS EDGE CASES FOR ALL MUSIC ELEMENTS------------------->
    @Test
    public void testSaveSongWithModifiedNoteProperties() {
        // Create a song with a note
        Song song = createTestSong();
        
        // Get the first note and modify all its properties
        SheetMusic sheetMusic = song.getSheetMusic().get(0);
        Staff staff = sheetMusic.getStaves().get(0);
        Measure measure = staff.getMeasures().get(0);
        MusicElement element = measure.getMusicElements().get(0);
        
        if (element instanceof Note) {
            Note note = (Note) element;
            
            // Large unique values
            note.setPitch(445450.0);
            note.setMidiNumber(857438902);
            note.setPitch(10000000);
            note.setDuration(0.5);
            note.setDurationChar('@');
            note.setDotted(3/2);           
            note.setTied(false);
            note.setLyric("Test lyric");
        }
        
        assertTrue(DataWriter.saveSongs(List.of(song), TEST_SONG_FILE));
        
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(new FileReader(TEST_SONG_FILE));
            JSONArray jsonSongs = (JSONArray) json.get("songs");
            
            assertEquals(1, jsonSongs.size());
            
            // Navigate to the first note in the JSON
            JSONObject jsonSong = (JSONObject) jsonSongs.get(0);
            JSONArray jsonSheetMusic = (JSONArray) jsonSong.get("sheetMusic");
            JSONObject firstSheet = (JSONObject) jsonSheetMusic.get(0);
            JSONArray jsonStaffs = (JSONArray) firstSheet.get("staffs");
            JSONObject firstStaff = (JSONObject) jsonStaffs.get(0);
            JSONArray jsonMeasures = (JSONArray) firstStaff.get("measures");
            JSONObject firstMeasure = (JSONObject) jsonMeasures.get(0);
            JSONArray jsonElements = (JSONArray) firstMeasure.get("musicElements");
            JSONObject firstElement = (JSONObject) jsonElements.get(0);
            
            // Verify all properties were saved correctly
            assertEquals("note", firstElement.get("type"));
            assertEquals(440.0, ((Number)firstElement.get("pitch")).doubleValue(), 0.001);
            assertEquals(69L, firstElement.get("midiNumber"));
            assertEquals("A4", firstElement.get("pitchName"));
            assertEquals(0.5, ((Number)firstElement.get("duration")).doubleValue(), 0.001);
            assertEquals("h", firstElement.get("durationChar").toString());
            assertEquals(1L, firstElement.get("dotted"));
            assertEquals(true, firstElement.get("tied"));
            assertEquals("Test lyric", firstElement.get("lyric"));
            assertEquals("#", firstElement.get("accidental"));
        } catch (Exception e) {
            fail("Exception checking note properties: " + e.getMessage());
        }
    }

    @Test
    public void testSaveSongWithModifiedRestProperties() {
        // Create a song with a rest
        Song song = createTestSong();
        
        // Get the second element (which should be a rest)
        SheetMusic sheetMusic = song.getSheetMusic().get(0);
        Staff staff = sheetMusic.getStaves().get(0);
        Measure measure = staff.getMeasures().get(0);
        MusicElement element = measure.getMusicElements().get(1);
        
        if (element instanceof Rest) {
            Rest rest = (Rest) element;
            
            // Set extreme values
            rest.setDuration(99.99);
            rest.setDurationChar('X');
            rest.setDotted(5);
            rest.setTied(true);
        }
        
        assertTrue(DataWriter.saveSongs(List.of(song), TEST_SONG_FILE));
        
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(new FileReader(TEST_SONG_FILE));
            JSONArray jsonSongs = (JSONArray) json.get("songs");
            
            // Navigate to the rest in JSON
            JSONObject jsonSong = (JSONObject) jsonSongs.get(0);
            JSONArray jsonSheetMusic = (JSONArray) jsonSong.get("sheetMusic");
            JSONObject firstSheet = (JSONObject) jsonSheetMusic.get(0);
            JSONArray jsonStaffs = (JSONArray) firstSheet.get("staffs");
            JSONObject firstStaff = (JSONObject) jsonStaffs.get(0);
            JSONArray jsonMeasures = (JSONArray) firstStaff.get("measures");
            JSONObject firstMeasure = (JSONObject) jsonMeasures.get(0);
            JSONArray jsonElements = (JSONArray) firstMeasure.get("musicElements");
            JSONObject restElement = (JSONObject) jsonElements.get(1);
            
            // Verify properties
            assertEquals("rest", restElement.get("type"));
            assertEquals(99.99, ((Number)restElement.get("duration")).doubleValue(), 0.001);
            assertEquals("X", restElement.get("durationChar").toString());
            assertEquals(5L, restElement.get("dotted"));
            assertEquals(true, restElement.get("tied"));
            assertEquals("###", restElement.get("accidental"));
        } catch (Exception e) {
            fail("Exception checking rest properties: " + e.getMessage());
        }
    }

    @Test
    public void testSaveSongWithModifiedChordProperties() {
        // Create a song with a chord
        Song song = createTestSong();
        
        // Get the third element (which should be a chord)
        SheetMusic sheetMusic = song.getSheetMusic().get(0);
        Staff staff = sheetMusic.getStaves().get(0);
        Measure measure = staff.getMeasures().get(0);
        MusicElement element = measure.getMusicElements().get(2);
        
        if (element instanceof Chord) {
            Chord chord = (Chord) element;
                        
            // Modify notes within the chord
            List<Note> notes = chord.getNotes();
            if (!notes.isEmpty()) {
                // First note - extreme high values
                notes.get(0).setPitch(9999.99);
                notes.get(0).setMidiNumber(12765); 
                notes.get(0).setPitch(999999999);
                
                // Second note - extreme low values
                if (notes.size() > 1) {
                    notes.get(1).setPitch(0.01);
                    notes.get(1).setMidiNumber(0);
                    notes.get(1).setPitch(-999999999);
                }
            }
        }
        
        assertTrue(DataWriter.saveSongs(List.of(song), TEST_SONG_FILE));
        
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(new FileReader(TEST_SONG_FILE));
            JSONArray jsonSongs = (JSONArray) json.get("songs");
            
            // Navigate to the chord in JSON
            JSONObject jsonSong = (JSONObject) jsonSongs.get(0);
            JSONArray jsonSheetMusic = (JSONArray) jsonSong.get("sheetMusic");
            JSONObject firstSheet = (JSONObject) jsonSheetMusic.get(0);
            JSONArray jsonStaffs = (JSONArray) firstSheet.get("staffs");
            JSONObject firstStaff = (JSONObject) jsonStaffs.get(0);
            JSONArray jsonMeasures = (JSONArray) firstStaff.get("measures");
            JSONObject firstMeasure = (JSONObject) jsonMeasures.get(0);
            JSONArray jsonElements = (JSONArray) firstMeasure.get("musicElements");
            JSONObject chordElement = (JSONObject) jsonElements.get(2);
            
            // Verify chord properties
            assertEquals("chord", chordElement.get("type"));
            assertEquals("b♭", chordElement.get("accidental"));
            
            // Verify notes within chord
            JSONArray jsonNotes = (JSONArray) chordElement.get("notes");
            assertTrue(jsonNotes.size() > 0);
            
            // First note
            JSONObject firstNote = (JSONObject) jsonNotes.get(0);
            assertEquals(9999.99, ((Number)firstNote.get("pitch")).doubleValue(), 0.001);
            assertEquals(127L, firstNote.get("midiNumber"));
            assertEquals("ULTRA_HIGH", firstNote.get("pitchName"));
            
            // Second note if it exists
            if (jsonNotes.size() > 1) {
                JSONObject secondNote = (JSONObject) jsonNotes.get(1);
                assertEquals(0.01, ((Number)secondNote.get("pitch")).doubleValue(), 0.001);
                assertEquals(0L, secondNote.get("midiNumber"));
                assertEquals("ULTRA_LOW", secondNote.get("pitchName"));
            }
        } catch (Exception e) {
            fail("Exception checking chord properties: " + e.getMessage());
        }
    }

    @Test
    public void testSaveSongWithModifiedTupletProperties() {
        // Create a song with a tuplet
        Song song = createTestSong();

        // Get the fourth element (which should be a tuplet)
        SheetMusic sheetMusic = song.getSheetMusic().get(0);
        Staff staff = sheetMusic.getStaves().get(0);
        Measure measure = staff.getMeasures().get(0);
        MusicElement element = measure.getMusicElements().get(3);

        if (element instanceof Tuplet) {
            Tuplet tuplet = (Tuplet) element;

            tuplet.setDuration(0.0001);

            // Modify elements within tuplet
            List<MusicElement> tupletElements = tuplet.getElements();
            if (!tupletElements.isEmpty() && tupletElements.get(0) instanceof Note) {
                Note note = (Note) tupletElements.get(0);
                note.setDuration(0.00001);
                note.setMidiNumber(999);
        }

        assertTrue(DataWriter.saveSongs(List.of(song), TEST_SONG_FILE));

        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(new FileReader(TEST_SONG_FILE));
            JSONArray jsonSongs = (JSONArray) json.get("songs");

            // Navigate to the tuplet in JSON
            JSONObject jsonSong = (JSONObject) jsonSongs.get(0);
            JSONArray jsonSheetMusic = (JSONArray) jsonSong.get("sheetMusic");
            JSONObject firstSheet = (JSONObject) jsonSheetMusic.get(0);
            JSONArray jsonStaffs = (JSONArray) firstSheet.get("staffs");
            JSONObject firstStaff = (JSONObject) jsonStaffs.get(0);
            JSONArray jsonMeasures = (JSONArray) firstStaff.get("measures");
            JSONObject firstMeasure = (JSONObject) jsonMeasures.get(0);
            JSONArray jsonElements = (JSONArray) firstMeasure.get("musicElements");
            JSONObject tupletElement = (JSONObject) jsonElements.get(3);

            // Verify tuplet properties
            assertEquals("tuplet", tupletElement.get("type"));
            assertEquals(99L, tupletElement.get("numerator"));
            assertEquals(1L, tupletElement.get("denominator"));
            assertEquals(0.0001, ((Number)tupletElement.get("duration")).doubleValue(), 0.00001);

            // Verify elements within tuplet
            JSONArray jsonTupletElements = (JSONArray) tupletElement.get("elements");
            assertTrue(jsonTupletElements.size() > 0);

            // First element (if it's a note)
            JSONObject firstElement = (JSONObject) jsonTupletElements.get(0);
            if ("note".equals(firstElement.get("type"))) {
                assertEquals(0.00001, ((Number)firstElement.get("duration")).doubleValue(), 0.000001);
                assertEquals(999L, firstElement.get("midiNumber"));
            }
        } catch (Exception e) {
            fail("Exception checking tuplet properties: " + e.getMessage());
        }
    }
 }

    
    // <-------------------HELPERS TO CREATE BASIC SONGS-------------------> 
    // Helper method to create a test song with all necessary components
    private Song createTestSong() {
        Song song = new Song("Test Song", "Test Composer", testUser);
        song.setPickUp(0);
        
        List<String> clefs = new ArrayList<>();
        clefs.add("Treble");
        Instrument instrument = new Instrument(clefs, "Piano");
        
        SheetMusic sheetMusic = new SheetMusic(instrument);
        
        Staff staff = new Staff("Treble", new ArrayList<>());
        
        Measure measure = new Measure(0, 4, 4, 120, new ArrayList<>());
        
        Note note = new Note(261.63, 60, "C4", 0.25, 'q', 0, false, "");
        
        Rest rest = new Rest(0.25, 'q', 0, false, "");
        
        List<Note> chordNotes = new ArrayList<>();
        Note chordNote1 = new Note(261.63, 60, "C4", 0.25, 'q', 0, false, "");
        Note chordNote2 = new Note(329.63, 64, "E4", 0.25, 'q', 0, false, "");
        chordNotes.add(chordNote1);
        chordNotes.add(chordNote2);
        Chord chord = new Chord("", chordNotes);
        
        List<MusicElement> tupletElements = new ArrayList<>();
        Note tupletNote1 = new Note(293.66, 62, "D4", 0.125, 'e', 0, false, "");
        Note tupletNote2 = new Note(329.63, 64, "E4", 0.125, 'e', 0, false, "");
        Note tupletNote3 = new Note(349.23, 65, "F4", 0.125, 'e', 0, false, "");
        tupletElements.add(tupletNote1);
        tupletElements.add(tupletNote2);
        tupletElements.add(tupletNote3);
        Tuplet tuplet = new Tuplet(3, 2, 0.375, tupletElements);
        
        measure.addMusicElement(note);
        measure.addMusicElement(rest);
        measure.addMusicElement(chord);
        measure.addMusicElement(tuplet);
        
        staff.addMeasure(measure);
        
        sheetMusic.addStaff(staff);
        
        song.addSheetMusic(sheetMusic);
        
        return song;
    }
}
