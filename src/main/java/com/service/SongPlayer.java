package com.service;

import com.model.Chord;
import com.model.DataConstants;
import com.model.DurationElement;
import com.model.Measure;
import com.model.MusicElement;
import com.model.Note;
import com.model.ProcessedMusicElement;
import com.model.Rest;
import com.model.SheetMusic;
import com.model.Song;
import com.model.Staff;
import com.model.Tuplet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jfugue.player.Player;
import org.jfugue.pattern.Pattern;
import org.json.simple.parser.ParseException;

/**
 * This class is responsible for playing a {@link Song} using JFugue patterns.
 * It converts musical elements into a format that can be interpreted and played by the JFugue library.
 * <p>
 * It processes various musical components such as notes, chords, rests, and tuplets.
 * </p>
 *
 * @author Will Flowers
 */
public class SongPlayer extends DataConstants {

  /**
   * Plays the given {@link Song} by converting it into a JFugue pattern.
   * It iterates over the sheet music and staves to build a full song pattern.
   *
   * @param song The song to be played.
   */
  public void play(Song song) {
    List<Staff> staves = new ArrayList<>();
    Pattern fullSong = new Pattern();
    Player player = new Player();

    for (SheetMusic sheetMusic : song.getSheetMusic()) {
      staves.addAll(sheetMusic.getStaves());
    }
    for (int i = 0; i < staves.size(); ++i) {
      Pattern pattern = new Pattern("V" + i + " R " + staffToJFuguePattern(staves.get(i)));
      fullSong.add(pattern);
    }
    System.out.println(fullSong);
    player.play(fullSong);
  }

  /**
   * Converts a {@link Staff} into a JFugue {@link Pattern} by processing each measure.
   *
   * @param staff The staff to convert.
   * @return A JFugue Pattern representing the staff.
   */
  private Pattern staffToJFuguePattern(Staff staff) {
    Pattern pattern = new Pattern();
    for (Measure measure : staff.getMeasures()) {
      measureToJFuguePattern(measure, pattern);
    }
    return pattern;
  }

  /**
   * Processes a {@link Measure} and appends its musical content to the given JFugue {@link Pattern}.
   *
   * @param measure The measure to process.
   * @param pattern The pattern to which the measure's content is added.
   */
  private void measureToJFuguePattern(Measure measure, Pattern pattern) {
    List<MusicElement> previousElementTiedElements = new ArrayList<>();
    for (MusicElement musicElement : measure.getMusicElements()) {
      ProcessedMusicElement processedMusicElement = processMusicElement(musicElement, previousElementTiedElements);
      previousElementTiedElements.clear();
      previousElementTiedElements.addAll(processedMusicElement.tiedElements());
      pattern.add(processedMusicElement.elementJFugueString());
    }
  }

  /**
   * Processes a {@link MusicElement} by determining its type and converting it into a JFugue string.
   *
   * @param musicElement The musical element to process.
   * @param previousTiedElements The list of previously tied musical elements.
   * @return A {@link ProcessedMusicElement} containing the JFugue string and any tied elements.
   * @throws IllegalArgumentException if the music element type is not valid.
   */
  private ProcessedMusicElement processMusicElement(MusicElement musicElement, List<MusicElement> previousTiedElements) {
    switch (musicElement.getType()) {
      case SONG_MUSIC_ELEMENT_NOTE -> {
        return noteToJFugueString(musicElement, previousTiedElements);
      }
      case SONG_MUSIC_ELEMENT_CHORD -> {
        return chordToJFugueString(musicElement, previousTiedElements);
      }
      case SONG_MUSIC_ELEMENT_REST -> {
        return restToJFugueString(musicElement, previousTiedElements);
      }
      case SONG_MUSIC_ELEMENT_TUPLET -> {
        return tupletToJFugueString(musicElement, previousTiedElements);
      }
      default -> throw new IllegalArgumentException("Music element does not have a valid type!");
    }
  }

  /**
   * Converts a note {@link MusicElement} to a JFugue string.
   * If the note has a tie, it is added to the tied elements list.
   *
   * @param element The note element to convert.
   * @param previousTiedElements The list of previously tied elements.
   * @return A {@link ProcessedMusicElement} containing the JFugue string and tied elements.
   */
  private ProcessedMusicElement noteToJFugueString(MusicElement element, List<MusicElement> previousTiedElements) {
    Note note = (Note) element;
    List<MusicElement> tiedElements = new ArrayList<>();

    String stringPart = note.getNoteName()
        + elementToJFugueDurationString(element, previousTiedElements);
    if (note.hasTie()) {
      tiedElements.add(note);
    }
    return new ProcessedMusicElement(stringPart, tiedElements);
  }

  /**
   * Converts a rest {@link MusicElement} to a JFugue string.
   * If the rest has a tie, it is added to the tied elements list.
   *
   * @param element The rest element to convert.
   * @param previousTiedElements The list of previously tied elements.
   * @return A {@link ProcessedMusicElement} containing the JFugue string and tied elements.
   */
  private ProcessedMusicElement restToJFugueString(MusicElement element, List<MusicElement> previousTiedElements) {
    Rest rest = (Rest) element;
    List<MusicElement> tiedElements = new ArrayList<>();

    String stringPart = "R"
        + elementToJFugueDurationString(element, previousTiedElements);
    if (rest.hasTie()) {
      tiedElements.add(rest);
    }
    return new ProcessedMusicElement(stringPart, tiedElements);
  }

  /**
   * Converts a musical element's duration into a JFugue-compatible duration string.
   * It checks if the element is tied to a previous one and applies the necessary formatting.
   *
   * @param element The musical element whose duration is to be converted.
   * @param previousTiedElements The list of previously tied elements.
   * @return A string representing the duration in JFugue format.
   */
  private String elementToJFugueDurationString(MusicElement element, List<MusicElement> previousTiedElements) {
    DurationElement durationElement = (DurationElement) element;
    return (previousTiedToCurrent(element, previousTiedElements) ? "-" : "")
        + durationElement.getDurationChar() + ".".repeat(Math.max(0, durationElement.getDotted()))
        + (durationElement.hasTie() ? "-" : "");
  }

  /**
   * Converts a chord {@link MusicElement} into a JFugue string by processing each note in the chord.
   *
   * @param element The chord element to convert.
   * @param previousTiedElements The list of previously tied elements.
   * @return A {@link ProcessedMusicElement} containing the chord's JFugue string and tied elements.
   */
  private ProcessedMusicElement chordToJFugueString(MusicElement element, List<MusicElement> previousTiedElements) {
    Chord chord = (Chord) element;
    StringBuilder chordString = new StringBuilder();
    for (Note note : chord.getNotes()) {
      chordString.append(noteToJFugueString(note, previousTiedElements).elementJFugueString()).append("+");
      if (note.hasTie()) {
        previousTiedElements.add(note);
      }
    }
    chordString.deleteCharAt(chordString.length()-1);
    return new ProcessedMusicElement(chordString.toString(), previousTiedElements);
  }

  /**
   * Converts a tuplet {@link MusicElement} into a JFugue string by processing its constituent elements.
   *
   * @param element The tuplet element to convert.
   * @param previousTiedElements The list of previously tied elements.
   * @return A {@link ProcessedMusicElement} containing the tuplet's JFugue string and tied elements.
   */
  private ProcessedMusicElement tupletToJFugueString(MusicElement element, List<MusicElement> previousTiedElements) {
    Tuplet tuplet = (Tuplet) element;
    StringBuilder tupletString = new StringBuilder();
    for (MusicElement musicElement : tuplet.getElements()) {
      ProcessedMusicElement processedMusicElement = processMusicElement(musicElement, previousTiedElements);
      tupletString.append(processedMusicElement.elementJFugueString()).append("*")
          .append(tuplet.getSubdivisions()).append(":").append(tuplet.getImpliedDivision()).append(" ");
      previousTiedElements = processedMusicElement.tiedElements();
    }
    return new ProcessedMusicElement(tupletString.toString(), previousTiedElements);
  }

  /**
   * Checks if the current musical element is tied to any of the previously processed elements.
   *
   * @param musicElement The current musical element.
   * @param previousTiedElements The list of previously tied elements.
   * @return {@code true} if the current element is tied to any previous element; {@code false} otherwise.
   */
  private boolean previousTiedToCurrent(MusicElement musicElement, List<MusicElement> previousTiedElements) {
    for (MusicElement aMusicElement : previousTiedElements) {
      if (aMusicElement.equals(musicElement)) {
        return true;
      }
    }
    return false;
  }

  /**
   * The entry point of the application.
   * It assembles songs, selects the first one, and plays it.
   *
   * @param args Command-line arguments.
   * @throws IOException If an I/O error occurs.
   * @throws ParseException If parsing of data fails.
   */
  public static void main(String[] args) throws IOException, ParseException {
    SongPlayer player = new SongPlayer();
    DataAssembler dataAssembler  = new DataAssembler();
    List<Song> songs = dataAssembler.getAssembledSongs();
    player.play(songs.getFirst());
  }
}
