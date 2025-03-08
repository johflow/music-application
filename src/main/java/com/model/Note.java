package com.model;

/**
 * template
 * 
 * @author 
 */
public class Note {
	private double pitch;
	private double duration;
	private char durationChar;
	private int midiNumber;
	private String noteName;
	private String lyric;

	/**
	 * template
	 * 
	 * @param pitch
	 * @param duration
	 * @param lyric
	 */
	public Note(double pitch, double duration, String lyric) {
		this.pitch = pitch;
		this.duration = duration;
		this.lyric = lyric;
		findMissingAttributes(this);
	}

	public Note(int midiNumber, double duration, String lyric) {
		this.midiNumber = midiNumber;
		this.duration = duration;
		this.lyric = lyric;
		findMissingAttributes(this);
	}

	public Note(String noteName, double duration, String lyric) {
		this.noteName = noteName;
		this.duration = duration;
		this.lyric = lyric;
		findMissingAttributes(this);
	}

	public Note(double pitch, char durationChar, String lyric) {
		this.pitch = pitch;
		this.durationChar = durationChar;
		this.lyric = lyric;
		findMissingAttributes(this);
	}

	public Note(int midiNumber, char durationChar, String lyric) {
		this.midiNumber = midiNumber;
		this.durationChar = durationChar;
		this.lyric = lyric;
		findMissingAttributes(this);
	}

	public Note(String noteName, char durationChar, String lyric) {
		this.noteName = noteName;
		this.durationChar = durationChar;
		this.lyric = lyric;
		findMissingAttributes(this);
	}

	public Note(double pitch, int midiNumber, String noteName, double duration, char durationChar, String lyric) {
		this.pitch = pitch;
		this.midiNumber = midiNumber;
		this.noteName = noteName;
		this.duration = duration;
		this.durationChar = durationChar;
		this.lyric = lyric;
	}

	/**
	 * template
	 */
	public void delete() {
		//TODO
	}

	/**
	 * template
	 */
	public void play() {
		//TODO
	}

	public void findMissingAttributes(Note note) {
		if(pitch != 0.0) {
			
		}
		else if(midiNumber != 0.0) {

		}
		else if(noteName != null) {

		}
		if(duration != 0.0) {
			
		}
		 else if(durationChar != '\0') {

		}
	}
}
