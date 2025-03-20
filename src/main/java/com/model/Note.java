package com.model;

import org.jfugue.player.Player;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a musical note with properties like pitch, duration, and other musical attributes.
 */
public class Note implements MusicElement {
    private double pitch;
    private int midiNumber;
    private String noteName;
    private double duration;
    private char durationChar;
    private int dotted;
    private boolean tied;
    private String lyric;
    private static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    /**
	 * Constructor for Note
	 * 
	 * @param pitch    The pitch of the note (MIDI value)
	 * @param duration The duration of the note
	 * @param isRest   Whether the note is a rest
	 */
	public Note(int pitch, int duration, boolean isRest) {
		this.pitch = pitch;
		this.duration = duration;
		findMissingAttributes();
	}

	public Note(double pitch, double duration, String lyric) {
		this.pitch = (int)pitch;
		this.duration = (int)duration;
		this.lyric = lyric;
		findMissingAttributes();
	}

	public Note(int midiNumber, double duration, String lyric) {
		this.midiNumber = midiNumber;
		this.duration = (int)duration;
		this.lyric = lyric;
		findMissingAttributes();
	}

	public Note(String noteName, double duration, String lyric) {
		this.noteName = noteName;
		this.duration = (int)duration;
		this.lyric = lyric;
		findMissingAttributes();
	}

	public Note(double pitch, char durationChar, String lyric) {
		this.pitch = (int)pitch;
		this.durationChar = durationChar;
		this.lyric = lyric;
		findMissingAttributes();
	}

	public Note(int midiNumber, char durationChar, String lyric) {
		this.midiNumber = midiNumber;
		this.durationChar = durationChar;
		this.lyric = lyric;
		findMissingAttributes();
	}

	public Note(String noteName, char durationChar, String lyric) {
		this.noteName = noteName;
		this.durationChar = durationChar;
		this.lyric = lyric;
		findMissingAttributes();
	}

	public Note(double pitch, int midiNumber, String noteName, double duration, char durationChar, String lyric) {
		this.pitch = pitch;
		this.midiNumber = midiNumber;
		this.noteName = noteName;
		this.duration = duration;
		this.durationChar = durationChar;
		this.lyric = lyric;
	}

  public Note(double pitch, int midiNumber, String noteName, double duration, char durationChar, int dotted, boolean tied, String lyric) {
		this.pitch = pitch;
		this.midiNumber = midiNumber;
		this.noteName = noteName;
		this.duration = duration;
		this.durationChar = durationChar;
		this.dotted = dotted;
		this.tied = tied;
		this.lyric = lyric;
	}

    /**
     * Calculate missing note attributes based on provided values
     * Uses the relationships between pitch (Hz), MIDI number, and note name
     * Also converts between fractional duration and duration characters
     */
    private void findMissingAttributes() {
        // Maps for more efficient lookups
        final Map<Character, Double> DURATION_MAP = Map.of(
            'w', 1.0,         // whole note
            'h', 0.5,         // half note
            'q', 0.25,        // quarter note
            'i', 0.125,       // eighth note
            's', 0.0625      // sixteenth note
        );
        
        final Map<String, Integer> NOTE_INDEX_MAP = new HashMap<>();
        for (int i = 0; i < NOTE_NAMES.length; i++) {
            NOTE_INDEX_MAP.put(NOTE_NAMES[i], i);
        }

        // A440 frequency standard and mathematical constants for conversion
        final double A440 = 440.0;
        final double LOG_2 = Math.log(2);
        
        // Calculate pitch/MIDI number/note name based on what's available
        computePitchAndName(A440, LOG_2, NOTE_INDEX_MAP);
        
        // Convert between duration and duration character
        computeDuration(DURATION_MAP);
    }

    /**
     * Computes the relationships between pitch, MIDI number, and note name
     */
    private void computePitchAndName(double a440, double log2, Map<String, Integer> noteMap) {
        // Pitch to MIDI number and note name
        if (pitch > 0 && midiNumber == 0) {
            midiNumber = (int)(69 + 12 * (Math.log(pitch / a440) / log2));
            noteName = NOTE_NAMES[midiNumber % 12] + ((midiNumber / 12) - 1);
        } 
        // MIDI number to pitch and note name
        else if (midiNumber > 0 && (Math.abs(pitch) < 0.001 || noteName == null)) {
            pitch = a440 * Math.pow(2, (midiNumber - 69) / 12.0);
            noteName = NOTE_NAMES[midiNumber % 12] + ((midiNumber / 12) - 1);
        } 
        // Note name to MIDI number and pitch
        else if (noteName != null && noteName.length() > 1 && 
                (midiNumber == 0 || Math.abs(pitch) < 0.001)) {
            try {
                // Parse the noteName (e.g., "C4", "D#5")
                String notePart = noteName.substring(0, noteName.length() - 1);
                int octave = Integer.parseInt(noteName.substring(noteName.length() - 1));
                
                Integer noteIndex = noteMap.get(notePart);
                if (noteIndex != null) {
                    midiNumber = noteIndex + (octave + 1) * 12;
                    pitch = a440 * Math.pow(2, (midiNumber - 69) / 12.0);
                }
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                // Handle invalid note name format
                System.err.println("Invalid note name format: " + noteName);
            }
        }
    }

    /**
     * Computes duration and duration character conversions
     */
    private void computeDuration(Map<Character, Double> durationMap) {
        // Convert duration to duration character
        if (duration > 0 && durationChar == '\0') {
            // Find closest duration character
            double minDiff = Double.MAX_VALUE;
            for (Map.Entry<Character, Double> entry : durationMap.entrySet()) {
                double diff = Math.abs(entry.getValue() - duration);
                if (diff < minDiff) {
                    minDiff = diff;
                    durationChar = entry.getKey();
                }
            }
        } 
        // Convert duration character to duration
        else if (durationChar != '\0' && Math.abs(duration) < 0.001) {
            Double durationValue = durationMap.get(durationChar);
            if (durationValue != null) {
                duration = durationValue;
            } else {
                // Default to quarter note if unknown character
                duration = 0.25;
                durationChar = 'q';
            }
        }
    }

    // Getters and setters
    
    public String toJFugueString(String tiesDurationChar) {
		String JFugueString;
		JFugueString = noteName + durationChar;
		return JFugueString;
	}

	public char getDurationChar() {
		return durationChar;
	}
	public int getDotted() {
		return dotted;
	}

	public boolean hasTie() {
		return tied;
	}

	public String getType() {
		return getType();
	}

	public double getPitch() {
		return pitch;
	}

	public int getMidiNumber() {
		return midiNumber;
	}

	public String getNoteName() {
		return noteName;
	}

	public double getDuration() {
		return duration;
	}

	public String getLyric() {
		return lyric;
	}

	public void setPitch(double pitch) {
		if (pitch < 0) {
			throw new IllegalArgumentException("Notes cannot have a pitch of zero or less!");
		}
		this.pitch = pitch;
	}

	public void setMidiNumber(int midiNumber) {
		if (midiNumber < 0) {
			throw new IllegalArgumentException("Notes cannot have a Midi Number of zero or less!");
		}
		this.midiNumber = midiNumber;
	}

	public void setNoteName(String noteName) {
		if (noteName == null) {
			throw new IllegalArgumentException("Note name cannot be null!");
		}
		this.noteName = noteName;
	}

	public void setDuration(double duration) {
		if (duration <= 0 || duration > 2) {
			throw new IllegalArgumentException("Invalid duration argument!");
		}
		this.duration = duration;
	}

	public void setDurationChar(char durationChar) {
		this.durationChar = durationChar;
	}

	public void setDotted(int dotted) {
		if (dotted < 0) {
			throw new IllegalArgumentException("You cannot dot a note negative times!");
		}
		this.dotted = dotted;
	}

	public void setTied(boolean tied) {
		this.tied = tied;
	}

	public void setLyric(String lyric) {
		if (lyric == null) {
			throw new IllegalArgumentException("Lyrics cannot be null! (Try making it \"\")");
		}
		this.lyric = lyric;
	}

    @Override
    public void play() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'play'");
    }
}