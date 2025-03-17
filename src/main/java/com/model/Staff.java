package com.model;

import java.util.ArrayList;
import java.util.List;

public class Staff {
  private String clefType;
  private List<Measure> measures;

  public Staff(String clefType, List<Measure> measures) {
    this.clefType = clefType;
    this.measures = measures;
  }
}
