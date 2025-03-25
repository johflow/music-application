package com.model;

import java.util.ArrayList;
import java.util.List;
import org.jfugue.pattern.Pattern;

public class Staff extends DataConstants {
  private String clefType;
  private List<Measure> measures;

  public Staff(String clefType, List<Measure> measures) {
    this.clefType = clefType;
    this.measures = measures;
  }

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

  public void addMeasure(Measure measure) {
    measures.add(measure);
  }

  public Pattern toJfuguePattern() {
    return new Pattern();
  }

  @Override
  public String toString() {
    return "Staff{" +
        "measures=" + measures +
        '}';
  }
}
