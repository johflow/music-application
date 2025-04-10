package com.service;

import com.model.DataConstants;
import javax.xml.crypto.Data;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class JFugueToJsonConverter extends DataConstants {

  /**
   * Main method which takes a JFugue string as input and converts it to a JSON file.
   * Usage: java JFugueToJsonConverter "C5q D5q E5q F5q"
   *
   * @param args the command-line arguments (expects the JFugue string as the first argument)
   */
  public static void main(String[] args) {
    if (args.length < 1) {
      System.out.println("Usage: java JFugueToJsonConverter \"<JFugue string>\"");
      return;
    }

    // Get the JFugue string from command-line arguments
    String jfugueString = args[0];
    // Split the string into tokens (each token assumed to be a note)
    String[] tokens = jfugueString.split("\\s+");

    // Create JSON array for music elements (notes)
    JSONArray musicElements = new JSONArray();
    for (String token : tokens) {
      // For this simple example, we assume the token format is like "C5q":
      // - The last character indicates duration (e.g., 'q' for quarter note).
      // - The rest is taken as the note name.
      // This can be expanded to perform more detailed parsing.
      String noteName = token.substring(0, token.length() - 1);
      char durationChar = token.charAt(token.length() - 1);

      // Default/fake values for fields not directly available in the JFugue string.
      // You can improve this logic with proper parsing and conversion (e.g., computing MIDI numbers).
      double pitch = 0.0; // Placeholder value
      int midiNumber = 60; // A common default (Middle C)
      double duration = 1.0; // Default duration value (for a quarter note, for example)
      int dotted = 0;
      boolean tied = false;
      String lyric = "";

      // Build the JSON object for a note using keys from DataConstants.
      JSONObject noteJson = new JSONObject();
      noteJson.put(DataConstants.SONG_MUSIC_ELEMENT_NOTE, DataConstants.SONG_MUSIC_ELEMENT_NOTE);
      noteJson.put(DataConstants.SONG_MUSIC_ELEMENT_NOTE_NAME, noteName);
      noteJson.put(DataConstants.SONG_MUSIC_ELEMENT_DURATION, duration);
      noteJson.put(DataConstants.SONG_MUSIC_ELEMENT_DURATION_CHAR, String.valueOf(durationChar));
      noteJson.put(DataConstants.SONG_MUSIC_ELEMENT_DOTTED, dotted);
      noteJson.put(DataConstants.SONG_MUSIC_ELEMENT_TIED, tied);
      noteJson.put(DataConstants.SONG_MUSIC_ELEMENT_LYRIC, lyric);
      noteJson.put(DataConstants.SONG_MUSIC_ELEMENT_MIDI_NUMBER, midiNumber);
      noteJson.put(DataConstants.SONG_MUSIC_ELEMENT_PITCH, pitch);

      musicElements.add(noteJson);
    }

    // Build a measure JSON object with default time signature and tempo.
    JSONObject measureJson = new JSONObject();
    measureJson.put(DataConstants.SONG_MEASURES_KEY_SIGNATURE, 0);
    measureJson.put(DataConstants.SONG_MEASURES_TIME_SIGNATURE_NUMERATOR, 4);
    measureJson.put(DataConstants.SONG_MEASURES_TIME_SIGNATURE_DENOMINATOR, 4);
    measureJson.put(DataConstants.SONG_MEASURES_TEMPO, 120);
    measureJson.put(DataConstants.SONG_MUSIC_ELEMENTS, musicElements);

    // Build a staff JSON object. Here we use a default clef type.
    JSONArray measuresArray = new JSONArray();
    measuresArray.add(measureJson);

    JSONObject staffJson = new JSONObject();
    staffJson.put(DataConstants.SONG_STAFF_CLEF_TYPE, "treble");
    staffJson.put(DataConstants.SONG_MEASURES, measuresArray);

    // Build an instrument JSON object.
    JSONArray clefTypes = new JSONArray();
    clefTypes.add("treble");

    JSONObject instrumentJson = new JSONObject();
    instrumentJson.put(DataConstants.SONG_INSTRUMENT_NAME, "Piano");
    instrumentJson.put(DataConstants.SONG_INSTRUMENT_CLEF_TYPES, clefTypes);

    // Build the sheet music JSON object.
    JSONArray stavesArray = new JSONArray();
    stavesArray.add(staffJson);

    JSONObject sheetMusicJson = new JSONObject();
    sheetMusicJson.put(DataConstants.SONG_INSTRUMENT, instrumentJson);
    sheetMusicJson.put(DataConstants.SONG_STAVES, stavesArray);

    JSONArray sheetMusicArray = new JSONArray();
    sheetMusicArray.add(sheetMusicJson);

    // Build the song JSON object.
    JSONObject songJson = new JSONObject();
    songJson.put(DataConstants.SONG_ID, UUID.randomUUID().toString());
    songJson.put(DataConstants.SONG_TITLE, "Converted Song");
    songJson.put(DataConstants.SONG_COMPOSER, "Unknown");
    songJson.put(DataConstants.SONG_PUBLISHER, UUID.randomUUID().toString());
    songJson.put(DataConstants.SONG_PICK_UP, 0);
    songJson.put(DataConstants.SONG_SHEET_MUSIC, sheetMusicArray);

    // Wrap the song in the songs array.
    JSONArray songsArray = new JSONArray();
    songsArray.add(songJson);

    JSONObject outputJson = new JSONObject();
    outputJson.put(DataConstants.SONG_OBJECT_KEY, songsArray);

    // Write the JSON object to a file called "song.json".
    try (FileWriter file = new FileWriter("song.json")) {
      file.write(outputJson.toJSONString());
      file.flush();
      System.out.println("JSON file created: song.json");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
