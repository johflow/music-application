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


public class SongPlayer extends DataConstants {

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
    System.out.println(fullSong.toString());
    player.play(fullSong);
  }

  private Pattern staffToJFuguePattern(Staff staff) {
    Pattern pattern = new Pattern();
    for (Measure measure : staff.getMeasures()) {
      measureToJFuguePattern(measure, pattern);
    }
    return pattern;
  }

  private void measureToJFuguePattern(Measure measure, Pattern pattern) {
    List<MusicElement> previousElementTiedElements = new ArrayList<>();
    for (MusicElement musicElement : measure.getMusicElements()) {
      ProcessedMusicElement processedMusicElement = processMusicElement(musicElement, previousElementTiedElements);
      previousElementTiedElements.clear();
      previousElementTiedElements.addAll(processedMusicElement.tiedElements());
      pattern.add(processedMusicElement.elementJFugueString());
    }
  }

  private ProcessedMusicElement processMusicElement(MusicElement musicElement, List<MusicElement> previousTiedElements) {
    StringBuilder pattern = new StringBuilder();
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

  private String elementToJFugueDurationString(MusicElement element, List<MusicElement> previousTiedElements) {
    DurationElement durationElement = (DurationElement) element;
    return (previousTiedToCurrent(element, previousTiedElements) ? "-" : "")
        + durationElement.getDurationChar() + ".".repeat(Math.max(0, durationElement.getDotted()))
        + (durationElement.hasTie() ? "-" : "");
  }

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

  private boolean previousTiedToCurrent(MusicElement musicElement, List<MusicElement> previousTiedElements) {
    for (MusicElement aMusicElement : previousTiedElements) {
      if (aMusicElement.equals(musicElement)) {
        return true;
      }
    }
    return false;
  }

  public static void main(String[] args) throws IOException, ParseException {
    SongPlayer player = new SongPlayer();
    DataAssembler dataAssembler  = new DataAssembler();
    List<Song> songs = dataAssembler.getAssembledSongs();
    player.play(songs.getFirst());
  }


}
