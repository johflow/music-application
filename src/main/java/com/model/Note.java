package com.model;

import org.jfugue.player.Player;

public class Note implements MusicElement {
	private double pitch;
	private double duration;
	private char durationChar;
	private int midiNumber;
	private String noteName;
	private String lyric;
	private static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

	public Note(double pitch, double duration, String lyric) {
		this.pitch = pitch;
		this.duration = duration;
		this.lyric = lyric;
		findMissingAttributes();
	}

	public Note(int midiNumber, double duration, String lyric) {
		this.midiNumber = midiNumber;
		this.duration = duration;
		this.lyric = lyric;
		findMissingAttributes();
	}

	public Note(String noteName, double duration, String lyric) {
		this.noteName = noteName;
		this.duration = duration;
		this.lyric = lyric;
		findMissingAttributes();
	}

	public Note(double pitch, char durationChar, String lyric) {
		this.pitch = pitch;
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

	public void play() {
		Player player = new Player();
		player.play(noteName + durationChar);
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
}
