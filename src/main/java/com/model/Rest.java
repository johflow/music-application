package com.model;

import org.jfugue.player.Player;

public class Rest implements MusicElement {
  private String type = "rest";
  private double duration;
  private char durationChar;
  private int dotted;
  private boolean tied;
  private String lyric;

  public Rest(double duration, char durationChar, int dotted, boolean tied, String lyric) {
    this.duration = duration;
    this.durationChar = durationChar;
    this.dotted = dotted;
    this.tied = tied;
    this.lyric = lyric;
  }

  public void play() {
        Player player = new Player();
        player.play(toJfugueString());
    }


    @Override
    public String toJfugueString() {
      String jFugueString = "Rest" + durationChar;
      return jFugueString;
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
   * Sets the duration of the rest
   *
   * @param duration The duration to set
   */
  public void setDuration(double duration) {
    if (duration <= 0) throw new IllegalArgumentException("Duration must be positive");
    this.duration = duration;
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
   * Sets the duration character
   *
   * @param durationChar The duraction character to set
   */
  public void setDurationChar(char durationChar) {
    this.durationChar = durationChar;
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
   * Sets the number of dots for the rest
   *
   * @param dotted The number of dots to set
   */
  public void setDotted(int dotted) {
    if (dotted < 0) throw new IllegalArgumentException("Dotted count cannot be negative");
    this.dotted = dotted;
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
   * Sets whether this res is tied
   *
   * @param tied The tied state to set
   */
  public void setTied(boolean tied) {
    this.tied = tied;
  }

  /**
   * Gets the lyric of the rest
   * 
   * @return The lyric of the rest
   */
  public String getLyric() {
    return lyric;
  }

  public void setLyric(String lyric) {
    this.lyric = lyric;
  }

}
