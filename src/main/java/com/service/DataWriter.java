package com.service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.model.Chord;
import com.model.DataConstants;
import com.model.Measure;
import com.model.MusicElement;
import com.model.Note;
import com.model.Rest;
import com.model.SheetMusic;
import com.model.Song;
import com.model.Staff;
import com.model.Tuplet;
import com.model.User;
/**
 * Class that writes data to JSON.
 * 
 * @author Joshua Gould
 */
@SuppressWarnings("unchecked")
public class DataWriter extends DataConstants {
    /**
     * Writes user data to JSON.
     * 
     * @return True or false depending on success of write.
     */
    public static boolean saveUsers(List<User> users) {
        try {
            // Create the root JSON object with "users" key
            JSONObject root = new JSONObject();
            JSONArray jsonUsers = new JSONArray();
            
            // Add each user to the JSON array
            for (User user : users) {
                jsonUsers.add(getUserJSON(user));
            }
            
            // Add the users array to the root object
            root.put("users", jsonUsers);
            
            // try (FileWriter file = new FileWriter(USER_FILE_LOCATION)) { uncomment this when done testing
            try (FileWriter file = new FileWriter("src/main/java/com/data/TESTusers.json")) {  // delete this when done testing
                file.write(root.toJSONString());
                file.flush();
                return true;
            }
        } catch (IOException e) {
            System.err.println("Failed to save users to JSON file:");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Writes song data to JSON.
     * 
     * @return True or false depending on success of write.
     */
    public static boolean saveSongs(List<Song> songs) {
        try {
            JSONObject root = new JSONObject();
            JSONArray jsonSongs = new JSONArray();
            
            // Add each song to the JSON array
            for (Song song : songs) {
                jsonSongs.add(getSongJSON(song));
            }

            root.put("songs", jsonSongs);

            // try (FileWriter file = new FileWriter(SONG_FILE_LOCATION)) { uncomment this when done testing
            try (FileWriter file = new FileWriter("src/main/java/com/data/TESTsongs.json")) {  // delete this when done testing
                file.write(root.toJSONString());
                file.flush();
                return true;
            }
        } catch (IOException e) {
            System.err.println("Failed to save songs to JSON file:");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * For each data variable, it gets added to a JSONObject to be written in users.json
     * 
     * @param user user that is saving their data
     * @return     a JSONObject of user details
     */
    private static JSONObject getUserJSON(User user) {
        JSONObject userDetails = new JSONObject();
        JSONArray favoriteSongs = new JSONArray();
        JSONArray followedUsers = new JSONArray();
        
        for (Song favoriteSong : user.getFavoriteSongs()) {
            favoriteSongs.add(favoriteSong.getId().toString());
        }    
        for (User followedUser : user.getFollowedUsers()) {
            followedUsers.add(followedUser.getId().toString());
        }
        
        userDetails.put(USER_ID, user.getId().toString());
        userDetails.put(USER_EMAIL, user.getEmail());
        userDetails.put(USER_USERNAME, user.getUsername());
        userDetails.put(USER_PASSWORD, user.getPassword());
        userDetails.put(USER_FAVORITED_SONGS, favoriteSongs);
        userDetails.put(USER_FOLLOWED_USERS, followedUsers);
        userDetails.put(USER_THEME_COLOR, user.getThemeColor().name());

        return userDetails;
    }

    /**
     * For each data variable, it gets added to a JSONObject to be written in songs.json
     * 
     * @param song The song to convert to JSON
     * @return A JSONObject representing the song
     */
    private static JSONObject getSongJSON(Song song) {
        JSONObject songDetails = new JSONObject();
        
        // Basic song details
        songDetails.put(SONG_ID, song.getId().toString());
        songDetails.put(SONG_TITLE, song.getTitle());
        songDetails.put(SONG_COMPOSER, song.getComposer());
        songDetails.put(SONG_PUBLISHER, song.getPublisher().getId().toString());
        songDetails.put(SONG_PICK_UP, song.getPickUp());

        // Create sheet music array
        JSONArray sheetMusicJSON = new JSONArray();
        songDetails.put(SONG_SHEET_MUSIC, sheetMusicJSON);
        
        // Process each sheet music entry
        for (SheetMusic sheet : song.getSheetMusic()) {
            JSONObject sheetMusicEntry = new JSONObject();
            sheetMusicJSON.add(sheetMusicEntry);
            
            // Add instrument
            JSONObject instrumentJSON = new JSONObject();
            instrumentJSON.put(SONG_INSTRUMENT_NAME, sheet.getInstrument().getInstrumentName());
            
            // Add clef types
            JSONArray clefTypeJSON = new JSONArray();
            for (String clefType : sheet.getInstrument().getClefTypes()) {
                clefTypeJSON.add(clefType);
            }
            instrumentJSON.put(SONG_INSTRUMENT_CLEF_TYPES, clefTypeJSON);
            
            // Add instrument to sheet music entry
            sheetMusicEntry.put(SONG_INSTRUMENT, instrumentJSON);
            
            // Process staves
            JSONArray stavesArray = new JSONArray();
            sheetMusicEntry.put(SONG_STAVES, stavesArray);
            
            for (Staff staff : sheet.getStaves()) {
                // Add staff details
                JSONObject staveJSON = new JSONObject();
                staveJSON.put(SONG_STAFF_CLEF_TYPE, staff.getClefType());
                stavesArray.add(staveJSON);
                
                // Process measures
                JSONArray measuresArray = new JSONArray();
                staveJSON.put(SONG_MEASURES, measuresArray);
                
                for (Measure measure : staff.getMeasures()) {
                    JSONObject measureJSON = new JSONObject();
                    measureJSON.put(SONG_MEASURES_KEY_SIGNATURE, measure.getKeySignature());
                    measureJSON.put(SONG_MEASURES_TIME_SIGNATURE_NUMERATOR, measure.getTimeSignatureNumerator());
                    measureJSON.put(SONG_MEASURES_TIME_SIGNATURE_DENOMINATOR, measure.getTimeSignatureDenominator());
                    measureJSON.put(SONG_MEASURES_TEMPO, measure.getTempo());
                    
                    // Process music elements
                    JSONArray musicElementsArray = new JSONArray();
                    measureJSON.put(SONG_MUSIC_ELEMENTS, musicElementsArray);
                    
                    for (MusicElement element : measure.getMusicElements()) {
                        // Process each music element
                        JSONObject elementJSON = getMusicElementByType(element.getType(), element);
                        musicElementsArray.add(elementJSON);
                    }
                    measuresArray.add(measureJSON);
                }
            }
        }
        
        return songDetails;
    }

    /**
     * For the case of a note element, it gets added to a JSONObject to be written in the music elements array
     * 
     * @param element The music element to convert to JSON
     * @return A JSONObject representing the music element of notes
     */
    private static JSONObject getNoteJSON(MusicElement element) {
        Note note = (Note) element; 
        JSONObject noteJSON = new JSONObject();
        noteJSON.put(SONG_MUSIC_ELEMENT_TYPE, note.getType());
        noteJSON.put(SONG_MUSIC_ELEMENT_PITCH, note.getPitch());
        noteJSON.put(SONG_MUSIC_ELEMENT_MIDI_NUMBER, note.getMidiNumber());
        noteJSON.put(SONG_MUSIC_ELEMENT_NOTE_NAME, note.getNoteName());
        noteJSON.put(SONG_MUSIC_ELEMENT_DURATION, note.getDuration());
        noteJSON.put(SONG_MUSIC_ELEMENT_DURATION_CHAR, note.getDurationChar());
        noteJSON.put(SONG_MUSIC_ELEMENT_DOTTED, note.getDotted());
        noteJSON.put(SONG_MUSIC_ELEMENT_TIED, note.hasTie());
        noteJSON.put(SONG_MUSIC_ELEMENT_LYRIC, note.getLyric());
        return noteJSON;
    }

    /**
     * For the case of a rest element, it gets added to a JSONObject to be written in the music elements array
     * 
     * @param element The music element to convert to JSON
     * @return A JSONObject representing the music element of rests
     */
    private static JSONObject getRestJSON(MusicElement element) {
        Rest rest = (Rest) element;
        JSONObject restJSON = new JSONObject();
        restJSON.put(SONG_MUSIC_ELEMENT_TYPE, rest.getType());
        restJSON.put(SONG_MUSIC_ELEMENT_DURATION, rest.getDuration());
        restJSON.put(SONG_MUSIC_ELEMENT_DURATION_CHAR, rest.getDurationChar());
        restJSON.put(SONG_MUSIC_ELEMENT_DOTTED, rest.getDotted());
        restJSON.put(SONG_MUSIC_ELEMENT_TIED, rest.hasTie());
        restJSON.put(SONG_MUSIC_ELEMENT_LYRIC, rest.getLyric());
        return restJSON;
    }

    /**
     * For the case of a chord element, it gets added to a JSONObject to be written in the music elements array
     * 
     * @param element The music element to convert to JSON
     * @return A JSONObject representing the music element of chords
     */
    private static JSONObject getChordJSON(MusicElement element) {
        Chord chord = (Chord) element;
        JSONObject chordJSON = new JSONObject();
        chordJSON.put(SONG_MUSIC_ELEMENT_TYPE, chord.getType());
        chordJSON.put(SONG_MUSIC_ELEMENT_LYRIC, chord.getLyric());

        JSONArray notesJSON = new JSONArray();
        for (Note note : chord.getNotes()) {
            JSONObject noteJSON = new JSONObject();
            noteJSON.put(SONG_MUSIC_ELEMENT_PITCH, note.getPitch());
            noteJSON.put(SONG_MUSIC_ELEMENT_MIDI_NUMBER, note.getMidiNumber());
            noteJSON.put(SONG_MUSIC_ELEMENT_NOTE_NAME, note.getNoteName());
            noteJSON.put(SONG_MUSIC_ELEMENT_DURATION, note.getDuration());
            noteJSON.put(SONG_MUSIC_ELEMENT_DURATION_CHAR, note.getDurationChar());
            noteJSON.put(SONG_MUSIC_ELEMENT_DOTTED, note.getDotted());
            noteJSON.put(SONG_MUSIC_ELEMENT_TIED, note.hasTie());
            noteJSON.put(SONG_MUSIC_ELEMENT_LYRIC, note.getLyric());
            notesJSON.add(noteJSON);
        }
        chordJSON.put(SONG_MUSIC_ELEMENT_CHORD_NOTES, notesJSON);
        return chordJSON;
    }

    /**
     * For the case of a tuplet element, it gets added to a JSONObject to be written in the music elements array
     * 
     * @param element The music element to convert to JSON
     * @return A JSONObject representing the music element of tuplets
     */
    private static JSONObject getTupletJSON(MusicElement element) {
        Tuplet tuplet = (Tuplet) element;
        JSONObject tupletJSON = new JSONObject();
        tupletJSON.put(SONG_MUSIC_ELEMENT_TYPE, tuplet.getType());
        tupletJSON.put(SONG_MUSIC_ELEMENT_SUBDIVISIONS, tuplet.getSubdivisions());
        tupletJSON.put(SONG_MUSIC_ELEMENT_IMPLIED_DIVISION, tuplet.getImpliedDivision());
        tupletJSON.put(SONG_MUSIC_ELEMENT_DURATION, tuplet.getDuration());

        JSONArray elementsJSON = new JSONArray();
        for (MusicElement tupletElement : tuplet.getElements()) {
            JSONObject tupletElementJSON = getMusicElementByType(tupletElement.getType(), tupletElement);
            elementsJSON.add(tupletElementJSON);
        }
        tupletJSON.put(SONG_MUSIC_ELEMENTS, elementsJSON);
        return tupletJSON;
    }

    /**
     * Returns a JSON object for the given music element based on its type.
     * 
     * @param type    The type of the music element
     * @param element The music element to convert
     * @return        A JSONObject representing the music element
     */
    private static JSONObject getMusicElementByType(String type, MusicElement element) {
        JSONObject musicElementJSON = switch (type) {
            case SONG_MUSIC_ELEMENT_REST -> getRestJSON(element);
            case SONG_MUSIC_ELEMENT_TUPLET -> getTupletJSON(element);
            case SONG_MUSIC_ELEMENT_CHORD -> getChordJSON(element);
            case SONG_MUSIC_ELEMENT_NOTE -> getNoteJSON(element);
            default -> throw new IllegalArgumentException("Invalid music element type: " + type);
        };
        return musicElementJSON;
    }
}
