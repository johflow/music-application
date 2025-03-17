package com.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents sheet music for a song
 */
public class SheetMusic {

  private List<Staff> staves;
  private Instrument instrument;

  public SheetMusic(Instrument instrument, List<Staff> staves) {
    this.staves = staves;
    this.instrument = instrument;
  }

  public void transpose(String key) {
    //TODO
  }

  public void display() {
    //TODO
  }


}
