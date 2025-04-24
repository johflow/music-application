package com.service;

import com.model.Chord;
import com.model.DataConstants;
import com.model.DurationElement;
import com.model.Instrument;
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
 * It processes various musical components such as notes, chords, rests, and tuplets.
 */
public class SongPlayer extends DataConstants {

  /**
   * Plays the given {@link Song} by converting it into a JFugue pattern.
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
    System.out.println(pattern);
    return pattern;
  }

  /**
   * Processes a {@link Measure} and appends its musical content to the given JFugue {@link Pattern}.
   * Iterates over the measure's music elements by index so the "next" element can be used for tie detection.
   *
   * @param measure The measure to process.
   * @param pattern The pattern to which the measure's content is added.
   */
  private void measureToJFuguePattern(Measure measure, Pattern pattern) {
    List<MusicElement> elements = measure.getMusicElements();
    List<MusicElement> previousTiedElements = new ArrayList<>();
    for (int i = 0; i < elements.size(); i++) {
      MusicElement current = elements.get(i);
      MusicElement next = (i < elements.size() - 1) ? elements.get(i + 1) : null;
      ProcessedMusicElement processed = processMusicElement(current, previousTiedElements, next);
      previousTiedElements.clear();
      previousTiedElements.addAll(processed.tiedElements());
      pattern.add("T" + elements.get(i).getTempo() + " " + processed.elementJFugueString());
    }
  }

  /**
   * Processes a {@link MusicElement} by determining its type and converting it into a JFugue string.
   * Also accepts a nextElement to help determine trailing ties.
   *
   * @param musicElement The musical element to process.
   * @param previousTiedElements The list of previously tied musical elements.
   * @param nextElement The upcoming music element in the measure (or null).
   * @return A {@link ProcessedMusicElement} containing the JFugue string and any tied elements.
   */
  private ProcessedMusicElement processMusicElement(MusicElement musicElement, List<MusicElement> previousTiedElements, MusicElement nextElement) {
    return switch (musicElement.getType()) {
      case SONG_MUSIC_ELEMENT_NOTE ->
          noteToJFugueString(musicElement, previousTiedElements, nextElement);
      case SONG_MUSIC_ELEMENT_CHORD ->
          chordToJFugueString(musicElement, previousTiedElements, nextElement);
      case SONG_MUSIC_ELEMENT_REST ->
          restToJFugueString(musicElement, previousTiedElements, nextElement);
      case SONG_MUSIC_ELEMENT_TUPLET ->
          tupletToJFugueString(musicElement, previousTiedElements, nextElement);
      default -> throw new IllegalArgumentException("Music element does not have a valid type!");
    };
  }

  /**
   * Converts a note {@link MusicElement} to a JFugue string.
   * The trailing tie marker is added only if there is an upcoming tie.
   *
   * @param element The note element to convert.
   * @param previousTiedElements The list of previously tied elements.
   * @param nextElement The upcoming music element (or null).
   * @return A {@link ProcessedMusicElement} containing the JFugue string and tied elements.
   */
  private ProcessedMusicElement noteToJFugueString(MusicElement element, List<MusicElement> previousTiedElements, MusicElement nextElement) {
    Note note = (Note) element;
    List<MusicElement> tiedElements = new ArrayList<>();
    String stringPart = note.getNoteName() + elementToJFugueDurationString(element, previousTiedElements, nextElement);
    if (note.hasTie() && hasUpcomingTie(element, nextElement)) {
      tiedElements.add(note);
    }
    return new ProcessedMusicElement(stringPart, tiedElements);
  }

  /**
   * Converts a rest {@link MusicElement} to a JFugue string.
   *
   * @param element The rest element to convert.
   * @param previousTiedElements The list of previously tied elements.
   * @param nextElement The upcoming music element (or null).
   * @return A {@link ProcessedMusicElement} containing the JFugue string and tied elements.
   */
  private ProcessedMusicElement restToJFugueString(MusicElement element, List<MusicElement> previousTiedElements, MusicElement nextElement) {
    Rest rest = (Rest) element;
    List<MusicElement> tiedElements = new ArrayList<>();
    String stringPart = "R" + elementToJFugueDurationString(element, previousTiedElements, nextElement);
    if (rest.hasTie() && hasUpcomingTie(element, nextElement)) {
      tiedElements.add(rest);
    }
    return new ProcessedMusicElement(stringPart, tiedElements);
  }

  /**
   * Converts a chord {@link MusicElement} into a JFugue string by processing each note.
   * For chord notes, the next element is not passed (handled as a unit).
   *
   * @param element The chord element to convert.
   * @param previousTiedElements The list of previously tied elements.
   * @param nextElement Unused for chords; can be null.
   * @return A {@link ProcessedMusicElement} containing the chord's JFugue string and tied elements.
   */
  private ProcessedMusicElement chordToJFugueString(MusicElement element, List<MusicElement> previousTiedElements, MusicElement nextElement) {
    Chord chord = (Chord) element;
    StringBuilder chordString = new StringBuilder();
    for (Note note : chord.getNotes()) {
      // For chord notes, pass null for next element.
      chordString.append(noteToJFugueString(note, previousTiedElements, null).elementJFugueString()).append("+");
      if (note.hasTie()) {
        previousTiedElements.add(note);
      }
    }
    chordString.deleteCharAt(chordString.length() - 1);
    return new ProcessedMusicElement(chordString.toString(), previousTiedElements);
  }

  /**
   * Converts a tuplet {@link MusicElement} into a JFugue string by processing its constituent elements.
   * For elements within the tuplet, the next element is determined from the tuplet's internal list.
   *
   * @param element The tuplet element to convert.
   * @param previousTiedElements The list of previously tied elements.
   * @param nextElement The upcoming music element outside the tuplet (unused here).
   * @return A {@link ProcessedMusicElement} containing the tuplet's JFugue string and tied elements.
   */
  private ProcessedMusicElement tupletToJFugueString(MusicElement element, List<MusicElement> previousTiedElements, MusicElement nextElement) {
    Tuplet tuplet = (Tuplet) element;
    StringBuilder tupletString = new StringBuilder();
    List<MusicElement> elems = tuplet.getElements();
    for (int i = 0; i < elems.size(); i++) {
      MusicElement current = elems.get(i);
      MusicElement nextInTuplet = (i < elems.size() - 1) ? elems.get(i + 1) : null;
      ProcessedMusicElement processed = processMusicElement(current, previousTiedElements, nextInTuplet);
      tupletString.append(processed.elementJFugueString())
          .append("*").append(tuplet.getSubdivisions()).append(":").append(tuplet.getImpliedDivision()).append(" ");
      previousTiedElements = processed.tiedElements();
    }
    return new ProcessedMusicElement(tupletString.toString(), previousTiedElements);
  }

  /**
   * Converts a musical element's duration into a JFugue-compatible duration string.
   * It prefixes a "-" if the element is tied from a previous element and appends a "-" only if there is an upcoming tie.
   *
   * @param element The musical element whose duration is to be converted.
   * @param previousTiedElements The list of previously tied elements.
   * @param nextElement The upcoming music element (or null).
   * @return A string representing the duration in JFugue format.
   */
  private String elementToJFugueDurationString(MusicElement element, List<MusicElement> previousTiedElements, MusicElement nextElement) {
    DurationElement durationElement = (DurationElement) element;
    String prefix = previousTiedToCurrent(element, previousTiedElements) ? "-" : "";
    String suffix = hasUpcomingTie(element, nextElement) ? "-" : "";
    return prefix + durationElement.getDurationChar() + suffix + ".".repeat(Math.max(0, durationElement.getDotted()));
  }

  /**
   * Checks whether the current musical element is tied to a previous element.
   * For Notes, it compares pitch; for Chords it checks each note; for Tuplets it checks the last element; for Rests it compares durationChar.
   *
   * @param current The current musical element.
   * @param previousTiedElements The list of previously tied elements.
   * @return true if current is tied to a previous element; false otherwise.
   */
  private boolean previousTiedToCurrent(MusicElement current, List<MusicElement> previousTiedElements) {
    if (previousTiedElements == null || previousTiedElements.isEmpty()) {
      return false;
    }
    MusicElement prev = previousTiedElements.get(previousTiedElements.size() - 1);
    String currentType = current.getType();
    String prevType = prev.getType();

    // For Note: check if current note has tie and same pitch as previous.
    if (currentType.equals(SONG_MUSIC_ELEMENT_NOTE)) {
      Note currentNote = (Note) current;
      if (!currentNote.hasTie()) return false;
      if (prevType.equals(SONG_MUSIC_ELEMENT_NOTE)) {
        return currentNote.getNoteName().equals(((Note) prev).getNoteName());
      } else if (prevType.equals(SONG_MUSIC_ELEMENT_CHORD)) {
        Chord prevChord = (Chord) prev;
        for (Note n : prevChord.getNotes()) {
          if (n.hasTie() && currentNote.getNoteName().equals(n.getNoteName())) {
            return true;
          }
        }
        return false;
      } else if (prevType.equals(SONG_MUSIC_ELEMENT_TUPLET)) {
        Tuplet prevTuplet = (Tuplet) prev;
        List<MusicElement> tupElements = prevTuplet.getElements();
        if (!tupElements.isEmpty()) {
          MusicElement lastInTuplet = tupElements.get(tupElements.size() - 1);
          return previousTiedToCurrent(current, List.of(lastInTuplet));
        }
        return false;
      } else if (prevType.equals(SONG_MUSIC_ELEMENT_REST)) {
        return false;
      }
    }
    // For Chord: check each tied note.
    else if (currentType.equals(SONG_MUSIC_ELEMENT_CHORD)) {
      Chord currentChord = (Chord) current;
      for (Note currNote : currentChord.getNotes()) {
        if (!currNote.hasTie()) continue;
        if (prevType.equals(SONG_MUSIC_ELEMENT_NOTE)) {
          if (currNote.getNoteName().equals(((Note) prev).getNoteName())) {
            return true;
          }
        } else if (prevType.equals(SONG_MUSIC_ELEMENT_CHORD)) {
          Chord prevChord = (Chord) prev;
          for (Note prevNote : prevChord.getNotes()) {
            if (prevNote.hasTie() && currNote.getNoteName().equals(prevNote.getNoteName())) {
              return true;
            }
          }
        } else if (prevType.equals(SONG_MUSIC_ELEMENT_TUPLET)) {
          Tuplet prevTuplet = (Tuplet) prev;
          List<MusicElement> tupElements = prevTuplet.getElements();
          if (!tupElements.isEmpty()) {
            MusicElement lastInTuplet = tupElements.get(tupElements.size() - 1);
            if (previousTiedToCurrent(currNote, List.of(lastInTuplet))) {
              return true;
            }
          }
        }
      }
      return false;
    }
    // For Tuplet: only check the first element.
    else if (currentType.equals(SONG_MUSIC_ELEMENT_TUPLET)) {
      Tuplet currentTuplet = (Tuplet) current;
      List<MusicElement> currentElements = currentTuplet.getElements();
      if (!currentElements.isEmpty()) {
        MusicElement firstInTuplet = currentElements.get(0);
        return previousTiedToCurrent(firstInTuplet, previousTiedElements);
      }
      return false;
    }
    // For Rest: check that previous is also a Rest with same duration.
    else if (currentType.equals(SONG_MUSIC_ELEMENT_REST)) {
      Rest currentRest = (Rest) current;
      if (!currentRest.hasTie()) return false;
      if (prevType.equals(SONG_MUSIC_ELEMENT_REST)) {
        Rest prevRest = (Rest) prev;
        return currentRest.getDurationChar() == (prevRest.getDurationChar());
      }
      return false;
    }
    return false;
  }

  /**
   * Returns true if the current element has an upcoming tie connecting it to the next element.
   * For a Note, if the next element (note or chord) has a matching pitch;
   * for a Chord, if any note within is tied and matches a note in the next element;
   * for a Rest, if the next rest has the same duration; for a Tuplet, check its last element.
   *
   * @param current The current musical element.
   * @param next The upcoming musical element (or null).
   * @return true if there is an upcoming tie; false otherwise.
   */
  private boolean hasUpcomingTie(MusicElement current, MusicElement next) {
    if (next == null) return false;
    String currentType = current.getType();
    if (currentType.equals(SONG_MUSIC_ELEMENT_NOTE)) {
      Note currNote = (Note) current;
      if (!currNote.hasTie()) return false;
      if (next.getType().equals(SONG_MUSIC_ELEMENT_NOTE)) {
        Note nextNote = (Note) next;
        return currNote.getNoteName().equals(nextNote.getNoteName());
      } else if (next.getType().equals(SONG_MUSIC_ELEMENT_CHORD)) {
        Chord nextChord = (Chord) next;
        for (Note n : nextChord.getNotes()) {
          if (currNote.getNoteName().equals(n.getNoteName())) return true;
        }
      }
    } else if (currentType.equals(SONG_MUSIC_ELEMENT_CHORD)) {
      Chord currChord = (Chord) current;
      if (next.getType().equals(SONG_MUSIC_ELEMENT_CHORD)) {
        Chord nextChord = (Chord) next;
        for (Note currNote : currChord.getNotes()) {
          if (!currNote.hasTie()) continue;
          for (Note nextNote : nextChord.getNotes()) {
            if (currNote.getNoteName().equals(nextNote.getNoteName())) return true;
          }
        }
      } else if (next.getType().equals(SONG_MUSIC_ELEMENT_NOTE)) {
        Note nextNote = (Note) next;
        for (Note currNote : currChord.getNotes()) {
          if (currNote.hasTie() && currNote.getNoteName().equals(nextNote.getNoteName())) return true;
        }
      }
    } else if (currentType.equals(SONG_MUSIC_ELEMENT_REST)) {
      if (next.getType().equals(SONG_MUSIC_ELEMENT_REST)) {
        Rest currRest = (Rest) current;
        Rest nextRest = (Rest) next;
        return currRest.hasTie() && nextRest.hasTie() && currRest.getDurationChar() == (nextRest.getDurationChar());
      }
    } else if (currentType.equals(SONG_MUSIC_ELEMENT_TUPLET)) {
      Tuplet currTuplet = (Tuplet) current;
      if (!currTuplet.getElements().isEmpty()) {
        MusicElement lastInTuplet = currTuplet.getElements().get(currTuplet.getElements().size() - 1);
        return hasUpcomingTie(lastInTuplet, next);
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
    DataAssembler dataAssembler = new DataAssembler();
    List<Song> songs = dataAssembler.getAssembledSongs();
    List<String> clefTypes = new ArrayList<>();
    clefTypes.add("treble");
    Instrument insturment = new Instrument(clefTypes, "piano");
    Song songTest = new Song("my song", "test");
    Staff staff = songs.get(0).getSheetMusic().get(0).getStaves().get(0);
    List<Staff> staves = new ArrayList<>();
    staves.add(staff);
    SheetMusic sheetMusic = new SheetMusic(insturment, staves);
    songTest.addSheetMusic(sheetMusic);
    player.play(songs.get(0));
  }
}
