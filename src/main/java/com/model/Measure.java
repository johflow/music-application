package com.model;
import java.util.ArrayList;
import java.util.List;

/**
 * template
 * 
 * @author 
 */
public class Measure extends DataConstants {
	private int keySignature;
	private int timeSignatureNumerator;
	private int timeSignatureDenominator;
	private List<MusicElement> musicElements;
	private int measureNumber;
	private double tempo;



	public Measure(){}

	public Measure(int keySignature, int timeSignatureNumerator, int timeSignatureDenominator,
			double tempo, List<MusicElement> elements) {
		this.keySignature = keySignature;
		this.timeSignatureNumerator = timeSignatureNumerator;
		this.timeSignatureDenominator = timeSignatureDenominator;
		this.elements = elements;
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

	/**
	 * template
	 */
	public List<MusicElement> getElements() {
		//TODO
		return musicElements;
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


}
