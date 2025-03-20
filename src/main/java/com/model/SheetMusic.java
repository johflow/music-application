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

<<<<<<< HEAD
}
=======
  public void display() {
    //TODO
  }

  /**
   * Gets the instrument of the sheet music
   * 
   * @return The instrument of the sheet music
   */
  public Instrument getInstrument() {
    return instrument;
  }

  /**
   * Gets the staves of the sheet music
   * 
   * @return The staves of the sheet music
   */
  public List<Staff> getStaves() {
    return staves;
  }
}
>>>>>>> 597b3e47251deb52d305d20928f9ca793af10c04
