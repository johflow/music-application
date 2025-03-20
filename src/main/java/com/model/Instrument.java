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
}
