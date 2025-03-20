package com.model;

import java.util.ArrayList;
import java.util.List;

public class Staff extends DataConstants {
  private String clefType;
  private List<Measure> measures;

  public Staff(String clefType, List<Measure> measures) {
    this.clefType = clefType;
    this.measures = measures;
  }

  public String toJFugueString() {
    StringBuilder JFugueString = new StringBuilder();
    for (int i = 0; i < measures.size(); ++i) {
      for (int j = 0; j < measures.get(i).getMusicElements().size(); ++j) {
        switch (measures.get(i).getMusicElementAtIndex(j).getType()) {
          case SONG_MUSIC_ELEMENT_NOTE -> {
            //JFugueString.append(noteToJFugueString(measures.get(i).getMusicElementAtIndex(j)));
          }
          case SONG_MUSIC_ELEMENT_CHORD -> {

          }
          case SONG_MUSIC_ELEMENT_REST -> {

          }
          case SONG_MUSIC_ELEMENT_TUPLET -> {

          }
        }
      }
    }
    return "";
  }

//  private StringBuilder noteToJFugueString(MusicElement musicElement) {
//    StringBuilder JFugueString = new StringBuilder();
//    Note note = (Note) measures.get(i).getMusicElementAtIndex(j);
//    JFugueString.append(note.toJFugueString());
//    for (int k = 1; ((Note) measures.get(i).getMusicElementAtIndex(k-1)).hasTie(); ++k) {
//      JFugueString.append(((Note) measures.get(i).getMusicElementAtIndex(k)).getDurationChar());
//    }
//    for (int l = 0; l < ((Note) measures.get(i).getMusicElementAtIndex(j)).getDotted(); ++l) {
//      JFugueString.append(".");
//    }
//    return JFugueString;
//  }

  /**
   * Gets the measures of the staff
   * 
   * @return The measures of the staff
   */
  public List<Measure> getMeasures() {
    return measures;
  }

  /**
   * Gets the clef type of the staff
   * 
   * @return The clef type of the staff
   */
  public String getClefType() {
    return clefType;
  }
}
