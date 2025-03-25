package com.model;

import java.util.List;

/**
 * template
 * 
 * @author 
 */
public class Instrument {
    private List<String> clefTypes;
    private String instrumentName;

    public Instrument(List<String> clefTypes, String instrumentName) {
        this.clefTypes = clefTypes;
        this.instrumentName = instrumentName;
    }

    public List<String> getClefTypes() {
        return clefTypes;
    }

    public void setClefTypes(List<String> clefTypes) {
        this.clefTypes = clefTypes;
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
    }

    /**
     * Adds a single clef type to the list of supported clefs
     * @param clefType Clef type to add
     * @return true if added successfully
     */
    public boolean addClefType(String clefType) {
        if (!clefTypes.contains(clefType)) {
            return clefTypes.add(clefType);
        }
        return false;
    }
    
    /**
     * Removes a clef type from the list of supported clefs
     * @param clefType Clef type to remove
     * @return true if removed successfully
     */
    public boolean removeClefType(String clefType) {
        return clefTypes.remove(clefType);
    } 

    @Override
    public String toString() {
        return instrumentName;
    }
}
