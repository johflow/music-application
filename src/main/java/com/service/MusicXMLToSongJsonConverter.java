
package com.service;

import static java.util.Map.entry;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import java.io.File;
import java.util.Map;
import java.io.FileWriter;
import java.util.*;

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

    public static void main(String[] args) {

        String inputXml  = "roaring.musicxml";
        String outputJson = "src/main/java/com/data/songs.json";

        try {
            // ──────────────────────────────────────────────────
            // XML parse – disable DTD loading.
            // ──────────────────────────────────────────────────
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setFeature("http://xml.org/sax/features/validation",       false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(inputXml));
            doc.getDocumentElement().normalize();

            // ──────────────────────────────────────────────────
            // Metadata
            // ──────────────────────────────────────────────────
            String title    = extractTitle(doc);
            String composer = extractComposer(doc);
            JSONArray genres = new JSONArray();
            genres.add("None");

            Map<String,String> partIdToName = extractPartList(doc);

            // ──────────────────────────────────────────────────
            // Process each <part>
            // ──────────────────────────────────────────────────
            JSONArray stavesArray = new JSONArray();
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
                Map<String,List<JSONObject>> voiceMeasuresMap = new HashMap<>();

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
                    Map<String,JSONArray> measureVoiceElements = new HashMap<>();

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
                            JSONArray tupletElems = new JSONArray();
                            for (NoteWithTempo ntp : tupletGroup) {
                                tupletElems.add(processSingleNote(ntp.note, divisions, ntp.tempo));
                            }
                            JSONObject tupletJson = new JSONObject();
                            tupletJson.put("type", "tuplet");
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
                            tupletJson.put("subdivisions", act);
                            tupletJson.put("impliedDivision", norm);
                            tupletJson.put("duration", normalVal * act);
                            tupletJson.put("tempo", tempoOfNote);
                            tupletJson.put("elements", tupletElems);

                            measureVoiceElements
                                .computeIfAbsent(voice, v -> new JSONArray())
                                .add(tupletJson);
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

                            JSONArray arr = measureVoiceElements
                                .computeIfAbsent(voice, v -> new JSONArray());

                            if (chordGroup.size() == 1) {
                                arr.add( processSingleNote(noteElement, divisions, tempoOfNote) );
                            } else {
                                JSONObject chordJson = new JSONObject();
                                chordJson.put("type", "chord");
                                chordJson.put("tempo", tempoOfNote);
                                JSONArray chordNotes = new JSONArray();
                                for (NoteWithTempo nwp : chordGroup) {
                                    chordNotes.add(processSingleNote(nwp.note, divisions, nwp.tempo));
                                }
                                chordJson.put("notes", chordNotes);
                                chordJson.put("lyric", getLyric(chordGroup.get(0).note));
                                arr.add(chordJson);
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
                            JSONObject rest = new JSONObject();
                            rest.put("type", "rest");
                            DurationSymbol ds = fullMeasureRestSymbol(timeNum, timeDen);
                            if (ds != null) {
                                rest.put("durationChar", ds.durationChar);
                                rest.put("dotted", ds.dots);
                                double test = DURATION_CHAR_TO_DURATION.get(ds.durationChar);
                                rest.put("duration", DURATION_CHAR_TO_DURATION.get(ds.durationChar));
                            } else {
                                // fallback: build a string of eighth rests, or whatever you prefer
                                rest.put("durationChar", "q");  // default
                                rest.put("dotted", 0);
                                rest.put("duration", .25);
                            }
                            rest.put("tied", false);
                            rest.put("lyric", "");
                            rest.put("tempo", measureStartTempo);
                            JSONArray arr = new JSONArray(); arr.add(rest);
                            measureVoiceElements.put(v, arr);
                        }
                    }

                    // ──────────────────────────────────────────────
                    // Pack into measure JSON and stash by voice
                    // ──────────────────────────────────────────────
                    for (Map.Entry<String,JSONArray> e : measureVoiceElements.entrySet()) {
                        JSONObject measureJson = new JSONObject();
                        measureJson.put("keySignature",            keySignature);
                        measureJson.put("timeSignatureNumerator",  timeNum);
                        measureJson.put("timeSignatureDenominator",timeDen);
                        measureJson.put("musicElements", e.getValue());

                        voiceMeasuresMap
                            .computeIfAbsent(e.getKey(), vv -> new ArrayList<>())
                            .add(measureJson);
                    }
                } // end measure loop

                // ──────────────────────────────────────────────
                // Build staves from voices
                // ──────────────────────────────────────────────
                if (voiceMeasuresMap.isEmpty()) {
                    // shouldn't happen now, but keep fallback
                    continue;
                }
                for (Map.Entry<String,List<JSONObject>> e : voiceMeasuresMap.entrySet()) {
                    JSONObject staffJson = new JSONObject();
                    staffJson.put("clefType","treble");
                    staffJson.put("measures", e.getValue());
                    staffJson.put("voice", e.getKey());
                    stavesArray.add(staffJson);
                }
            } // end part loop

            // ──────────────────────────────────────────────────
            // Instrument + song assembly
            // ──────────────────────────────────────────────────
            String firstInstrument = partIdToName.isEmpty()
                ? "Unknown" : partIdToName.values().iterator().next();
            JSONObject instrumentJson = new JSONObject();
            instrumentJson.put("instrumentName", firstInstrument);
            JSONArray clefTypes = new JSONArray(); clefTypes.add("treble");
            instrumentJson.put("clefTypes", clefTypes);

            JSONObject sheetMusicJson = new JSONObject();
            sheetMusicJson.put("instrument", instrumentJson);
            sheetMusicJson.put("staves", stavesArray);

            JSONObject songJson = new JSONObject();
            songJson.put("id", UUID.randomUUID().toString());
            songJson.put("genre", genres);
            songJson.put("title",    title != null ? title    : "Converted Song");
            songJson.put("composer", composer != null ? composer : "Unknown");
            songJson.put("publisher", UUID.randomUUID().toString());
            songJson.put("pickUp", 0);
            JSONArray sheetArr = new JSONArray(); sheetArr.add(sheetMusicJson);
            songJson.put("sheetMusic", sheetArr);

            JSONObject output = new JSONObject();
            JSONArray songsArr = new JSONArray(); songsArr.add(songJson);
            output.put("songs", songsArr);

            try (FileWriter w = new FileWriter(outputJson)) {
                w.write(output.toJSONString());
            }
            System.out.println("Conversion successful → " + outputJson);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    } // end main

    // ──────────────────────────────────────────────────────────────
    //  Unmodified helper methods
    //  (Only signatures of processSingleNote & processNotes changed)
    // ──────────────────────────────────────────────────────────────

    private static String extractTitle(Document doc) {
        NodeList workList = doc.getElementsByTagName("work-title");
        if (workList.getLength() > 0) return workList.item(0).getTextContent();
        NodeList movList  = doc.getElementsByTagName("movement-title");
        if (movList.getLength() > 0) return movList.item(0).getTextContent();
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
        double f = (double) num / den;                // fraction of a whole note
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
        return null;                                  // nothing matched
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
     */
    private static DurationSymbol beatsToSymbol(double beats) { // This shit doesn't work nor find what I think it needs to find
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
        return null;                                   // no symbol matches
    }


    // ───────────────── music element helpers (unchanged, but add tempo param) ─────────────────
    private static JSONObject processSingleNote(Element noteElement,
        int divisions, // e.g., divisions=2 means 2 division units per QUARTER note
        int tempo) {
        JSONObject noteJson = new JSONObject();
        noteJson.put("tempo", tempo);

        boolean isRest = noteElement.getElementsByTagName("rest").getLength() > 0;
        boolean durationSet = false; // Flag to track if duration/char have been set

        if (isRest) {
            noteJson.put("type", "rest");
            // Rests don't have pitch information
            noteJson.put("noteName", "");
            noteJson.put("midiNumber", 0);
            noteJson.put("pitch", 0.0);

            // --- Try using <type> tag first ---
            String typeText = getElementText(noteElement, "type");
            if (typeText != null) {
                // Use the switch to set duration/char based on typeText
                switch (typeText.toLowerCase()) {
                    case "whole":   noteJson.put("durationChar", "w"); noteJson.put("duration", 1.0);    durationSet = true; break;
                    case "half":    noteJson.put("durationChar", "h"); noteJson.put("duration", 0.5);    durationSet = true; break;
                    case "quarter": noteJson.put("durationChar", "q"); noteJson.put("duration", 0.25);   durationSet = true; break;
                    case "eighth":  noteJson.put("durationChar", "i"); noteJson.put("duration", 0.125);  durationSet = true; break;
                    case "16th":    noteJson.put("durationChar", "s"); noteJson.put("duration", 0.0625); durationSet = true; break;
                    case "32nd":    noteJson.put("durationChar", "t"); noteJson.put("duration", 0.03125);durationSet = true; break;
                    case "64th":    noteJson.put("durationChar", "x"); noteJson.put("duration", 0.015625);durationSet = true; break;
                    // Add other MusicXML types like 'breve', 'longa', '128th' etc. if needed
                    default:
                        System.err.println("Warning: Unrecognized rest type '" + typeText + "'. Using fallback duration.");
                        // Set a fallback if type is unknown but present
                        noteJson.put("durationChar", "w");
                        noteJson.put("duration", 0.25); // Default to quarter? Or use duration tag below?
                        // Consider letting it fall through to duration tag logic if type is weird
                        // For now, setting a default based on unknown type.
                        durationSet = true; // Mark duration as set, even if fallback
                        break;

                }
                // If type was recognized (or fallback applied), also handle explicit <dot> elements
                if (durationSet && noteJson.containsKey("duration")) { // Check if duration was actually set
                    NodeList dotList = noteElement.getElementsByTagName("dot");
                    int dots = dotList.getLength();
                    noteJson.put("dotted", dots);
                    if (dots > 0) {
                        // Adjust duration based on the number of dots
                        double currentDuration = (double) noteJson.get("duration");
                        // Formula for multiple dots: duration * (2 - (1 / 2^dots))
                        // e.g., 1 dot: * (2 - 0.5) = *1.5
                        // e.g., 2 dots: * (2 - 0.25) = *1.75
                        noteJson.put("duration", currentDuration * (2.0 - Math.pow(0.5, dots)));
                    }
                } else if (durationSet) {
                    // Handle case where duration wasn't set by switch (e.g. weird type default didn't set it)
                    NodeList dotList = noteElement.getElementsByTagName("dot");
                    noteJson.put("dotted", dotList.getLength()); // Still record dots if present
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

                        DurationSymbol ds = beatsToSymbol(beatsRelativeToWhole);
                        if (ds != null) {
                            // Found a matching standard symbol (e.g., dotted eighth -> beats=0.1875)
                            noteJson.put("durationChar", ds.durationChar);
                            // Use the calculated beats value, which already accounts for the dot effect if ds found one
                            noteJson.put("duration", beatsRelativeToWhole);
                            noteJson.put("dotted", ds.dots);
                            durationSet = true;
                        } else {
                            // No simple symbol found (e.g., duration was 5 divisions, divisions=2 -> 5/2/4 = 0.625)
                            // Use the calculated numeric duration and a fallback char.
                            System.err.println("Warning: Could not find standard symbol for rest duration " + durStr + " (divisions=" + divisions + ", beatsRelWhole=" + beatsRelativeToWhole + "). Using calculated duration.");
                            noteJson.put("durationChar", "w"); // Indicate non-standard symbol representation
                            noteJson.put("duration", beatsRelativeToWhole);
                            // We can't easily determine dots from an arbitrary fraction, assume 0 for the symbol representation.
                            noteJson.put("dotted", 0);
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
                noteJson.put("durationChar", "q");
                noteJson.put("duration", 0.25);
                noteJson.put("dotted", 0);
                // durationSet = true; // Implicitly set now
            }

        } else { // It's a Note (Not a Rest)
            noteJson.put("type", "note");

            // --- Process Pitch ---
            String noteName = null; // Use null to indicate if pitch was found
            int midiNumber = 0;
            double pitchHz = 0.0;

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

            // Store pitch info (use defaults if pitchElement was null or parsing failed)
            noteJson.put("noteName", (noteName != null) ? noteName : "C4"); // Default if null
            noteJson.put("midiNumber", midiNumber);
            noteJson.put("pitch", pitchHz);


            // --- Set Note Duration (Must have type or duration) ---
            // Similar logic as rests: try type, then duration, then fallback
            String typeText = getElementText(noteElement, "type");
            if (typeText != null) {
                // Use switch based on typeText...
                switch (typeText.toLowerCase()) {
                    case "whole":   noteJson.put("durationChar", "w"); noteJson.put("duration", 1.0);    durationSet = true; break;
                    case "half":    noteJson.put("durationChar", "h"); noteJson.put("duration", 0.5);    durationSet = true; break;
                    case "quarter": noteJson.put("durationChar", "q"); noteJson.put("duration", 0.25);   durationSet = true; break;
                    case "eighth":  noteJson.put("durationChar", "i"); noteJson.put("duration", 0.125);  durationSet = true; break;
                    case "16th":    noteJson.put("durationChar", "s"); noteJson.put("duration", 0.0625); durationSet = true; break;
                    case "32nd":    noteJson.put("durationChar", "t"); noteJson.put("duration", 0.03125);durationSet = true; break;
                    case "64th":    noteJson.put("durationChar", "x"); noteJson.put("duration", 0.015625);durationSet = true; break;
                    default:
                        System.err.println("Warning: Unrecognized note type '" + typeText + "'.");
                        // Fall through to try <duration> tag? Or set default? Let's try falling through.
                        break; // Break switch, but durationSet remains false
                }
                // Handle dots ONLY if duration was set by the type
                if (durationSet) {
                    NodeList dotList = noteElement.getElementsByTagName("dot");
                    int dots = dotList.getLength();
                    noteJson.put("dotted", dots);
                    if (dots > 0) {
                        double currentDuration = (double) noteJson.get("duration");
                        noteJson.put("duration", currentDuration * (2.0 - Math.pow(0.5, dots)));
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
                        DurationSymbol ds = beatsToSymbol(beatsRelativeToWhole);
                        if (ds != null) {
                            // Set duration based on symbol found from <duration> tag
                            noteJson.put("durationChar", ds.durationChar);
                            noteJson.put("duration", beatsRelativeToWhole);
                            noteJson.put("dotted", ds.dots);
                            durationSet = true;
                        } else {
                            // Set duration based on calculated value from <duration> tag
                            System.err.println("Warning: Could not find standard symbol for note duration " + durStr + " (divisions=" + divisions + ", beatsRelWhole=" + beatsRelativeToWhole + "). Using calculated duration.");
                            noteJson.put("durationChar", "w");
                            noteJson.put("duration", beatsRelativeToWhole);
                            noteJson.put("dotted", 0);
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
                noteJson.put("durationChar", "q");
                noteJson.put("duration", 0.25);
                noteJson.put("dotted", 0);
            }
        } // End if/else isRest

        // --- Common Attributes (Tie, Lyric) --- Applies to both notes and rests
        boolean tied = false;
        NodeList tieList = noteElement.getElementsByTagName("tie");
        for (int i = 0; i < tieList.getLength(); i++) {
            Element t = (Element) tieList.item(i);
            // Check specifically for the 'start' type tie indicator
            if ("start".equalsIgnoreCase(t.getAttribute("type"))) {
                tied = true;
                break;
            }
        }
        noteJson.put("tied", tied);
        noteJson.put("lyric", getLyric(noteElement)); // getLyric handles missing lyric tag gracefully

        return noteJson;
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
}
