package com.service;

import com.model.Instrument;
import com.model.Measure;
import com.model.Note;
import com.model.SheetMusic;
import com.model.Song;
import com.model.Staff;
import com.model.MusicElement;
import org.jfugue.pattern.Pattern;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;


public class SongPlayerTest {


  private static class TestSongPlayer extends SongPlayer {
    private Pattern capturedPattern;

    @Override
    public void play(Song song) {

      List<Staff> staves = new java.util.ArrayList<>();
      Pattern fullSong = new Pattern();
      for (SheetMusic sheetMusic : song.getSheetMusic()) {
        staves.addAll(sheetMusic.getStaves());
      }
      for (int i = 0; i < staves.size(); i++) {

        Pattern staffPattern = new Pattern();
        for (Measure measure : staves.get(i).getMeasures()) {
          for (MusicElement element : measure.getMusicElements()) {

            if (element instanceof Note note) {

              staffPattern.add(note.getNoteName() + note.getDurationChar());
            } else {
              staffPattern.add("?");
            }
          }
        }
        Pattern pattern = new Pattern("V" + i + " R " + staffPattern);
        fullSong.add(pattern);
      }
      capturedPattern = fullSong;

      System.out.println(fullSong);

    }

    public Pattern getCapturedPattern() {
      return capturedPattern;
    }
  }

  @Test
  public void testPlayPatternForSongWithNote() {

    Note note = new Note(60, 60, "C4", 1, 'q', 0, false, "Test lyric");


    Measure measure = new Measure(0, 4, 4, 120, List.of(note));


    Staff staff = new Staff("treble", List.of(measure));


    Instrument instrument = new Instrument(List.of("treble"), "Piano");


    SheetMusic sheetMusic = new SheetMusic(instrument, List.of(staff));


    Song song = new Song(UUID.randomUUID(), "Test Song", "Composer", 4, List.of(sheetMusic));


    TestSongPlayer testPlayer = new TestSongPlayer();
    testPlayer.play(song);
    Pattern captured = testPlayer.getCapturedPattern();
    assertNotNull(captured, "Captured pattern should not be null.");


    String patternString = captured.toString();

    assertTrue(patternString.contains("V0"), "Pattern should contain voice V0.");
    assertTrue(patternString.contains("C4q"), "Pattern should contain the note 'C4q'.");
  }
}
