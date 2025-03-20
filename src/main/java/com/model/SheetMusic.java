package com.model;

import java.util.ArrayList;
import java.util.List;
import org.jfugue.pattern.Pattern;

/**
 * Represents sheet music for a song
 */
public class SheetMusic {
  private List<Staff> staves;
  private Instrument instrument;

  /**
   * Constructor for the SheetMusic class
   * @param instrument The insrument associated with this sheet music
   */
  public SheetMusic(Instrument instrument) {
    this.staves = new ArrayList<>();
    this.instrument = instrument;
  }

  /**
  * Converts this sheet music to a JFugue Pattern
  * @return Pattern representing this sheet music in JFugue format
  */
  public Pattern toJfuguePattern() {
    Pattern pattern = new Pattern();
      
    // Set the instrument for this pattern
    pattern.setInstrument(instrument.getInstrumentName());
      
    // Convert each staff to a pattern and add it to the main pattern
    for (Staff staff : staves) {
        Pattern staffPattern = staff.toJfuguePattern();
        pattern.add(staffPattern);
      }
      
      return pattern;
  }

  /**
  * Adds a new staff to this sheet music
  * @param staff The staff to add
  */
  public void addStaff(Staff staff) {
    staves.add(staff);
  }
  
  /**
   * Creates a new staff with the specified clef type and adds it to this sheet music
   * @param clefType The type of clef for the new staff
   * @return The newly created staff
   */
  public Staff createStaff(String clefType) {
      Staff staff = new Staff(clefType, new ArrayList<>());
      staves.add(staff);
      return staff;
  }
  
  /**
   * Gets the instrument associated with this sheet music
   * @return The instrument
   */
  public Instrument getInstrument() {
      return instrument;
  }
  
  /**
   * Sets the instrument for this sheet music
   * @param instrument The new instrument
   */
  public void setInstrument(Instrument instrument) {
      this.instrument = instrument;
  }
  
  /**
   * Gets all staves in this sheet music
   * @return List of staves
   */
  public List<Staff> getStaves() {
      return staves;
  }
}


