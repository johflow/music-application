package com.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import static java.util.Map.entry;
import java.util.Set;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.model.Chord;
import com.model.Instrument;
import com.model.Measure;
import com.model.MusicElement;
import com.model.Note;
import com.model.Rest;
import com.model.SheetMusic;
import com.model.Song;
import com.model.Staff;
import com.model.Tuplet;

/**
 * MusicXMLToSongJsonConverter
 *
 *  • Adds tempo to every music element (note, rest, chord, tuplet).
 *  • Tracks <direction><sound tempo="…"> changes at DOM‑order precision,
 *    so an element always inherits the BPM in effect *at the moment it begins*,
 *    even when tempo changes occur mid‑measure.
 *
 *  HOW IT WORKS
 *  -------------
 *  While walking each <measure>, we iterate over *all* children in document
 *  order (not just <note> nodes).  A running {@code currentTempo} integer is
 *  updated whenever we encounter a <direction> containing a tempo attribute,
 *  and that value is stamped onto every element we subsequently create.
 *
 *  The last tempo encountered in a measure is carried forward as the starting
 *  tempo of the next measure, matching normal playback semantics.
 *
 *  Chords and tuplets inherit the tempo of their first note.
 *
 *  Synthetic full‑measure rests inserted for silent voices inherit the
 *  measure‑initial tempo.
 */
public class MusicXMLToSongJsonConverter {

    /** Helper to carry tempo with a note DOM node. */
    private static final class NoteWithTempo {
        final Element note;
        final int tempo;
        NoteWithTempo(Element n, int t) { this.note = n; this.tempo = t; }
    }

    // Default output JSON file path
    private static final String DEFAULT_OUTPUT_JSON = "src/main/java/com/data/songs.json";

    /**
     * Main method that runs the converter as a standalone application.
     * For integration with the UI, use the convertMusicXMLToSong method instead.
     */
    public static void main(String[] args) {
        String inputXml = "everlong.musicxml";
        // Changed to false to preserve existing songs when run from main
        convertMusicXMLToSong(inputXml, DEFAULT_OUTPUT_JSON, false);
    }
    
    /**
     * Converts a MusicXML file to a Song object and adds it to the existing songs.json file.
     * This method can be called from the UI when a user uploads a MusicXML file.
     * 
     * @param musicXmlFilePath Path to the MusicXML file to convert
     * @return The UUID of the newly created song, or null if conversion failed
     */
    public static UUID convertMusicXMLToSong(String musicXmlFilePath) {
        return convertMusicXMLToSong(musicXmlFilePath, DEFAULT_OUTPUT_JSON, false, "Unknown");
    }
    
    /**
     * Converts a MusicXML file to a Song object and adds it to the json file.
     * 
     * @param musicXmlFilePath Path to the MusicXML file to convert
     * @param outputJsonFilePath Path to the output JSON file
     * @param replaceExisting If true, replaces the existing JSON file; if false, adds to it
     * @return The UUID of the newly created song, or null if conversion failed
     */
    public static UUID convertMusicXMLToSong(String musicXmlFilePath, String outputJsonFilePath, boolean replaceExisting) {
        return convertMusicXMLToSong(musicXmlFilePath, outputJsonFilePath, replaceExisting, "Unknown");
    }
    
    /**
     * Converts a MusicXML file to a Song object and adds it to the json file.
     * 
     * @param musicXmlFilePath Path to the MusicXML file to convert
     * @param outputJsonFilePath Path to the output JSON file
     * @param replaceExisting If true, replaces the existing JSON file; if false, adds to it
     * @param composerName The name to use as composer if none found in the file
     * @return The UUID of the newly created song, or null if conversion failed
     */
    public static UUID convertMusicXMLToSong(String musicXmlFilePath, String outputJsonFilePath, boolean replaceExisting, String composerName) {
        UUID songId = null;
        
        try {
            System.out.println("Starting conversion of: " + musicXmlFilePath);
            
            // ──────────────────────────────────────────────────
            // XML parse – disable DTD loading.
            // ──────────────────────────────────────────────────
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setFeature("http://xml.org/sax/features/validation", false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            File inputFile = new File(musicXmlFilePath);
            Document doc = builder.parse(inputFile);
            doc.getDocumentElement().normalize();

            // ──────────────────────────────────────────────────
            // Metadata
            // ──────────────────────────────────────────────────
            String title = extractTitle(doc);
            
            // If no title found in the MusicXML file, use the filename (without extension)
            if (title == null || title.trim().isEmpty()) {
                String fileName = inputFile.getName();
                // Remove the file extension
                int lastDotPos = fileName.lastIndexOf('.');
                if (lastDotPos > 0) {
                    fileName = fileName.substring(0, lastDotPos);
                }
                // Convert file name to title case (e.g., "my_song.musicxml" -> "My Song")
                title = convertFileNameToTitle(fileName);
                System.out.println("No title found in MusicXML. Using filename as title: " + title);
            } else {
                System.out.println("Found title in MusicXML: " + title);
            }
            
            String composer = extractComposer(doc);
            if (composer == null || composer.trim().isEmpty()) {
                composer = composerName;
                System.out.println("No composer found in MusicXML. Using provided composer: " + composer);
            } else {
                System.out.println("Found composer in MusicXML: " + composer);
            }
            
            List<String> genres = new ArrayList<>();
            genres.add("None");

            Map<String,String> partIdToName = extractPartList(doc);

            // ──────────────────────────────────────────────────
            // Process each <part>
            // ──────────────────────────────────────────────────
            List<Staff> staves = new ArrayList<>();
            NodeList partNodes = doc.getElementsByTagName("part");

            for (int p = 0; p < partNodes.getLength(); p++) {
                Element partElement = (Element) partNodes.item(p);

                // Track every voice that appears in this part.
                Set<String> globalVoices = new HashSet<>();
                NodeList allNotes = partElement.getElementsByTagName("note");
                for (int n = 0; n < allNotes.getLength(); n++) {
                    globalVoices.add(getVoiceNumber((Element) allNotes.item(n)));
                }
                if (globalVoices.isEmpty()) globalVoices.add("1");

                // Voice → list<measureJson>
                Map<String, List<Measure>> voiceMeasuresMap = new HashMap<>();

                // Tempo that will apply to *first* element of the first measure
                int currentTempo = 120;

                NodeList measureNodes = partElement.getElementsByTagName("measure");
                for (int m = 0; m < measureNodes.getLength(); m++) {
                    Element measureElement = (Element) measureNodes.item(m);

                    int measureStartTempo = currentTempo; // remember for synthetic rests
                    int keySignature  = extractKeySignature(measureElement);
                    int timeNum       = extractTimeNumerator(measureElement);
                    int timeDen       = extractTimeDenom(measureElement);
                    int divisions     = extractDivisions(measureElement);

                    // ──────────────────────────────────────────────
                    // Pass 1: collect notes in DOM order *with* tempo
                    // ──────────────────────────────────────────────
                    List<NoteWithTempo> orderedNotes = new ArrayList<>();
                    Node child = measureElement.getFirstChild();
                    while (child != null) {
                        if (child.getNodeType() == Node.ELEMENT_NODE) {
                            Element elem = (Element) child;
                            String tag = elem.getTagName();
                            if ("direction".equals(tag)) {
                                Integer tempoDir = tempoFromDirection(elem);
                                if (tempoDir != null) currentTempo = tempoDir;
                            } else if ("note".equals(tag)) {
                                orderedNotes.add(new NoteWithTempo(elem, currentTempo));
                            }
                        }
                        child = child.getNextSibling();
                    }

                    // ──────────────────────────────────────────────
                    // Pass 2: build music elements grouped by voice
                    // ──────────────────────────────────────────────
                    Map<String, List<MusicElement>> measureVoiceElements = new HashMap<>();

                    int k = 0;
                    while (k < orderedNotes.size()) {
                        NoteWithTempo nt = orderedNotes.get(k);
                        Element noteElement = nt.note;
                        int tempoOfNote    = nt.tempo;
                        String voice       = getVoiceNumber(noteElement);

                        // Tuplet start?
                        if (hasTupletStart(noteElement)) {
                            List<NoteWithTempo> tupletGroup = new ArrayList<>();
                            tupletGroup.add(nt);
                            k++;
                            while (k < orderedNotes.size()) {
                                NoteWithTempo next = orderedNotes.get(k);
                                tupletGroup.add(next);
                                if (hasTupletStop(next.note)) { k++; break; }
                                k++;
                            }
                            List<MusicElement> tupletElements = new ArrayList<>();
                            for (NoteWithTempo ntp : tupletGroup) {
                                tupletElements.add(processSingleNote(ntp.note, divisions, ntp.tempo));
                            }
                            // Grab time‑modification from first note
                            Element timeMod = getChildElement(tupletGroup.get(0).note,"time-modification");
                            int act = 1, norm = 1;
                            String nType = null;
                            if (timeMod != null) {
                                try { act  = Integer.parseInt(getElementText(timeMod,"actual-notes")); } catch(Exception ignore){}
                                try { norm = Integer.parseInt(getElementText(timeMod,"normal-notes")); } catch(Exception ignore){}
                                nType = getElementText(timeMod,"normal-type");
                            }
                            double normalVal = getNoteDurationValue(nType);
                            
                            // Create a Tuplet using the constructor
                            Tuplet tuplet = new Tuplet(act, norm, normalVal * act, tupletElements, tempoOfNote);
                            
                            measureVoiceElements
                                .computeIfAbsent(voice, v -> new ArrayList<>())
                                .add(tuplet);
                        }

                        // Chord grouping
                        else if (!hasChordTag(noteElement)) {
                            List<NoteWithTempo> chordGroup = new ArrayList<>();
                            chordGroup.add(nt);
                            // peek ahead
                            while (k + 1 < orderedNotes.size()
                                && hasChordTag(orderedNotes.get(k+1).note)) {
                                chordGroup.add( orderedNotes.get(k+1) );
                                k++;
                            }
                            k++; // advance beyond group

                            List<MusicElement> voiceElements = measureVoiceElements
                                .computeIfAbsent(voice, v -> new ArrayList<>());

                            if (chordGroup.size() == 1) {
                                voiceElements.add(processSingleNote(noteElement, divisions, tempoOfNote));
                            } else {
                                List<Note> chordNotes = new ArrayList<>();
                                for (NoteWithTempo nwp : chordGroup) {
                                    MusicElement element = processSingleNote(nwp.note, divisions, nwp.tempo);
                                    if (element instanceof Note) {
                                        chordNotes.add((Note) element);
                                    }
                                }
                                
                                // Create a Chord using the constructor
                                Chord chord = new Chord(getLyric(chordGroup.get(0).note), chordNotes, tempoOfNote);
                                voiceElements.add(chord);
                            }
                        }

                        // Already‑tagged chord note – will have been handled
                        else { k++; }
                    } // end while orderedNotes

                    // ──────────────────────────────────────────────
                    // Fill missing voices with full‑measure rest
                    // ──────────────────────────────────────────────
                    for (String v : globalVoices) {
                        if (!measureVoiceElements.containsKey(v)) {
                            DurationSymbol ds = fullMeasureRestSymbol(timeNum, timeDen);
                            Rest rest;
                            
                            // Our improved fullMeasureRestSymbol shouldn't return null,
                            // but keeping this check for robustness
                            if (ds != null) {
                                double duration = DURATION_CHAR_TO_DURATION.get(ds.durationChar);
                                rest = new Rest(duration, ds.durationChar.charAt(0), ds.dots, false, "", (double)measureStartTempo);
                            } else {
                                // fallback to quarter rest
                                System.err.println("Could not determine full-measure rest for time signature " + 
                                                  timeNum + "/" + timeDen + ". Using quarter rest.");
                                rest = new Rest(0.25, 'q', 0, false, "", (double)measureStartTempo);
                            }
                            
                            List<MusicElement> restElements = new ArrayList<>();
                            restElements.add(rest);
                            measureVoiceElements.put(v, restElements);
                        }
                    }

                    // ──────────────────────────────────────────────
                    // Pack into measure objects and stash by voice
                    // ──────────────────────────────────────────────
                    for (Map.Entry<String, List<MusicElement>> e : measureVoiceElements.entrySet()) {
                        // Create Measure using the constructor
                        Measure measure = new Measure(keySignature, timeNum, timeDen, e.getValue());
                        
                        voiceMeasuresMap
                            .computeIfAbsent(e.getKey(), vv -> new ArrayList<>())
                            .add(measure);
                    }
                } // end measure loop

                // ──────────────────────────────────────────────
                // Build staves from voices
                // ──────────────────────────────────────────────
                if (voiceMeasuresMap.isEmpty()) {
                    // shouldn't happen now, but keep fallback
                    continue;
                }
                for (Map.Entry<String, List<Measure>> e : voiceMeasuresMap.entrySet()) {
                    // Create Staff using the constructor
                    Staff staff = new Staff("treble", e.getValue());
                    staves.add(staff);
                }
            } // end part loop

            // ──────────────────────────────────────────────────
            // Instrument + song assembly
            // ──────────────────────────────────────────────────
            String firstInstrument = partIdToName.isEmpty()
                ? "Unknown" : partIdToName.values().iterator().next();
            
            // Create Instrument using the constructor
            List<String> clefTypes = new ArrayList<>();
            clefTypes.add("treble");
            Instrument instrument = new Instrument(clefTypes, firstInstrument);

            // Create SheetMusic using the constructor
            SheetMusic sheetMusic = new SheetMusic(instrument, staves);
            
            List<SheetMusic> sheetMusicList = new ArrayList<>();
            sheetMusicList.add(sheetMusic);

            // Create Song using the constructor
            songId = UUID.randomUUID();
            System.out.println("Generated UUID for new song: " + songId);
            
            Song song = new Song(songId, 
                                 title != null ? title : "Converted Song", 
                                 composer != null ? composer : "Unknown", 
                                 0, sheetMusicList);
            song.setGenres(genres);

            // Add the new song to the JSON file
            addSongToJsonFile(song, outputJsonFilePath, replaceExisting);
            
            System.out.println("Conversion successful → " + outputJsonFilePath);
            return songId;

        } catch (Exception ex) {
            System.err.println("Error converting MusicXML file: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Adds a Song to the specified JSON file, either by replacing the existing content
     * or appending to it.
     * 
     * @param song The Song to add
     * @param jsonFilePath Path to the JSON file
     * @param replaceExisting If true, replaces existing content; if false, adds to it
     * @throws Exception If an error occurs during the file operation
     */
    private static void addSongToJsonFile(Song song, String jsonFilePath, boolean replaceExisting) throws Exception {
        // Convert the new song to JSON
        JSONObject newSongJson = convertSongToJsonObject(song);
        
        // Initialize variables for JSON handling
        JSONObject fullJson = new JSONObject();
        JSONArray songsArray = new JSONArray();
        
        // Read existing file if it exists and we're not replacing
        File jsonFile = new File(jsonFilePath);
        if (jsonFile.exists() && !replaceExisting) {
            try {
                // Use a buffered reader for better performance with larger files
                StringBuilder content = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(jsonFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line);
                    }
                }
                
                // Parse the content as JSON
                JSONParser parser = new JSONParser();
                Object parsedObj = parser.parse(content.toString());
                
                if (parsedObj instanceof JSONObject) {
                    fullJson = (JSONObject) parsedObj;
                    Object songsObj = fullJson.get("songs");
                    
                    if (songsObj instanceof JSONArray) {
                        // Copy existing songs to our new array
                        JSONArray existingSongs = (JSONArray) songsObj;
                        System.out.println("Found " + existingSongs.size() + " existing songs");
                        
                        for (Object songObj : existingSongs) {
                            songsArray.add(songObj);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error reading existing JSON, starting fresh: " + e.getMessage());
                // Continue with empty arrays/objects
            }
        }
        
        // Add the new song to the array
        songsArray.add(newSongJson);
        System.out.println("Added new song. Total songs now: " + songsArray.size());
        
        // Put the songs array in the full JSON object
        fullJson.put("songs", songsArray);
        
        // Make sure parent directory exists
        if (jsonFile.getParentFile() != null) {
            jsonFile.getParentFile().mkdirs();
        }
        
        // Write the JSON to the file
        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(fullJson.toJSONString());
            System.out.println("Successfully wrote " + songsArray.size() + " songs to " + jsonFilePath);
        }
    }

    // Helper method to convert our Song model to a JSONObject for saving
    private static JSONObject convertSongToJsonObject(Song song) {
        JSONObject songJson = new JSONObject();
        songJson.put("id", song.getId().toString());
        
        JSONArray genreArray = new JSONArray();
        for (String genre : song.getGenres()) {
            genreArray.add(genre);
        }
        songJson.put("genre", genreArray);
        
        songJson.put("title", song.getTitle());
        songJson.put("composer", song.getComposer());
        songJson.put("publisher", song.getPublisher() != null ? 
            song.getPublisher().getId().toString() : UUID.randomUUID().toString());
        songJson.put("pickUp", song.getPickUp());
        
        JSONArray sheetMusicArray = new JSONArray();
        for (SheetMusic sheet : song.getSheetMusic()) {
            JSONObject sheetJson = convertSheetMusicToJsonObject(sheet);
            sheetMusicArray.add(sheetJson);
        }
        songJson.put("sheetMusic", sheetMusicArray);
        
        return songJson;
    }

    // Helper method to convert SheetMusic to JSONObject
    private static JSONObject convertSheetMusicToJsonObject(SheetMusic sheetMusic) {
        JSONObject sheetJson = new JSONObject();
        
        // Instrument
        JSONObject instrumentJson = new JSONObject();
        instrumentJson.put("instrumentName", sheetMusic.getInstrument().getInstrumentName());
        
        JSONArray clefTypesArray = new JSONArray();
        for (String clefType : sheetMusic.getInstrument().getClefTypes()) {
            clefTypesArray.add(clefType);
        }
        instrumentJson.put("clefTypes", clefTypesArray);
        sheetJson.put("instrument", instrumentJson);
        
        // Staves
        JSONArray stavesArray = new JSONArray();
        for (Staff staff : sheetMusic.getStaves()) {
            JSONObject staffJson = convertStaffToJsonObject(staff);
            stavesArray.add(staffJson);
        }
        sheetJson.put("staves", stavesArray);
        
        return sheetJson;
    }

    // Helper method to convert Staff to JSONObject
    private static JSONObject convertStaffToJsonObject(Staff staff) {
        JSONObject staffJson = new JSONObject();
        staffJson.put("clefType", staff.getClefType());
        
        JSONArray measuresArray = new JSONArray();
        for (Measure measure : staff.getMeasures()) {
            JSONObject measureJson = convertMeasureToJsonObject(measure);
            measuresArray.add(measureJson);
        }
        staffJson.put("measures", measuresArray);
        
        return staffJson;
    }

    // Helper method to convert Measure to JSONObject
    private static JSONObject convertMeasureToJsonObject(Measure measure) {
        JSONObject measureJson = new JSONObject();
        measureJson.put("keySignature", measure.getKeySignature());
        measureJson.put("timeSignatureNumerator", measure.getTimeSignatureNumerator());
        measureJson.put("timeSignatureDenominator", measure.getTimeSignatureDenominator());
        
        JSONArray elementsArray = new JSONArray();
        for (MusicElement element : measure.getMusicElements()) {
            JSONObject elementJson = convertMusicElementToJsonObject(element);
            elementsArray.add(elementJson);
        }
        measureJson.put("musicElements", elementsArray);
        
        return measureJson;
    }

    // Helper method to convert MusicElement to JSONObject
    private static JSONObject convertMusicElementToJsonObject(MusicElement element) {
        JSONObject elementJson = new JSONObject();
        elementJson.put("type", element.getType());
        elementJson.put("tempo", element.getTempo());
        
        if (element instanceof Note note) {
            elementJson.put("noteName", note.getNoteName());
            elementJson.put("midiNumber", note.getMidiNumber());
            elementJson.put("pitch", note.getPitch());
            elementJson.put("duration", note.getDuration());
            elementJson.put("durationChar", String.valueOf(note.getDurationChar()));
            elementJson.put("dotted", note.getDotted());
            elementJson.put("tied", note.hasTie());
            elementJson.put("lyric", note.getLyric());
        } 
        else if (element instanceof Rest rest) {
            elementJson.put("duration", rest.getDuration());
            elementJson.put("durationChar", String.valueOf(rest.getDurationChar()));
            elementJson.put("dotted", rest.getDotted());
            elementJson.put("tied", rest.hasTie());
            elementJson.put("lyric", rest.getLyric());
        } 
        else if (element instanceof Chord chord) {
            elementJson.put("lyric", chord.getLyric());
            
            JSONArray notesArray = new JSONArray();
            for (Note note : chord.getNotes()) {
                JSONObject noteJson = convertMusicElementToJsonObject(note);
                notesArray.add(noteJson);
            }
            elementJson.put("notes", notesArray);
        } 
        else if (element instanceof Tuplet tuplet) {
            elementJson.put("subdivisions", tuplet.getSubdivisions());
            elementJson.put("impliedDivision", tuplet.getImpliedDivision());
            elementJson.put("duration", tuplet.getDuration());
            
            JSONArray elementsArray = new JSONArray();
            for (MusicElement tupletElement : tuplet.getElements()) {
                JSONObject tupletElementJson = convertMusicElementToJsonObject(tupletElement);
                elementsArray.add(tupletElementJson);
            }
            elementJson.put("elements", elementsArray);
        }
        
        return elementJson;
    }

    // ──────────────────────────────────────────────────────────────
    //  Unmodified helper methods
    //  (Only signatures of processSingleNote & processNotes changed)
    // ──────────────────────────────────────────────────────────────

    private static String extractTitle(Document doc) {
        // Try standard MusicXML title tags in order of preference
        // 1. First check for the <work> element's <work-title>
        NodeList workList = doc.getElementsByTagName("work-title");
        if (workList.getLength() > 0 && !workList.item(0).getTextContent().trim().isEmpty()) {
            return workList.item(0).getTextContent().trim();
        }
        
        // 2. Then check for the <movement-title> element
        NodeList movList = doc.getElementsByTagName("movement-title");
        if (movList.getLength() > 0 && !movList.item(0).getTextContent().trim().isEmpty()) {
            return movList.item(0).getTextContent().trim();
        }
        
        // 3. Try the <credit> elements with type="title"
        NodeList creditList = doc.getElementsByTagName("credit");
        for (int i = 0; i < creditList.getLength(); i++) {
            Element credit = (Element) creditList.item(i);
            String type = credit.getAttribute("type");
            if ("title".equalsIgnoreCase(type)) {
                NodeList creditWords = credit.getElementsByTagName("credit-words");
                if (creditWords.getLength() > 0 && !creditWords.item(0).getTextContent().trim().isEmpty()) {
                    return creditWords.item(0).getTextContent().trim();
                }
            }
        }
        
        // 4. Try any <credit> element's <credit-words> as a fallback
        for (int i = 0; i < creditList.getLength(); i++) {
            Element credit = (Element) creditList.item(i);
            NodeList creditWords = credit.getElementsByTagName("credit-words");
            if (creditWords.getLength() > 0) {
                String text = creditWords.item(0).getTextContent().trim();
                if (!text.isEmpty() && text.length() < 100) { // Reasonable length for a title
                    return text;
                }
            }
        }
        
        // No title found in the document
        return null;
    }

    // Pair-object to return both parts
    private static final class DurationSymbol {
        final String durationChar;
        final int dots;
        DurationSymbol(String dc, int d) { durationChar = dc; dots = d; }
    }

    /** Map exponent n (where value = 1/2ⁿ)  → durationChar */
    private static final Map<Integer,String> DUR_CHAR = Map.of(
        0, "w",   // whole
        1, "h",   // half
        2, "q",   // quarter
        3, "i",   // eighth
        4, "s",   // 16th
        5, "t",   // 32nd
        6, "x"    // 64th
    );

    private static final Map<String, Double> DURATION_CHAR_TO_DURATION = Map.of(
        "w", 1.0,
        "h", 0.5,
        "q", 0.25,
        "i", 0.125,
        "s", 0.0625,
        "t", 0.03125,
        "x", 0.015625
    );

    /** Returns the symbol (char + dots) for a full-measure rest, or null if none fits */
    private static DurationSymbol fullMeasureRestSymbol(int num, int den) {
        // Handle common cases first
        if (num == 4 && den == 4) return new DurationSymbol("w", 0); // 4/4 time
        if (num == 3 && den == 4) return new DurationSymbol("h", 1); // 3/4 time
        if (num == 2 && den == 4) return new DurationSymbol("h", 0); // 2/4 time
        if (num == 6 && den == 8) return new DurationSymbol("h", 1); // 6/8 time
        if (num == 9 && den == 8) return new DurationSymbol("h", 1); // 9/8 time
        if (num == 12 && den == 8) return new DurationSymbol("w", 1); // 12/8 time
        
        // For other cases, calculate the fraction of a whole note
        double f = (double) num / den;  // fraction of a whole note
        
        // Safety check for extreme values
        if (f > 4.0) {
            System.out.println("Very large time signature detected: " + num + "/" + den + 
                               " (" + f + " beats). Using whole note.");
            return new DurationSymbol("w", 0);
        }
        
        // Try to find an exact match with standard note durations
        for (int n = 0; n <= 6; n++) {                // up to 64th-notes
            double pow = 1.0 / (1 << n);              // 2⁻ⁿ
            if (Math.abs(f - pow) < 1e-9) {           // exact power of two
                return new DurationSymbol(DUR_CHAR.get(n), 0);
            }
            double dotted = pow + pow / 2;            // dotted value
            if (Math.abs(f - dotted) < 1e-9) {
                return new DurationSymbol(DUR_CHAR.get(n), 1);
            }
        }
        
        // If no exact match, find the closest standard duration
        int closestN = 0;
        double closestDiff = Double.MAX_VALUE;
        boolean needsDot = false;
        
        for (int n = 0; n <= 6; n++) {
            double pow = 1.0 / (1 << n);
            double diff = Math.abs(f - pow);
            if (diff < closestDiff) {
                closestDiff = diff;
                closestN = n;
                needsDot = false;
            }
            
            double dotted = pow + pow / 2;
            diff = Math.abs(f - dotted);
            if (diff < closestDiff) {
                closestDiff = diff;
                closestN = n;
                needsDot = true;
            }
        }
        
        return new DurationSymbol(DUR_CHAR.get(closestN), needsDot ? 1 : 0);
    }


    private static String extractComposer(Document doc) {
        NodeList creatorList = doc.getElementsByTagName("creator");
        for (int i = 0; i < creatorList.getLength(); i++) {
            Element c = (Element) creatorList.item(i);
            if ("composer".equalsIgnoreCase(c.getAttribute("type")))
                return c.getTextContent();
        }
        return null;
    }

    private static Map<String,String> extractPartList(Document doc) {
        Map<String,String> map = new HashMap<>();
        NodeList scoreParts = doc.getElementsByTagName("score-part");
        for (int i = 0; i < scoreParts.getLength(); i++) {
            Element part = (Element) scoreParts.item(i);
            String id = part.getAttribute("id");
            String name = "Unknown";
            NodeList nameList = part.getElementsByTagName("part-name");
            if (nameList.getLength() > 0) name = nameList.item(0).getTextContent();
            map.put(id,name);
        }
        return map;
    }

    // ───────────────── key, time, divisions same as before ─────────────────

    private static int extractKeySignature(Element measureElement) {
        NodeList keyList = measureElement.getElementsByTagName("key");
        if (keyList.getLength() > 0) {
            Element key = (Element) keyList.item(0);
            NodeList fifths = key.getElementsByTagName("fifths");
            if (fifths.getLength() > 0) {
                try { return Integer.parseInt(fifths.item(0).getTextContent()); }
                catch (NumberFormatException ignore) {}
            }
        }
        return 0;
    }

    private static int extractTimeNumerator(Element m) {
        NodeList t = m.getElementsByTagName("time");
        if (t.getLength() > 0) {
            NodeList beats = ((Element)t.item(0)).getElementsByTagName("beats");
            if (beats.getLength() > 0) {
                try { return Integer.parseInt(beats.item(0).getTextContent()); }
                catch (NumberFormatException ignore){}
            }
        }
        return 4;
    }
    private static int extractTimeDenom(Element m) {
        NodeList t = m.getElementsByTagName("time");
        if (t.getLength() > 0) {
            NodeList bt = ((Element)t.item(0)).getElementsByTagName("beat-type");
            if (bt.getLength() > 0) {
                try { return Integer.parseInt(bt.item(0).getTextContent()); }
                catch (NumberFormatException ignore){}
            }
        }
        return 4;
    }
    private static int extractDivisions(Element m) {
        NodeList attrs = m.getElementsByTagName("attributes");
        if (attrs.getLength() > 0) {
            NodeList divs = ((Element)attrs.item(0)).getElementsByTagName("divisions");
            if (divs.getLength() > 0) {
                try { return Integer.parseInt(divs.item(0).getTextContent()); }
                catch(NumberFormatException ignore){}
            }
        }
        return 1;
    }

    // ───────────────── tempo from <direction> helper ─────────────────
    private static Integer tempoFromDirection(Element direction) {
        NodeList soundNodes = direction.getElementsByTagName("sound");
        if (soundNodes.getLength() > 0) {
            Element s = (Element) soundNodes.item(0);
            String tempoStr = s.getAttribute("tempo");
            if (tempoStr != null && !tempoStr.isEmpty()) {
                try { return (int) Double.parseDouble(tempoStr); }
                catch (NumberFormatException ignore){}
            }
        }
        return null;
    }

    /**
     * Convert a beat-length (e.g., 1.0, 0.75, 0.5) into a DurationSymbol.
     * Returns null if the value cannot be written as either a plain power-of-two
     * note (1, 1/2, 1/4 …) or a single-dotted power-of-two (3/4, 3/8 …).
     * For very large durations (>4 beats), defaults to whole note.
     */
    private static DurationSymbol beatsToSymbol(double beats) {
        // Handle extremely large durations by returning a whole note
        if (beats > 4.0) {
            System.out.println("Large duration value (" + beats + " beats) - defaulting to whole note.");
            return new DurationSymbol("w", 0);
        }
        
        final double EPS = 1e-9;                       // floating-point tolerance
        for (int n = 0; n <= 6; n++) {                 // up to 64th-notes
            double pow   = 1.0 / (1 << n);             // 2⁻ⁿ
            double dotted = pow + pow / 2;             // dotted value (3·pow/2)

            if (Math.abs(beats - pow) < EPS) {
                return new DurationSymbol(DUR_CHAR.get(n), 0);
            }
            if (Math.abs(beats - dotted) < EPS) {
                return new DurationSymbol(DUR_CHAR.get(n), 1);
            }
        }
        
        // If no standard symbol matches but duration is reasonable,
        // find the closest basic duration and use that
        if (beats <= 4.0 && beats > 0) {
            // Find the closest power of 2 duration
            int closestN = 0;
            double closestDiff = Double.MAX_VALUE;
            
            for (int n = 0; n <= 6; n++) {
                double pow = 1.0 / (1 << n);
                double diff = Math.abs(beats - pow);
                if (diff < closestDiff) {
                    closestDiff = diff;
                    closestN = n;
                }
            }
            
            return new DurationSymbol(DUR_CHAR.get(closestN), 0);
        }
        
        return null;                                   // nothing matched
    }


    // ───────────────── music element helpers (updated to use constructors) ─────────────────
    private static MusicElement processSingleNote(Element noteElement,
        int divisions, // e.g., divisions=2 means 2 division units per QUARTER note
        int tempo) {
        
        boolean isRest = noteElement.getElementsByTagName("rest").getLength() > 0;
        boolean durationSet = false; // Flag to track if duration/char have been set
        
        double duration = 0.25; // Default duration (quarter note)
        char durationChar = 'q'; // Default duration character
        int dotted = 0; // Default dots
        boolean tied = false;
        String lyric = getLyric(noteElement);

        if (isRest) {
            // --- Try using <type> tag first ---
            String typeText = getElementText(noteElement, "type");
            if (typeText != null) {
                // Use the switch to set duration/char based on typeText
                switch (typeText.toLowerCase()) {
                    case "whole":   durationChar = 'w'; duration = 1.0;    durationSet = true; break;
                    case "half":    durationChar = 'h'; duration = 0.5;    durationSet = true; break;
                    case "quarter": durationChar = 'q'; duration = 0.25;   durationSet = true; break;
                    case "eighth":  durationChar = 'i'; duration = 0.125;  durationSet = true; break;
                    case "16th":    durationChar = 's'; duration = 0.0625; durationSet = true; break;
                    case "32nd":    durationChar = 't'; duration = 0.03125;durationSet = true; break;
                    case "64th":    durationChar = 'x'; duration = 0.015625;durationSet = true; break;
                    // Add other MusicXML types like 'breve', 'longa', '128th' etc. if needed
                    default:
                        System.err.println("Warning: Unrecognized rest type '" + typeText + "'. Using fallback duration.");
                        // Set a fallback if type is unknown but present
                        durationChar = 'w';
                        duration = 0.25; // Default to quarter? Or use duration tag below?
                        // Consider letting it fall through to duration tag logic if type is weird
                        // For now, setting a default based on unknown type.
                        durationSet = true; // Mark duration as set, even if fallback
                        break;

                }
                // If type was recognized (or fallback applied), also handle explicit <dot> elements
                if (durationSet) { // Check if duration was actually set
                    NodeList dotList = noteElement.getElementsByTagName("dot");
                    dotted = dotList.getLength();
                    if (dotted > 0) {
                        // Adjust duration based on the number of dots
                        // Formula for multiple dots: duration * (2 - (1 / 2^dots))
                        // e.g., 1 dot: * (2 - 0.5) = *1.5
                        // e.g., 2 dots: * (2 - 0.25) = *1.75
                        duration = duration * (2.0 - Math.pow(0.5, dotted));
                    }
                } else if (durationSet) {
                    // Handle case where duration wasn't set by switch (e.g. weird type default didn't set it)
                    NodeList dotList = noteElement.getElementsByTagName("dot");
                    dotted = dotList.getLength(); // Still record dots if present
                }
            }

            // --- If <type> didn't set duration (or wasn't present), try using <duration> tag ---
            if (!durationSet) {
                String durStr = getElementText(noteElement, "duration");
                // Ensure divisions is valid to prevent division by zero
                if (durStr != null && divisions > 0) {
                    try {
                        double durationInDivisions = Double.parseDouble(durStr);
                        // Calculate duration relative to a WHOLE note:
                        // (duration / divisions) gives quarter notes. Divide by 4 for whole notes.
                        double beatsRelativeToWhole = durationInDivisions / divisions / 4.0;
                        
                        // Handle extremely large values by capping them
                        if (beatsRelativeToWhole > 50.0) {
                            System.out.println("Extremely large rest duration detected: " + beatsRelativeToWhole + 
                                " beats. Capping at 1 measure (4 beats).");
                            beatsRelativeToWhole = 4.0; // Cap at a maximum of a 4/4 measure
                        }

                        DurationSymbol ds = beatsToSymbol(beatsRelativeToWhole);
                        if (ds != null) {
                            // Found a matching standard symbol (e.g., dotted eighth -> beats=0.1875)
                            durationChar = ds.durationChar.charAt(0);
                            // Use the calculated beats value, which already accounts for the dot effect if ds found one
                            duration = beatsRelativeToWhole;
                            dotted = ds.dots;
                            durationSet = true;
                        } else {
                            // No simple symbol found - this should be less common with our improved beatsToSymbol
                            System.err.println("Warning: Unusual rest duration " + durStr + 
                                " (divisions=" + divisions + ", beatsRelWhole=" + beatsRelativeToWhole + 
                                "). Using whole note with calculated duration.");
                            durationChar = 'w'; // Indicate non-standard symbol representation
                            duration = Math.min(beatsRelativeToWhole, 4.0); // Cap at a whole note
                            // We can't easily determine dots from an arbitrary fraction, assume 0 for the symbol representation.
                            dotted = 0;
                            durationSet = true;
                        }
                    } catch (NumberFormatException | ArithmeticException e) {
                        System.err.println("Error processing duration tag for rest: " + e.getMessage());
                        // Fall through to final fallback
                    }
                }
            }

            // --- Final fallback if NO duration info was found or parsed ---
            if (!durationSet) {
                System.err.println("Error: Could not determine duration for rest element. Using fallback q=0.25.");
                durationChar = 'q';
                duration = 0.25;
                dotted = 0;
                // durationSet = true; // Implicitly set now
            }
            
            // Create and return a Rest object
            return new Rest(duration, durationChar, dotted, tied, lyric, tempo);

        } else { // It's a Note (Not a Rest)
            // --- Process Pitch ---
            String noteName = "C4"; // Default if not found
            int midiNumber = 60; // Default (C4)
            double pitchHz = 261.63; // Default (C4)

            Element pitchElement = getChildElement(noteElement, "pitch");
            if (pitchElement != null) {
                String step = getElementText(pitchElement, "step");     // e.g., "C"
                String octave = getElementText(pitchElement, "octave"); // e.g., "4"
                String alterStr = getElementText(pitchElement, "alter");  // e.g., "1", "-1", null
                String accidental = ""; // e.g., "#", "b"

                if (alterStr != null) {
                    try {
                        int alterVal = Integer.parseInt(alterStr);
                        if (alterVal == 1) accidental = "#";
                        else if (alterVal == -1) accidental = "b";
                        else if (alterVal == 2) accidental = "##"; // Handle double sharp if needed
                        else if (alterVal == -2) accidental = "bb"; // Handle double flat if needed
                    } catch (NumberFormatException ignore) {}
                }

                if (step != null && octave != null) {
                    noteName = step + accidental + octave; // e.g., "C#4"

                    // --- Calculate MIDI/Hz ---
                    Map<String, Integer> semitoneMap = Map.ofEntries(
                        entry("C",0),  entry("B#",0),
                        entry("C#",1), entry("Db",1),
                        entry("D",2),
                        entry("D#",3), entry("Eb",3),
                        entry("E",4),  entry("Fb",4),
                        entry("E#",5), entry("F",5),
                        entry("F#",6), entry("Gb",6),
                        entry("G",7),
                        entry("G#",8), entry("Ab",8),
                        entry("A",9),
                        entry("A#",10),entry("Bb",10),
                        entry("B",11), entry("Cb",11)
                        // Add double sharps/flats if supported and needed
                    );

                    String letter = step + accidental;
                    int octaveInt = 4; // Default octave
                    try { octaveInt = Integer.parseInt(octave); }
                    catch (NumberFormatException ignore) {}

                    // Lookup semitone index (handle case where letter isn't in map?)
                    int semitoneIndex = semitoneMap.getOrDefault(letter, 0); // Default to C if lookup fails?

                    // MIDI number calculation: (Octave + 1) * 12 + Semitone Index relative to C
                    midiNumber = (octaveInt + 1) * 12 + semitoneIndex;
                    // Pitch calculation: A4 = 440 Hz = MIDI 69
                    pitchHz = 440.0 * Math.pow(2, (midiNumber - 69) / 12.0);
                }
            }

            // --- Set Note Duration (Must have type or duration) ---
            // Similar logic as rests: try type, then duration, then fallback
            String typeText = getElementText(noteElement, "type");
            if (typeText != null) {
                // Use switch based on typeText...
                switch (typeText.toLowerCase()) {
                    case "whole":   durationChar = 'w'; duration = 1.0;    durationSet = true; break;
                    case "half":    durationChar = 'h'; duration = 0.5;    durationSet = true; break;
                    case "quarter": durationChar = 'q'; duration = 0.25;   durationSet = true; break;
                    case "eighth":  durationChar = 'i'; duration = 0.125;  durationSet = true; break;
                    case "16th":    durationChar = 's'; duration = 0.0625; durationSet = true; break;
                    case "32nd":    durationChar = 't'; duration = 0.03125;durationSet = true; break;
                    case "64th":    durationChar = 'x'; duration = 0.015625;durationSet = true; break;
                    default:
                        System.err.println("Warning: Unrecognized note type '" + typeText + "'.");
                        // Fall through to try <duration> tag? Or set default? Let's try falling through.
                        break; // Break switch, but durationSet remains false
                }
                // Handle dots ONLY if duration was set by the type
                if (durationSet) {
                    NodeList dotList = noteElement.getElementsByTagName("dot");
                    dotted = dotList.getLength();
                    if (dotted > 0) {
                        double currentDuration = duration;
                        duration = currentDuration * (2.0 - Math.pow(0.5, dotted));
                    }
                }
            }

            // If type didn't set duration (e.g., unrecognized type, or no type tag)
            if (!durationSet) {
                String durStr = getElementText(noteElement, "duration");
                if (durStr != null && divisions > 0) {
                    try {
                        double durationInDivisions = Double.parseDouble(durStr);
                        double beatsRelativeToWhole = durationInDivisions / divisions / 4.0;
                        
                        // Handle extremely large values by capping them
                        if (beatsRelativeToWhole > 50.0) {
                            System.out.println("Extremely large note duration detected: " + beatsRelativeToWhole + 
                                " beats. Capping at 1 measure (4 beats).");
                            beatsRelativeToWhole = 4.0; // Cap at a maximum of a 4/4 measure
                        }
                        
                        DurationSymbol ds = beatsToSymbol(beatsRelativeToWhole);
                        if (ds != null) {
                            // Set duration based on symbol found from <duration> tag
                            durationChar = ds.durationChar.charAt(0);
                            duration = beatsRelativeToWhole;
                            dotted = ds.dots;
                            durationSet = true;
                        } else {
                            // Set duration based on calculated value from <duration> tag
                            System.err.println("Warning: Unusual note duration " + durStr + 
                                " (divisions=" + divisions + ", beatsRelWhole=" + beatsRelativeToWhole + 
                                "). Using whole note with calculated duration.");
                            durationChar = 'w';
                            duration = Math.min(beatsRelativeToWhole, 4.0); // Cap at a whole note
                            dotted = 0;
                            durationSet = true;
                        }
                    } catch (NumberFormatException | ArithmeticException e) {
                        System.err.println("Error processing duration tag for note: " + e.getMessage());
                        // Fall through to final fallback
                    }
                }
            }

            // Final fallback for notes if all else fails (should be rare for valid notes)
            if (!durationSet) {
                System.err.println("Error: Could not determine duration for note element. Using fallback q=0.25.");
                durationChar = 'q';
                duration = 0.25;
                dotted = 0;
            }
            
            // Process tie information
            NodeList tieList = noteElement.getElementsByTagName("tie");
            for (int i = 0; i < tieList.getLength(); i++) {
                Element t = (Element) tieList.item(i);
                // Check specifically for the 'start' type tie indicator
                if ("start".equalsIgnoreCase(t.getAttribute("type"))) {
                    tied = true;
                    break;
                }
            }
            
            // Create and return a Note object
            return new Note(pitchHz, midiNumber, noteName, duration, durationChar, dotted, tied, lyric, tempo);
        } // End if/else isRest
    }


    private static String getElementText(Element parent,String tag) {
        NodeList list = parent.getElementsByTagName(tag);
        if (list.getLength()>0) return list.item(0).getTextContent();
        return null;
    }
    private static String getLyric(Element noteElement) {
        NodeList lyr = noteElement.getElementsByTagName("lyric");
        if (lyr.getLength()>0)
            return getElementText((Element)lyr.item(0),"text");
        return "";
    }
    private static String getVoiceNumber(Element noteElement) {
        NodeList v = noteElement.getElementsByTagName("voice");
        if (v.getLength()>0) return v.item(0).getTextContent().trim();
        return "1";
    }
    private static boolean hasTupletStart(Element noteElement) {
        Element notations = getChildElement(noteElement,"notations");
        if (notations != null) {
            NodeList tuplets = notations.getElementsByTagName("tuplet");
            for (int i=0;i<tuplets.getLength();i++) {
                if ("start".equalsIgnoreCase(
                    ((Element)tuplets.item(i)).getAttribute("type")))
                    return true;
            }
        }
        return false;
    }
    private static boolean hasTupletStop(Element noteElement) {
        Element notations = getChildElement(noteElement,"notations");
        if (notations != null) {
            NodeList tuplets = notations.getElementsByTagName("tuplet");
            for (int i=0;i<tuplets.getLength();i++) {
                if ("stop".equalsIgnoreCase(
                    ((Element)tuplets.item(i)).getAttribute("type")))
                    return true;
            }
        }
        return false;
    }
    private static boolean hasChordTag(Element noteElement) {
        return noteElement.getElementsByTagName("chord").getLength() > 0;
    }
    private static Element getChildElement(Element parent,String tag) {
        NodeList list = parent.getElementsByTagName(tag);
        if (list.getLength()>0) return (Element) list.item(0);
        return null;
    }
    private static double getNoteDurationValue(String noteType) {
        if (noteType == null) return 0.25;
        return switch(noteType.toLowerCase()) {
            case "whole" -> 1.0;
            case "half" -> 0.5;
            case "quarter" -> 0.25;
            case "eighth" -> 0.125;
            case "16th" -> 0.0625;
            case "32nd" -> 0.03125;
            case "64th" -> 0.015625;
            default -> 0.25;
        };
    }
    private static String getFullMeasureRestDurationChar(int num,int den) {
        if (den==4) return "w";
        if (den==8) {
            if (num==6) return "h.";
            StringBuilder sb=new StringBuilder();
            for (int i=0;i<num;i++) sb.append("i");
            return sb.toString();
        }
        if (den==16) {
            StringBuilder sb=new StringBuilder();
            for (int i=0;i<num;i++) sb.append("s");
            return sb.toString();
        }
        return "q";
    }

    /**
     * Converts a filename to a title-case string.
     * Example: "my_song_file" -> "My Song File"
     */
    private static String convertFileNameToTitle(String fileName) {
        // Replace common separators with spaces
        String title = fileName.replaceAll("[_-]", " ");
        
        // Split into words
        String[] words = title.split("\\s+");
        StringBuilder result = new StringBuilder();
        
        // Capitalize first letter of each word
        for (String word : words) {
            if (!word.isEmpty()) {
                // Capitalize first letter, lowercase the rest
                result.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    result.append(word.substring(1).toLowerCase());
                }
                result.append(" ");
            }
        }
        
        return result.toString().trim();
    }

    /**
     * Handles the user action of selecting a MusicXML file.
     * Converts the file to a Song object and adds it to the JSON database.
     * 
     * @param musicXmlFile The MusicXML file selected by the user
     * @return A map containing the result of the conversion,
     *         with "success" (true/false) and "message" (string) entries
     */
    public static Map<String, String> handleMusicXMLFileSelected(File musicXmlFile) {
        return handleMusicXMLFileSelected(musicXmlFile, "Unknown");
    }
    
    /**
     * Handles the user action of selecting a MusicXML file.
     * Converts the file to a Song object and adds it to the JSON database.
     * Uses the provided username as the composer if none is found in the MusicXML file.
     * 
     * @param musicXmlFile The MusicXML file selected by the user
     * @param username The username to use as composer if none is found in the file
     * @return A map containing the result of the conversion,
     *         with "success" (true/false) and "message" (string) entries
     */
    public static Map<String, String> handleMusicXMLFileSelected(File musicXmlFile, String username) {
        Map<String, String> result = new HashMap<>();
        
        try {
            if (musicXmlFile == null) {
                result.put("success", "false");
                result.put("message", "No file selected");
                return result;
            }
            
            String filePath = musicXmlFile.getAbsolutePath();
            UUID songId = convertMusicXMLToSong(filePath, DEFAULT_OUTPUT_JSON, false, username);
            
            if (songId != null) {
                // Read the title and other metadata from the file to return to the UI
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
                factory.setFeature("http://xml.org/sax/features/validation", false);
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(musicXmlFile);
                doc.getDocumentElement().normalize();
                
                String title = extractTitle(doc);
                if (title == null || title.trim().isEmpty()) {
                    String fileName = musicXmlFile.getName();
                    int lastDotPos = fileName.lastIndexOf('.');
                    if (lastDotPos > 0) {
                        fileName = fileName.substring(0, lastDotPos);
                    }
                    title = convertFileNameToTitle(fileName);
                }
                
                String composer = extractComposer(doc);
                if (composer == null || composer.trim().isEmpty()) {
                    composer = username; // Use the logged-in username instead of "Unknown"
                    System.out.println("No composer found in MusicXML. Using provided composer: " + composer);
                } else {
                    System.out.println("Found composer in MusicXML: " + composer);
                }
                
                // Create a map with song information to return to the UI
                Map<String, String> songInfo = new HashMap<>();
                songInfo.put("id", songId.toString());
                songInfo.put("title", title);
                songInfo.put("composer", composer);
                
                result.put("success", "true");
                result.put("message", "Song conversion successful");
                result.putAll(songInfo);
            } else {
                result.put("success", "false");
                result.put("message", "Song conversion failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", "false");
            result.put("message", "An error occurred: " + e.getMessage());
        }
        
        return result;
    }
}
