package com.model;

import org.jfugue.player.Player;

/**
 * Represents a musical note
 */
public class Note {
	private static final String type = "note";
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

	private void findMissingAttributes() {
		if(pitch != 0.0) {
			midiNumber = (int)(69 + 12*(Math.log(pitch/440)/Math.log(2)));
			noteName = NOTE_NAMES[midiNumber % 12] + ((midiNumber/12)-1);
		}
		else if(midiNumber != 0.0) {
			pitch = 440*Math.pow(2,(midiNumber - 69)/12.0);
			noteName = NOTE_NAMES[midiNumber % 12] + ((midiNumber/12)-1);
		}
		else if(noteName != null) {
			midiNumber = (int)(69 + 12*(Math.log(pitch/440)/Math.log(2)));
			pitch = 440*Math.pow(2,(midiNumber - 69)/12.0);
		}
		if(duration != 0.0) {
			if(duration == 1.0) {
				durationChar = 'w';
			} else if(duration == 0.5) {
				durationChar = 'h';
			} else if(duration == 0.25) {
				durationChar = 'q';
			} else if(duration == 0.125) {
				durationChar = 'e';
			} else if(duration == 0.0625) {
				durationChar = 's';
			}
		}
		else if(durationChar != '\0') {
			if(durationChar == 'w') {
				duration = 1.0;
			} else if(durationChar == 'h') {
				duration = 0.5;
			} else if(durationChar == 'q') {
				duration = 0.25;
			} else if(durationChar == 'e') {
				duration = 0.125;
			} else if(durationChar == 's') {
				duration = 0.0625;
			}
		}
	}

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
		return type;
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
}
