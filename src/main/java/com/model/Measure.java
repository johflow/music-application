package com.model;
import java.util.ArrayList;
import java.util.List;

/**
 * template
 * 
 * @author 
 */
public class Measure {
	private int keySignature;
	private int timeSignatureNumerator;
	private int timeSignatureDenominator;
	private List<MusicElement> musicElements;
	private int measureNumber;
	private double tempo;



	public Measure(){}

	public Measure(int keySignature, int timeSignatureNumerator, int timeSignatureDenominator,
			double tempo, List<MusicElement> musicElements) {
		this.keySignature = keySignature;
		this.timeSignatureNumerator = timeSignatureNumerator;
		this.timeSignatureDenominator = timeSignatureDenominator;
		this.musicElements = musicElements;
		this.tempo = tempo;
	}

	/**
	 * template
	 * 
	 * @param pitch
	 * @param duration
	 */
	public void addNote(int pitch, double duration) {
		//TODO
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
	public Measure selectMeasure() {
		//TODO
		return new Measure();
	}

	public MusicElement getMusicElementAtIndex(int index) {
		return musicElements.get(index);
	}

	/**
	 * template
	 * 
	 * @param timeSignatureNumerator
	 * @param timeSignatureDenominator
	 */
	public void setTimeSignature(int timeSignatureNumerator, int timeSignatureDenominator) {
		//TODO
	}

	/**
	 * template
	 * 
	 * @param keySignature
	 */
	public void setKeySignature(String keySignature) {
		//TODO
	}

	/**
	 * Gets the key signature of the measure
	 * 
	 * @return The key signature of the measure
	 */
	public int getKeySignature() {
		return keySignature;
	}

	/**
	 * Gets the time signature numerator of the measure
	 * 
	 * @return The time signature numerator of the measure
	 */
	public int getTimeSignatureNumerator() {
		return timeSignatureNumerator;
	}	

	/**
	 * Gets the time signature denominator of the measure
	 * 
	 * @return The time signature denominator of the measure
	 */
	public int getTimeSignatureDenominator() {
		return timeSignatureDenominator;
	}	

	/**
	 * Gets the music elements of the measure
	 * 
	 * @return The music elements of the measure
	 */
	public List<MusicElement> getMusicElements() {
		return musicElements;
	}

	/**
	 * Gets the measure number of the measure
	 * 
	 * @return The measure number of the measure
	 */
	public int getMeasureNumber() {
		return measureNumber;
	}

	/**
	 * Gets the tempo of the measure
	 * 
	 * @return The tempo of the measure
	 */
	public double getTempo() {
		return tempo;
	}	
}
