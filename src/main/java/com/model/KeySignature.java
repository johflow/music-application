package com.model;

/**
 * Represents musical key signatures
 */
public enum KeySignature {

  ZERO_ACCIDENTALS("C Major", "A Minor", 0),
  ONE_SHARP("G Major", "E Minor", 1),
  TWO_SHARPS("D Major", "B Minor", 2),
  THREE_SHARPS("A Major", "F# Minor", 3),
  FOUR_SHARPS("E Major", "C# Minor", 4),
  FIVE_SHARPS("B Major", "G# Minor", 5),
  SIX_SHARPS("F# Major", "D# Minor", 6),
  SEVEN_SHARPS("C# Major", "A# Minor", 7),
  ONE_FLAT("F Major", "D Minor", -1),
  TWO_FLATS("Bb Major", "G Minor", -2),
  THREE_FLATS("Eb Major", "C Minor", -3),
  FOUR_FLATS("Ab Major", "F Minor", -4),
  FIVE_FLATS("Db Major", "Bb Minor", -5),
  SIX_FLATS("Gb Major", "Eb Minor", -6),
  SEVEN_FLATS("Cb Major", "Ab Minor", -7);

  private final String majorKey;
  private final String minorKey;
  private final int accidentals;

  KeySignature(String majorKey, String minorKey, int accidentals) {
    this.majorKey = majorKey;
    this.minorKey = minorKey;
    this.accidentals = accidentals;
  }

  public String getMajorKey() {
    return majorKey;
  }

  public String getMinorKey() {
    return minorKey;
  }

  public int getAccidentals() {
    return accidentals;
  }

  @Override
  public String toString() {
    return name() + " [Major: " + majorKey + ", Minor: " + minorKey + ", Accidentals: " + accidentals + "]";
  }
}
