package com.data;

import com.model.Note;
import com.model.ParsedSong;
import com.model.SheetMusic;
import com.model.Song;
import com.model.Staff;
import com.model.Measure;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;

/**
 * JUnit tests for the SongJsonParser class.
 */
public class SongJsonParserTest {

  // A valid JSON string containing one song with nested sheet music, staff, measure, and one note.
  private static final String VALID_SONG_JSON = "{"
      + "\"songs\": ["
      + "  {"
      + "    \"id\": \"aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa\","
      + "    \"title\": \"Test Song\","
      + "    \"composer\": \"Test Composer\","
      + "    \"publisher\": \"bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb\","
      + "    \"pickUp\": 4,"
      + "    \"sheetMusic\": ["
      + "      {"
      + "        \"instrument\": {"
      + "          \"instrumentName\": \"Piano\","
      + "          \"clefTypes\": [\"treble\", \"bass\"]"
      + "        },"
      + "        \"staves\": ["
      + "          {"
      + "            \"clefType\": \"treble\","
      + "            \"measures\": ["
      + "              {"
      + "                \"keySignature\": 0,"
      + "                \"timeSignatureNumerator\": 4,"
      + "                \"timeSignatureDenominator\": 4,"
      + "                \"tempo\": 120,"
      + "                \"musicElements\": ["
      + "                  {"
      + "                    \"type\": \"note\","
      + "                    \"pitch\": 60,"
      + "                    \"midiNumber\": 60,"
      + "                    \"noteName\": \"C4\","
      + "                    \"duration\": 1,"
      + "                    \"durationChar\": \"q\","
      + "                    \"dotted\": 0,"
      + "                    \"tied\": false,"
      + "                    \"lyric\": \"Test lyric\""
      + "                  }"
      + "                ]"
      + "              }"
      + "            ]"
      + "          }"
      + "        ]"
      + "      }"
      + "    ]"
      + "  }"
      + "]"
      + "}";

  @Test
  public void testGetParsedSongsWithValidJson() throws ParseException {
    SongJsonParser parser = new SongJsonParser();
    List<ParsedSong> parsedSongs = parser.getParsedSongs(VALID_SONG_JSON);
    assertNotNull(parsedSongs, "The parsed song list should not be null.");
    assertEquals(1, parsedSongs.size(), "There should be exactly one parsed song.");

    // Since ParsedSong is a record, we use its accessor methods.
    ParsedSong parsedSong = parsedSongs.getFirst();
    Song song = parsedSong.song();
    assertEquals(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"), song.getId(), "Song ID does not match.");
    assertEquals("Test Song", song.getTitle(), "Song title does not match.");
    assertEquals("Test Composer", song.getComposer(), "Composer does not match.");
    assertEquals(4, song.getPickUp(), "Pickup value does not match.");

    // Verify publisher ID from ParsedSong.
    assertEquals(UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"), parsedSong.publisher(), "Publisher ID does not match.");

    // Validate sheet music.
    List<SheetMusic> sheetMusicList = song.getSheetMusic();
    assertNotNull(sheetMusicList, "Sheet music list should not be null.");
    assertEquals(1, sheetMusicList.size(), "There should be one sheet music entry.");
    SheetMusic sheetMusic = sheetMusicList.getFirst();

    // Validate instrument information.
    assertEquals("Piano", sheetMusic.getInstrument().getInstrumentName(), "Instrument name does not match.");
    List<String> clefTypes = sheetMusic.getInstrument().getClefTypes();
    assertTrue(clefTypes.contains("treble"), "Clef types should include 'treble'.");
    assertTrue(clefTypes.contains("bass"), "Clef types should include 'bass'.");

    // Validate staves.
    List<Staff> staves = sheetMusic.getStaves();
    assertNotNull(staves, "Staves list should not be null.");
    assertEquals(1, staves.size(), "There should be one staff.");
    Staff staff = staves.getFirst();
    assertEquals("treble", staff.getClefType(), "Staff clef type does not match.");

    // Validate measures.
    List<Measure> measures = staff.getMeasures();
    assertNotNull(measures, "Measures list should not be null.");
    assertEquals(1, measures.size(), "There should be one measure.");
    Measure measure = measures.getFirst();
    assertEquals(0, measure.getKeySignature(), "Key signature does not match.");
    assertEquals(4, measure.getTimeSignatureNumerator(), "Time signature numerator does not match.");
    assertEquals(4, measure.getTimeSignatureDenominator(), "Time signature denominator does not match.");
    assertEquals(120, measure.getTempo(), "Tempo does not match.");

    // Validate music elements.
    List<?> musicElements = measure.getMusicElements();
    assertNotNull(musicElements, "Music elements list should not be null.");
    assertEquals(1, musicElements.size(), "There should be one music element.");
    Object element = musicElements.getFirst();
    assertInstanceOf(Note.class, element, "The music element should be a Note.");
    Note note = (Note) element;
    assertEquals(60, note.getPitch(), "Note pitch does not match.");
    assertEquals(60, note.getMidiNumber(), "MIDI number does not match.");
    assertEquals("C4", note.getNoteName(), "Note name does not match.");
    assertEquals(1, note.getDuration(), "Note duration does not match.");
    assertEquals('q', note.getDurationChar(), "Note duration character does not match.");
    assertEquals(0, note.getDotted(), "Dotted value does not match.");
    assertFalse(note.hasTie(), "Note tied flag should be false.");
    assertEquals("Test lyric", note.getLyric(), "Note lyric does not match.");
  }

  @Test
  public void testEmptySongsArray() throws ParseException {
    // JSON with an empty "songs" array.
    String jsonWithEmptySongs = "{ \"songs\": [] }";
    SongJsonParser parser = new SongJsonParser();
    List<ParsedSong> parsedSongs = parser.getParsedSongs(jsonWithEmptySongs);
    assertNotNull(parsedSongs, "The parsed song list should not be null.");
    assertTrue(parsedSongs.isEmpty(), "The parsed song list should be empty.");
  }

  @Test
  public void testMalformedJson() {
    // Provide a malformed JSON string.
    String malformedJson = "{ \"songs\": [ { \"id\": \"not-a-uuid\", ";
    SongJsonParser parser = new SongJsonParser();
    assertThrows(ParseException.class, () -> parser.getParsedSongs(malformedJson),
        "A malformed JSON string should throw a ParseException.");
  }

  @Test
  public void testMissingRequiredFieldInSong() {
    // JSON missing a required field (e.g., "id") in the song.
    String jsonMissingId = "{"
        + "\"songs\": ["
        + "  {"
        + "    \"title\": \"Test Song\","
        + "    \"composer\": \"Test Composer\","
        + "    \"publisher\": \"bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb\","
        + "    \"pickUp\": 4,"
        + "    \"sheetMusic\": []"
        + "  }"
        + "]"
        + "}";
    SongJsonParser parser = new SongJsonParser();
    // Expect an IllegalArgumentException because the required key "id" is missing.
    assertThrows(IllegalArgumentException.class, () -> parser.getParsedSongs(jsonMissingId),
        "A missing required field should throw an IllegalArgumentException.");
  }

  @Test
  public void testUnknownMusicElementType() {
    // JSON with a music element that has an unknown type.
    String jsonUnknownMusicElement = "{"
        + "\"songs\": ["
        + "  {"
        + "    \"id\": \"aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa\","
        + "    \"title\": \"Test Song\","
        + "    \"composer\": \"Test Composer\","
        + "    \"publisher\": \"bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb\","
        + "    \"pickUp\": 4,"
        + "    \"sheetMusic\": ["
        + "      {"
        + "        \"instrument\": {"
        + "          \"instrumentName\": \"Piano\","
        + "          \"clefTypes\": [\"treble\", \"bass\"]"
        + "        },"
        + "        \"staves\": ["
        + "          {"
        + "            \"clefType\": \"treble\","
        + "            \"measures\": ["
        + "              {"
        + "                \"keySignature\": 0,"
        + "                \"timeSignatureNumerator\": 4,"
        + "                \"timeSignatureDenominator\": 4,"
        + "                \"tempo\": 120,"
        + "                \"musicElements\": ["
        + "                  {"
        + "                    \"type\": \"unknown\","
        + "                    \"duration\": 1"
        + "                  }"
        + "                ]"
        + "              }"
        + "            ]"
        + "          }"
        + "        ]"
        + "      }"
        + "    ]"
        + "  }"
        + "]"
        + "}";
    SongJsonParser parser = new SongJsonParser();
    // Expect an IllegalArgumentException when an unknown music element type is encountered.
    assertThrows(IllegalArgumentException.class, () -> parser.getParsedSongs(jsonUnknownMusicElement),
        "An unknown music element type should throw an IllegalArgumentException.");
  }
}
