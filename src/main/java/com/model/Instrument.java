package com.model;

import java.util.List;

/**
 * template
 * 
 * @author 
 */
public class Instrument {
    private List<String> cleftTypes;
    private String instrumentName;

    public Instrument(List<String> clefTypes, String instrumentName) {
        this.cleftTypes = clefTypes;
        this.instrumentName = instrumentName;
    }

    public List<String> getCleftTypes() {
        return cleftTypes;
    }

    public void setCleftTypes(List<String> cleftTypes) {
        this.cleftTypes = cleftTypes;
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
    }
}
