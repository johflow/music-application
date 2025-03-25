package com.model;

public class Rest implements MusicElement, DurationElement {
  private String type;
  private double duration;
  private char durationChar;
  private int dotted;
  private boolean tied;
  private String lyric;

  public Rest(String type, double duration, char durationChar, int dotted, boolean tied,
      String lyric) {
    this.type = type;
    this.duration = duration;
    this.durationChar = durationChar;
    this.dotted = dotted;
    this.tied = tied;
    this.lyric = lyric;
  }

  public void play(){
    //TODO
  }

  public String toJFugueString() {
    //TODO
    return "";
  }

  /**
   * Gets the type of the rest
   * 
   * @return The type of the rest
   */
  public String getType() {
    return type;    
  }

  /**
   * Gets the duration of the rest
   * 
   * @return The duration of the rest
   */
  public double getDuration() {
    return duration;
  }

  /**
   * Gets the duration character of the rest
   * 
   * @return The duration character of the rest
   */
  public char getDurationChar() {
    return durationChar;
  }

  /**
   * Gets the dotted of the rest
   * 
   * @return The dotted of the rest
   */
  public int getDotted() {
    return dotted;
  }

  /**
   * Gets the tied of the rest
   * 
   * @return The tied of the rest
   */
  public boolean hasTie() {
    return tied;
  }

  /**
   * Gets the lyric of the rest
   * 
   * @return The lyric of the rest
   */
  public String getLyric() {
    return lyric;
  }

  @Override
  public String toString() {
    return "Rest{" +
        "durationChar=" + durationChar +
        '}';
  }
}
