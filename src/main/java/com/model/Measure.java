package com.model;

import java.util.List;

/**
 * Represents a measure in sheet music containing musical elements and properties
 */
public class Measure {
    private int keySignature;
    private int timeSignatureNumerator;
    private int timeSignatureDenominator;
    private List<MusicElement> musicElements;
    
    /**
     * Constructor for the Measure class
     * 
     * @param keySignature Key signature for this measure
     * @param timeSignatureNumerator Top number of the time signature
     * @param timeSignatureDenominator Bottom number of the time signature
     * @param musicElements List of musical elements contained in this measure
     */
    public Measure(int keySignature, int timeSignatureNumerator, int timeSignatureDenominator, List<MusicElement> musicElements) {
        this.keySignature = keySignature;
        this.timeSignatureNumerator = timeSignatureNumerator;
        this.timeSignatureDenominator = timeSignatureDenominator;
        this.musicElements = musicElements;
    }
    
    /**
     * Adds a musical element to this measure
     * 
     * @param element The MusicElement to add to the measure
     */
    public void addMusicElement(MusicElement element) {
        musicElements.add(element);
    }
    
    /**
     * Gets the key signature for this measure
     * 
     * @return The key signature value
     */
    public int getKeySignature() {
        return keySignature;
    }
    
    /**
     * Sets the key signature for this measure
     * 
     * @param keySignature The key signature value to set
     */
    public void setKeySignature(int keySignature) {
        this.keySignature = keySignature;
    }
    
    /**
     * Gets the numerator of the time signature
     * 
     * @return The time signature numerator
     */
    public int getTimeSignatureNumerator() {
        return timeSignatureNumerator;
    }
    
    /**
     * Sets the numerator of the time signature
     * 
     * @param timeSignatureNumerator The time signature numerator to set
     */
    public void setTimeSignatureNumerator(int timeSignatureNumerator) {
        this.timeSignatureNumerator = timeSignatureNumerator;
    }
    
    /**
     * Gets the denominator of the time signature
     * 
     * @return The time signature denominator
     */
    public int getTimeSignatureDenominator() {
        return timeSignatureDenominator;
    }
    
    /**
     * Sets the denominator of the time signature
     * 
     * @param timeSignatureDenominator The time signature denominator to set
     */
    public void setTimeSignatureDenominator(int timeSignatureDenominator) {
        this.timeSignatureDenominator = timeSignatureDenominator;
    }
    
    /**
     * Gets the list of music elements in this measure
     * 
     * @return List of music elements
     */
    public List<MusicElement> getMusicElements() {
        return musicElements;
    }
    
    /**
     * Sets the list of music elements in this measure
     * 
     * @param musicElements List of music elements to set
     */
    public void setMusicElements(List<MusicElement> musicElements) {
        this.musicElements = musicElements;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("| ");
        for (MusicElement musicElement : musicElements) {
            stringBuilder.append(musicElement).append(" ");
        }
        stringBuilder.append("|");
        return stringBuilder.toString();
    }
}
