package com.model;

import java.util.List;

public class Tuplet implements MusicElement {
  private String type;
  private int subdivisions;
  private int impliedDivision;
  private double duration;
  private List<MusicElement> elements;

  public Tuplet() {
    //TODO
  }

  public Tuplet(int subdivisions, int impliedDivision, double duration, List<MusicElement> elements) {
  }


  public String toJFugueString() {
    //TODO
    return "";
  }

  /**
   * Gets the type of the tuplet
   * 
   * @return The type of the tuplet
   */
  public String getType() {
    return type;
  }

  /**
   * Gets the subdivisions of the tuplet
   * 
   * @return The subdivisions of the tuplet
   */
  public int getSubdivisions() {
    return subdivisions;
  }

  /**
   * Gets the implied division of the tuplet
   * 
   * @return The implied division of the tuplet
   */
  public int getImpliedDivision() {
    return impliedDivision;
  }

  /**
   * Gets the duration of the tuplet
   * 
   * @return The duration of the tuplet
   */
  public double getDuration() {
    return duration;
  }

  /**
   * Gets the elements of the tuplet
   * 
   * @return The elements of the tuplet
   */
  public List<MusicElement> getElements() {
    return elements;
  }
}