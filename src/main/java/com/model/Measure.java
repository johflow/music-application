package com.model;
import java.util.ArrayList;

/**
 * template
 * 
 * @author 
 */
public class Measure {
	private KeySignature keySignature;
	private int timeSignatureNumerator;
	private int timeSignatureDenominator;
	private ArrayList<Chord> chords;
	private int measureNumber;
	private double tempo;

	/**
	 * template
	 * 
	 * @param keySignature
	 * @param timeSignatureNumerator
	 * @param timeSignatureDenominator
	 * @param tempo
	 */
	public Measure(KeySignature keySignature, int timeSignatureNumerator, int timeSignatureDenominator, double tempo) {
		//TODO
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
		return new Measure(null, 0, 0, 0);
	}

	/**
	 * template
	 */
	public ArrayList<Chord> getChords() {
		//TODO
		return chords;
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
}
