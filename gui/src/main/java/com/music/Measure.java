package com.music;
import java.util.ArrayList;

public class Measure {
	private KeySignature keySignature;
	private int timeSignatureNumerator;
	private int timeSignatureDenominator;
	private ArrayList<Chord> notes;
	private int measureNumber;
	private double tempo;

	public Measure(KeySignature keySignature, int timeSignatureNumerator, int timeSignatureDenominator, double tempo) {
		//TODO
	}

	public void addNote(int pitch, double duration) {
		//TODO
	}

	public void delete() {
		//TODO
	}

	public Measure selectMeasure() {
		//TODO
	}

	public ArrayList<Note> getNotes() {
		//TODO
	}

	public void setTimeSignature(int timeSignatureNumerator, int timeSignatureDenominator) {
		//TODO
	}

	public void setKeySignature(String keySignature) {
		//TODO
	}
}
