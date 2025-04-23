package com.data;

import com.model.Chord;
import com.model.DataConstants;
import com.model.Instrument;
import com.model.Measure;
import com.model.MusicElement;
import com.model.Note;
import com.model.ParsedSong;
import com.model.Rest;
import com.model.SheetMusic;
import com.model.Song;
import com.model.Staff;
import com.model.Tuplet;
import java.util.List;
import java.util.UUID;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.ArrayList;

/**
 * Parser for converting JSON content into ParsedSong objects.
 */
public class SongJsonParser extends DataConstants {

  /**
   * Parses the given JSON content and returns a list of ParsedSong objects.
   *
   * @param jsonContent the JSON content as a String
   * @return a list of ParsedSong objects
   * @throws ParseException if parsing the JSON fails
   */
  public List<ParsedSong> getParsedSongs(String jsonContent) throws ParseException {
    List<ParsedSong> parsedSongs = new ArrayList<>();
    JSONParser parser = new JSONParser();
    JSONObject jsonObject = (JSONObject) parser.parse(jsonContent);
    JSONArray songsJsonArray = getValue(jsonObject, SONG_OBJECT_KEY, JSONArray.class);
    for (Object songObject : songsJsonArray) {
      parsedSongs.add(getParsedSong(songObject));
    }
    return parsedSongs;
  }

  /**
   * Converts a JSON object representing a song into a ParsedSong.
   *
   * @param aSong the JSON object for the song
   * @return a ParsedSong object
   */
  private ParsedSong getParsedSong(Object aSong) {
    JSONObject songJSON = (JSONObject) aSong;
    UUID id = UUID.fromString(getValue(songJSON, SONG_ID, String.class));
    String title = getValue(songJSON, SONG_TITLE, String.class);
    String composer = getValue(songJSON, SONG_COMPOSER, String.class);
    UUID publisherID = UUID.fromString(getValue(songJSON, SONG_PUBLISHER, String.class));
    int pickUp = getValue(songJSON, SONG_PICK_UP, Number.class).intValue();

    List<String> genre = new ArrayList<>();
    try {
      JSONArray genreJSONArray = getValue(songJSON, SONG_GENRE, JSONArray.class);
      for (Object genreObject : genreJSONArray) {
        genre.add(getGenres(genreObject));
      }
    } catch (IllegalArgumentException e) {}

    List<SheetMusic> sheetMusic = new ArrayList<>();
    JSONArray sheetMusicJSONArray = getValue(songJSON, SONG_SHEET_MUSIC, JSONArray.class);
    for (Object sheetMusicObject : sheetMusicJSONArray) {
      sheetMusic.add(getSheetMusic(sheetMusicObject));
    }
    Song song = new Song(id, title, composer, pickUp, sheetMusic);
    song.setGenres(genre);
    return new ParsedSong(song, publisherID);
  }

  private String getGenres(Object genres) {
    if (genres instanceof String) {
      return (String) genres;
    } else {
      JSONObject genreJSON = (JSONObject) genres;
      return getValue(genreJSON, "name", String.class);
    }
  }
  
  /**
   * Parses a JSON object representing sheet music and returns a SheetMusic object.
   *
   * @param aSheetMusic the JSON object for sheet music
   * @return a SheetMusic object
   */
  private SheetMusic getSheetMusic(Object aSheetMusic) {
    JSONObject sheetMusicJSON = (JSONObject) aSheetMusic;
    JSONObject instrumentJSON = getValue(sheetMusicJSON, SONG_INSTRUMENT, JSONObject.class);
    String instrumentName = getValue(instrumentJSON, SONG_INSTRUMENT_NAME, String.class);
    JSONArray clefTypesJSON = getValue(instrumentJSON, SONG_INSTRUMENT_CLEF_TYPES, JSONArray.class);
    List<String> clefTypes = new ArrayList<>();
    if (clefTypesJSON != null) {
      for (Object clefTypeObject : clefTypesJSON) {
        clefTypes.add((String) clefTypeObject);
      }
    }
    Instrument instrument = new Instrument(clefTypes, instrumentName);
    List<Staff> staves = new ArrayList<>();
    JSONArray stavesJSON = getValue(sheetMusicJSON, SONG_STAVES, JSONArray.class);
    for (Object staffObject : stavesJSON) {
      staves.add(getStaff(staffObject));
    }
    return new SheetMusic(instrument, staves);
  }

  /**
   * Parses a JSON object representing a staff and returns a Staff object.
   *
   * @param staff the JSON object for the staff
   * @return a Staff object
   */
  private Staff getStaff(Object staff) {
    JSONObject staffJSON = (JSONObject) staff;
    String clefType = getValue(staffJSON, SONG_STAFF_CLEF_TYPE, String.class);
    List<Measure> measures = new ArrayList<>();
    JSONArray measuresJSON = getValue(staffJSON, SONG_MEASURES, JSONArray.class);
    for (Object measureObject : measuresJSON) {
      measures.add(getMeasure(measureObject));
    }
    return new Staff(clefType, measures);
  }

  /**
   * Parses a JSON object representing a measure and returns a Measure object.
   *
   * @param aMeasure the JSON object for the measure
   * @return a Measure object
   */
  private Measure getMeasure(Object aMeasure) {
    JSONObject measureJSON = (JSONObject) aMeasure;
    int keySignature = getValue(measureJSON, SONG_MEASURES_KEY_SIGNATURE, Number.class).intValue();
    int timeSignatureNumerator = getValue(measureJSON, SONG_MEASURES_TIME_SIGNATURE_NUMERATOR, Number.class).intValue();
    int timeSignatureDenominator = getValue(measureJSON, SONG_MEASURES_TIME_SIGNATURE_DENOMINATOR, Number.class).intValue();
    int tempo = getValue(measureJSON, SONG_MEASURES_TEMPO, Number.class).intValue();
    List<MusicElement> musicElements = new ArrayList<>();
    JSONArray musicElementsJSON = getValue(measureJSON, SONG_MUSIC_ELEMENTS, JSONArray.class);
    for (Object musicElementObject : musicElementsJSON) {
      musicElements.add(getMusicElement(musicElementObject));
    }
    return new Measure(keySignature, timeSignatureNumerator, timeSignatureDenominator, tempo, musicElements);
  }

  /**
   * Parses a JSON object representing a music element and returns the corresponding MusicElement object.
   *
   * @param musicElement the JSON object for the music element
   * @return a MusicElement object corresponding to the JSON input
   */
  private MusicElement getMusicElement (Object musicElement){
    JSONObject musicElementJSON = (JSONObject) musicElement;
    String type = getValue(musicElementJSON, SONG_MUSIC_ELEMENT_TYPE, String.class);
    return switch (type) {
      case SONG_MUSIC_ELEMENT_REST -> getRest(musicElementJSON);
      case SONG_MUSIC_ELEMENT_TUPLET -> getTuplet(musicElementJSON);
      case SONG_MUSIC_ELEMENT_NOTE -> getNote(musicElementJSON);
      case SONG_MUSIC_ELEMENT_CHORD -> getChord(musicElementJSON);
      default -> throw new IllegalArgumentException("Unknown music element type: " + type);
    };
  }

  /**
   * Parses a JSON object representing a rest and returns a Rest object.
   *
   * @param musicElementJSON the JSON object for the rest
   * @return a Rest object
   */
  private Rest getRest(JSONObject musicElementJSON) {
    double duration = getValue(musicElementJSON, SONG_MUSIC_ELEMENT_DURATION, Number.class).doubleValue();
    char durationChar = getValue(musicElementJSON, SONG_MUSIC_ELEMENT_DURATION_CHAR, String.class).charAt(0);
    int dotted = getValue(musicElementJSON, SONG_MUSIC_ELEMENT_DOTTED, Number.class).intValue();
    boolean tied = getValue(musicElementJSON, SONG_MUSIC_ELEMENT_TIED, Boolean.class);
    String lyric = getValue(musicElementJSON, SONG_MUSIC_ELEMENT_LYRIC, String.class);
    return new Rest(duration, durationChar, dotted, tied, lyric);
  }

  /**
   * Parses a JSON object representing a tuplet and returns a Tuplet object.
   *
   * @param musicElementJSON the JSON object for the tuplet
   * @return a Tuplet object
   */
  private Tuplet getTuplet(JSONObject musicElementJSON) {
    int subdivisions = getValue(musicElementJSON, SONG_MUSIC_ELEMENT_SUBDIVISIONS, Number.class).intValue();
    int impliedDivision = getValue(musicElementJSON, SONG_MUSIC_ELEMENT_IMPLIED_DIVISION, Number.class).intValue();
    double duration = getValue(musicElementJSON, SONG_MUSIC_ELEMENT_DURATION, Number.class).doubleValue();
    List<MusicElement> elements = new ArrayList<>();
    JSONArray tupletElementsJSON = getValue(musicElementJSON, SONG_MUSIC_ELEMENT_TUPLET_ELEMENTS, JSONArray.class);
    for (Object musicElementObject : tupletElementsJSON) {
      elements.add(getMusicElement(musicElementObject));
    }
    return new Tuplet(subdivisions, impliedDivision, duration, elements);
  }

  /**
   * Parses a JSON object representing a note and returns a Note object.
   *
   * @param musicElementJSON the JSON object for the note
   * @return a Note object
   */
  private Note getNote(JSONObject musicElementJSON) {
    double pitch = getValue(musicElementJSON, SONG_MUSIC_ELEMENT_PITCH, Number.class).doubleValue();
    int midiNumber = getValue(musicElementJSON, SONG_MUSIC_ELEMENT_MIDI_NUMBER, Number.class).intValue();
    String noteName = getValue(musicElementJSON, SONG_MUSIC_ELEMENT_NOTE_NAME, String.class);
    double duration = getValue(musicElementJSON, SONG_MUSIC_ELEMENT_DURATION, Number.class).doubleValue();
    char durationChar = getValue(musicElementJSON, SONG_MUSIC_ELEMENT_DURATION_CHAR, String.class).charAt(0);
    int dotted = getValue(musicElementJSON, SONG_MUSIC_ELEMENT_DOTTED, Number.class).intValue();
    boolean tied = getValue(musicElementJSON, SONG_MUSIC_ELEMENT_TIED, Boolean.class);
    String lyric = getValue(musicElementJSON, SONG_MUSIC_ELEMENT_LYRIC, String.class);
    return new Note(pitch, midiNumber, noteName, duration, durationChar, dotted, tied, lyric);
  }

  /**
   * Parses a JSON object representing a chord and returns a Chord object.
   *
   * @param musicElementJSON the JSON object for the chord
   * @return a Chord object
   */
  private Chord getChord(JSONObject musicElementJSON) {
    String chordLyric = (String) getValue(musicElementJSON, SONG_MUSIC_ELEMENT_LYRIC, String.class);
    List<Note> notes = new ArrayList<>();
    JSONArray chordNotes = getValue(musicElementJSON, SONG_MUSIC_ELEMENT_CHORD_NOTES, JSONArray.class);
    for (Object noteObject : chordNotes) {
      JSONObject chordNoteJSON = (JSONObject) noteObject;
      notes.add(getNote(chordNoteJSON));
    }
    return new Chord(chordLyric, notes);
  }

  /**
   * Retrieves a value from a JSON object with the specified key and casts it to the desired class.
   *
   * @param <T>   the type of the expected value
   * @param object the JSON object from which to retrieve the value
   * @param key    the key for the value
   * @param clazz  the Class object corresponding to the desired type
   * @return the value associated with the key cast to the desired type
   * @throws IllegalArgumentException if the key is missing or the value is not of the expected type
   */
  private <T> T getValue(JSONObject object, String key, Class<T> clazz) {
    Object value = object.get(key);
    if(value == null)
      throw new IllegalArgumentException("Missing key: " + key);
    try {
      return clazz.cast(value);
    } catch (ClassCastException e) {
      throw new IllegalArgumentException("Expected key '" + key + "' to be of type "
          + clazz.getName() + " but found " + value.getClass().getName(), e);
    }
  }
}
