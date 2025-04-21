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

public class MusicXMLToSongJsonConverter {

    public static void main(String[] args) {

        String inputXml = "Untitled.musicxml";
        String outputJson = "src/main/java/com/data/songs.json";

        try {
            // Set up the XML parser and disable external DTD loading.
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setFeature("http://xml.org/sax/features/validation", false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(inputXml));
            doc.getDocumentElement().normalize();

            // Extract metadata.
            String title = extractTitle(doc);
            String composer = extractComposer(doc);

            // Build a mapping from part id to instrument name.
            Map<String, String> partIdToName = extractPartList(doc);

            // Process each part.
            JSONArray stavesArray = new JSONArray();
            NodeList partNodes = doc.getElementsByTagName("part");
            for (int i = 0; i < partNodes.getLength(); i++) {
                Element partElement = (Element) partNodes.item(i);

                // Determine the global set of voices used in this part.
                Set<String> globalVoices = new HashSet<>();
                NodeList allNotes = partElement.getElementsByTagName("note");
                for (int n = 0; n < allNotes.getLength(); n++) {
                    Element note = (Element) allNotes.item(n);
                    globalVoices.add(getVoiceNumber(note));
                }
                if (globalVoices.isEmpty()) {
                    globalVoices.add("1"); // Default voice.
                }

                // For this part, group measures by voice.
                // Map: voice (String) -> List of measure JSON objects.
                Map<String, List<JSONObject>> voiceMeasuresMap = new HashMap<>();

                NodeList measureNodes = partElement.getElementsByTagName("measure");
                for (int j = 0; j < measureNodes.getLength(); j++) {
                    Element measureElement = (Element) measureNodes.item(j);

                    // Extract measure-level attributes.
                    int keySignature = extractKeySignature(measureElement);
                    int timeNumerator = extractTimeNumerator(measureElement);
                    int timeDenom = extractTimeDenom(measureElement);
                    int tempo = extractTempo(measureElement);
                    int divisions = extractDivisions(measureElement);

                    // For this measure, group note/chord/tuplet elements by voice.
                    Map<String, JSONArray> measureVoiceElements = new HashMap<>();

                    NodeList noteNodes = measureElement.getElementsByTagName("note");
                    int k = 0;
                    while (k < noteNodes.getLength()) {
                        Element noteElement = (Element) noteNodes.item(k);
                        String voice = getVoiceNumber(noteElement);

                        // Check for tuplet grouping.
                        if (hasTupletStart(noteElement)) {
                            List<Element> tupletGroup = new ArrayList<>();
                            tupletGroup.add(noteElement);
                            k++;
                            while (k < noteNodes.getLength()) {
                                Element nextNote = (Element) noteNodes.item(k);
                                tupletGroup.add(nextNote);
                                if (hasTupletStop(nextNote)) {
                                    k++;
                                    break;
                                }
                                k++;
                            }
                            JSONArray tupletElements = new JSONArray();
                            for (Element n : tupletGroup) {
                                tupletElements.add(processSingleNote(n, divisions));
                            }
                            // Use voice from first note in the group.
                            String groupVoice = getVoiceNumber(tupletGroup.get(0));
                            // Extract time-modification info.
                            Element timeMod = getChildElement(tupletGroup.get(0), "time-modification");
                            int actualNotes = 1, normalNotes = 1;
                            String normalType = null;
                            if (timeMod != null) {
                                try {
                                    actualNotes = Integer.parseInt(getElementText(timeMod, "actual-notes"));
                                    normalNotes = Integer.parseInt(getElementText(timeMod, "normal-notes"));
                                } catch (NumberFormatException e) {
                                    actualNotes = 1;
                                    normalNotes = 1;
                                }
                                normalType = getElementText(timeMod, "normal-type");
                            }
                            double normalValue = getNoteDurationValue(normalType);
                            double totalDuration = normalValue * actualNotes;
                            JSONObject tupletJson = new JSONObject();
                            tupletJson.put("type", "tuplet");
                            tupletJson.put("subdivisions", actualNotes);
                            tupletJson.put("impliedDivision", normalNotes);
                            tupletJson.put("duration", totalDuration);
                            tupletJson.put("elements", tupletElements);
                            JSONArray arr = measureVoiceElements.getOrDefault(groupVoice, new JSONArray());
                            arr.add(tupletJson);
                            measureVoiceElements.put(groupVoice, arr);
                        }
                        // Else, if note does not have a chord tag, check ahead for chord grouping.
                        else if (!hasChordTag(noteElement)) {
                            List<Element> chordGroup = new ArrayList<>();
                            chordGroup.add(noteElement);
                            // Look ahead for subsequent notes with a chord tag.
                            while (k + 1 < noteNodes.getLength()) {
                                Element nextNote = (Element) noteNodes.item(k + 1);
                                if (hasChordTag(nextNote)) {
                                    chordGroup.add(nextNote);
                                    k++;
                                } else {
                                    break;
                                }
                            }
                            k++; // Advance past the group.
                            String groupVoice = getVoiceNumber(noteElement);
                            JSONArray arr = measureVoiceElements.getOrDefault(groupVoice, new JSONArray());
                            if (chordGroup.size() == 1) {
                                arr.add(processSingleNote(noteElement, divisions));
                            } else {
                                JSONObject chordJson = new JSONObject();
                                chordJson.put("type", "chord");
                                JSONArray chordNotes = new JSONArray();
                                for (Element n : chordGroup) {
                                    chordNotes.add(processSingleNote(n, divisions));
                                }
                                chordJson.put("notes", chordNotes);
                                chordJson.put("lyric", getLyric(chordGroup.get(0)));
                                arr.add(chordJson);
                            }
                            measureVoiceElements.put(groupVoice, arr);
                        }
                        // If the note already has a chord tag (should be grouped above), skip.
                        else {
                            k++;
                        }
                    } // End while over note nodes.

                    // For any global voice that is missing in this measure, insert a full-measure rest.
                    for (String voice : globalVoices) {
                        if (!measureVoiceElements.containsKey(voice)) {
                            // Create a rest note filling the measure.
                            JSONObject restNote = new JSONObject();
                            restNote.put("type", "rest");
                            String fullMeasureRestDuration = getFullMeasureRestDurationChar(timeNumerator, timeDenom);
                            restNote.put("durationChar", fullMeasureRestDuration);

                            restNote.put("noteName", "");
                            restNote.put("midiNumber", 0);
                            restNote.put("pitch", 0.0);
                            restNote.put("dotted", 0);
                            restNote.put("tied", false);
                            restNote.put("lyric", "");
                            restNote.put("duration", 0);
                            JSONArray arr = new JSONArray();
                            arr.add(restNote);
                            measureVoiceElements.put(voice, arr);
                        }
                    }

                    // Now, for each voice present in this measure, create a measure JSON object and add it.
                    for (Map.Entry<String, JSONArray> entry : measureVoiceElements.entrySet()) {
                        JSONObject measureJson = new JSONObject();
                        measureJson.put("keySignature", keySignature);
                        measureJson.put("timeSignatureNumerator", timeNumerator);
                        measureJson.put("timeSignatureDenominator", timeDenom);
                        measureJson.put("tempo", tempo);
                        measureJson.put("musicElements", entry.getValue());
                        String voiceKey = entry.getKey();
                        List<JSONObject> measureList = voiceMeasuresMap.getOrDefault(voiceKey, new ArrayList<>());
                        measureList.add(measureJson);
                        voiceMeasuresMap.put(voiceKey, measureList);
                    }
                } // End for each measure.

                // For the current part, create a staff (voice) JSON object for each voice.
                if (voiceMeasuresMap.isEmpty()) {
                    // No voice info encountered: create one default staff.
                    List<JSONObject> defaultMeasures = new ArrayList<>();
                    NodeList measureNodesDefault = partElement.getElementsByTagName("measure");
                    for (int j = 0; j < measureNodesDefault.getLength(); j++) {
                        Element measureElement = (Element) measureNodesDefault.item(j);
                        JSONObject measureJson = new JSONObject();
                        measureJson.put("keySignature", extractKeySignature(measureElement));
                        measureJson.put("timeSignatureNumerator", extractTimeNumerator(measureElement));
                        measureJson.put("timeSignatureDenominator", extractTimeDenom(measureElement));
                        measureJson.put("tempo", extractTempo(measureElement));
                        JSONArray notesArray = processNotes(measureElement, extractDivisions(measureElement));
                        measureJson.put("musicElements", notesArray);
                        defaultMeasures.add(measureJson);
                    }
                    JSONObject staffJson = new JSONObject();
                    staffJson.put("clefType", "treble");
                    staffJson.put("measures", defaultMeasures);
                    staffJson.put("voice", "1");
                    stavesArray.add(staffJson);
                } else {
                    for (Map.Entry<String, List<JSONObject>> entry : voiceMeasuresMap.entrySet()) {
                        JSONObject staffJson = new JSONObject();
                        staffJson.put("clefType", "treble");
                        staffJson.put("measures", entry.getValue());
                        staffJson.put("voice", entry.getKey());
                        stavesArray.add(staffJson);
                    }
                }
            } // End for each part.

            // Build the instrument JSON object.
            String firstInstrument = partIdToName.isEmpty() ? "Unknown" : partIdToName.values().iterator().next();
            JSONObject instrumentJson = new JSONObject();
            instrumentJson.put("instrumentName", firstInstrument);
            JSONArray clefTypes = new JSONArray();
            clefTypes.add("treble");
            instrumentJson.put("clefTypes", clefTypes);

            // Build the sheetMusic JSON object.
            JSONObject sheetMusicJson = new JSONObject();
            sheetMusicJson.put("instrument", instrumentJson);
            JSONArray stavesWrapper = new JSONArray();
            stavesWrapper.addAll(stavesArray);
            sheetMusicJson.put("staves", stavesWrapper);

            // Build the song JSON object.
            JSONObject songJson = new JSONObject();
            songJson.put("id", UUID.randomUUID().toString());
            songJson.put("title", title != null ? title : "Converted Song");
            songJson.put("composer", composer != null ? composer : "Unknown");
            songJson.put("publisher", UUID.randomUUID().toString());
            songJson.put("pickUp", 0);
            JSONArray sheetMusicArray = new JSONArray();
            sheetMusicArray.add(sheetMusicJson);
            songJson.put("sheetMusic", sheetMusicArray);

            // Wrap the song in a songs array.
            JSONObject output = new JSONObject();
            JSONArray songsArray = new JSONArray();
            songsArray.add(songJson);
            output.put("songs", songsArray);

            // Write the JSON output to file.
            try (FileWriter writer = new FileWriter(outputJson)) {
                writer.write(output.toJSONString());
            }
            System.out.println("Conversion successful. Output written to " + outputJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper: Extract title from <work-title> or <movement-title>.
    private static String extractTitle(Document doc) {
        NodeList workList = doc.getElementsByTagName("work-title");
        if (workList.getLength() > 0) {
            return workList.item(0).getTextContent();
        }
        NodeList movementList = doc.getElementsByTagName("movement-title");
        if (movementList.getLength() > 0) {
            return movementList.item(0).getTextContent();
        }
        return null;
    }

    // Helper: Extract composer from <creator type="composer">.
    private static String extractComposer(Document doc) {
        NodeList creatorList = doc.getElementsByTagName("creator");
        for (int i = 0; i < creatorList.getLength(); i++) {
            Element creator = (Element) creatorList.item(i);
            if ("composer".equalsIgnoreCase(creator.getAttribute("type"))) {
                return creator.getTextContent();
            }
        }
        return null;
    }

    // Helper: Build a map from part id to instrument name using the <score-part> list.
    private static Map<String, String> extractPartList(Document doc) {
        Map<String, String> map = new HashMap<>();
        NodeList scoreParts = doc.getElementsByTagName("score-part");
        for (int i = 0; i < scoreParts.getLength(); i++) {
            Element part = (Element) scoreParts.item(i);
            String id = part.getAttribute("id");
            NodeList partNameList = part.getElementsByTagName("part-name");
            String name = partNameList.getLength() > 0 ? partNameList.item(0).getTextContent() : "Unknown";
            map.put(id, name);
        }
        return map;
    }

    // Helper: Extract key signature (from <attributes><key><fifths>).
    private static int extractKeySignature(Element measureElement) {
        NodeList keyList = measureElement.getElementsByTagName("key");
        if (keyList.getLength() > 0) {
            Element keyElement = (Element) keyList.item(0);
            NodeList fifthsList = keyElement.getElementsByTagName("fifths");
            if (fifthsList.getLength() > 0) {
                try {
                    return Integer.parseInt(fifthsList.item(0).getTextContent());
                } catch (NumberFormatException e) {}
            }
        }
        return 0;
    }

    // Helper: Extract time signature numerator (from <time><beats>).
    private static int extractTimeNumerator(Element measureElement) {
        NodeList timeList = measureElement.getElementsByTagName("time");
        if (timeList.getLength() > 0) {
            Element timeElement = (Element) timeList.item(0);
            NodeList beatsList = timeElement.getElementsByTagName("beats");
            if (beatsList.getLength() > 0) {
                try {
                    return Integer.parseInt(beatsList.item(0).getTextContent());
                } catch (NumberFormatException e) {}
            }
        }
        return 4;
    }

    // Helper: Extract time signature denominator (from <time><beat-type>).
    private static int extractTimeDenom(Element measureElement) {
        NodeList timeList = measureElement.getElementsByTagName("time");
        if (timeList.getLength() > 0) {
            Element timeElement = (Element) timeList.item(0);
            NodeList beatTypeList = timeElement.getElementsByTagName("beat-type");
            if (beatTypeList.getLength() > 0) {
                try {
                    return Integer.parseInt(beatTypeList.item(0).getTextContent());
                } catch (NumberFormatException e) {}
            }
        }
        return 4;
    }

    // Helper: Extract tempo from <sound tempo="..."> or inside <direction>.
    private static int extractTempo(Element measureElement) {
        NodeList soundList = measureElement.getElementsByTagName("sound");
        if (soundList.getLength() > 0) {
            Element soundElement = (Element) soundList.item(0);
            String tempoStr = soundElement.getAttribute("tempo");
            if (tempoStr != null && !tempoStr.isEmpty()) {
                try {
                    return (int) Double.parseDouble(tempoStr);
                } catch (NumberFormatException e) {}
            }
        }
        NodeList directionList = measureElement.getElementsByTagName("direction");
        for (int i = 0; i < directionList.getLength(); i++) {
            Element direction = (Element) directionList.item(i);
            NodeList soundNodes = direction.getElementsByTagName("sound");
            if (soundNodes.getLength() > 0) {
                Element soundElement = (Element) soundNodes.item(0);
                String tempoStr = soundElement.getAttribute("tempo");
                if (tempoStr != null && !tempoStr.isEmpty()) {
                    try {
                        return (int) Double.parseDouble(tempoStr);
                    } catch (NumberFormatException e) {}
                }
            }
        }
        return 120;
    }

    // Helper: Extract the divisions value (used for note duration scaling).
    private static int extractDivisions(Element measureElement) {
        NodeList attributesList = measureElement.getElementsByTagName("attributes");
        if (attributesList.getLength() > 0) {
            Element attrElement = (Element) attributesList.item(0);
            NodeList divisionsList = attrElement.getElementsByTagName("divisions");
            if (divisionsList.getLength() > 0) {
                try {
                    return Integer.parseInt(divisionsList.item(0).getTextContent());
                } catch (NumberFormatException e) {}
            }
        }
        return 1;
    }

    // Fallback processing for measures without voice info.
    private static JSONArray processNotes(Element measureElement, int divisions) {
        JSONArray musicElements = new JSONArray();
        NodeList noteNodes = measureElement.getElementsByTagName("note");
        List<List<Element>> groups = new ArrayList<>();
        for (int i = 0; i < noteNodes.getLength(); i++) {
            Element noteElement = (Element) noteNodes.item(i);
            if (!hasChordTag(noteElement)) {
                List<Element> group = new ArrayList<>();
                group.add(noteElement);
                groups.add(group);
            } else {
                if (!groups.isEmpty()) {
                    groups.get(groups.size() - 1).add(noteElement);
                } else {
                    List<Element> group = new ArrayList<>();
                    group.add(noteElement);
                    groups.add(group);
                }
            }
        }
        for (List<Element> group : groups) {
            if (group.size() == 1) {
                JSONObject noteJson = processSingleNote(group.get(0), divisions);
                musicElements.add(noteJson);
            } else {
                JSONObject chordJson = new JSONObject();
                chordJson.put("type", "chord");
                JSONArray chordNotes = new JSONArray();
                for (Element noteElem : group) {
                    JSONObject noteJson = processSingleNote(noteElem, divisions);
                    chordNotes.add(noteJson);
                }
                chordJson.put("notes", chordNotes);
                chordJson.put("lyric", getLyric(group.get(0)));
                musicElements.add(chordJson);
            }
        }
        return musicElements;
    }

    // Helper: Check if a note element has a <chord/> child.
    private static boolean hasChordTag(Element noteElement) {
        NodeList chordList = noteElement.getElementsByTagName("chord");
        return chordList.getLength() > 0;
    }

    private static JSONObject processSingleNote(Element noteElement, int divisions) {
        JSONObject noteJson = new JSONObject();
        NodeList restList = noteElement.getElementsByTagName("rest");
        if (restList.getLength() > 0) {
            noteJson.put("type", "rest");
            // rest logic...
        } else {
            noteJson.put("type", "note");
        }

        String noteName;
        if (restList.getLength() == 0) {
            Element pitchElement = (Element) noteElement.getElementsByTagName("pitch").item(0);
            if (pitchElement != null) {
                String step   = getElementText(pitchElement, "step");
                String octave = getElementText(pitchElement, "octave");
                String alter  = getElementText(pitchElement, "alter");

                // build noteName e.g. "C#4" or "Bb3"
                String accidental = "";
                if (alter != null) {
                    try {
                        int alt = Integer.parseInt(alter);
                        if (alt == 1)      accidental = "#";
                        else if (alt == -1) accidental = "b";
                    } catch (NumberFormatException ignored) {}
                }
                noteName = step + accidental + octave;
            } else {
                noteName = "C4";   // fallback
            }
        } else {
            noteName = "";
        }

        noteJson.put("noteName", noteName);

        // ────────────────────────────────────────────────────────────────────────
        // compute MIDI number and pitch from noteName
        // ────────────────────────────────────────────────────────────────────────
        if (!noteName.isEmpty()) {
            // map from note letters (with accidentals) to semitone 0–11
            Map<String,Integer> semitoneMap = Map.ofEntries(
                entry("C",  0), entry("B#",  0),
                entry("C#", 1), entry("Db",  1),
                entry("D",  2),
                entry("D#", 3), entry("Eb",  3),
                entry("E",  4), entry("Fb",  4),
                entry("E#", 5), entry("F",   5),
                entry("F#", 6), entry("Gb",  6),
                entry("G",  7),
                entry("G#", 8), entry("Ab",  8),
                entry("A",  9),
                entry("A#",10), entry("Bb", 10),
                entry("B", 11), entry("Cb", 11)
            );

            // split noteName into letter+accidental vs. octave
            int len = noteName.length();
            // octave is last char (or two, for 10+?), but MusicXML uses single-digit octaves
            String octaveStr = noteName.substring(len - 1);
            String letterPart = noteName.substring(0, len - 1);

            int octaveInt = 4; // default
            try {
                octaveInt = Integer.parseInt(octaveStr);
            } catch (NumberFormatException ignored) {}

            int semitoneIndex = semitoneMap.getOrDefault(letterPart, 0);
            int midiNumber    = (octaveInt + 1) * 12 + semitoneIndex;
            double pitchHz    = 440.0 * Math.pow(2, (midiNumber - 69) / 12.0);

            noteJson.put("midiNumber", midiNumber);
            noteJson.put("pitch", pitchHz);
        } else {
            // for rests or unknowns
            noteJson.put("midiNumber", 0);
            noteJson.put("pitch", 0.0);
        }
        // ────────────────────────────────────────────────────────────────────────

        // durationChar, dotted, tied, lyric, etc.
        String typeText = getElementText(noteElement, "type");
        if (typeText != null) {
            switch (typeText.toLowerCase()) {
                case "whole"   -> {noteJson.put("durationChar", "w"); noteJson.put("duration", 1);}
                case "half"    -> {noteJson.put("durationChar", "h"); noteJson.put("duration", 0.5);}
                case "quarter" -> {noteJson.put("durationChar", "q"); noteJson.put("duration", 0.25);}
                case "eighth"  -> {noteJson.put("durationChar", "i"); noteJson.put("duration", 0.125);}
                case "16th"    -> {noteJson.put("durationChar", "s"); noteJson.put("duration", 0.0625);}
                case "32nd"    -> {noteJson.put("durationChar", "t"); noteJson.put("duration", 0.03125);}
                case "64th"    -> {noteJson.put("durationChar", "x"); noteJson.put("duration", 0.015625);}
                default        -> {noteJson.put("durationChar", "q"); noteJson.put("duration", 0.0078125);}
            }
        }
        NodeList dotList = noteElement.getElementsByTagName("dot");
        noteJson.put("dotted", dotList.getLength());

        boolean tied = false;
        NodeList tieList = noteElement.getElementsByTagName("tie");
        for (int i = 0; i < tieList.getLength(); i++) {
            Element tieElem = (Element) tieList.item(i);
            if ("start".equalsIgnoreCase(tieElem.getAttribute("type"))) {
                tied = true;
                break;
            }
        }
        noteJson.put("tied", tied);
        noteJson.put("lyric", getLyric(noteElement));

        return noteJson;
    }


    // Helper: Get text content of a child element by tag name.
    private static String getElementText(Element parent, String tagName) {
        NodeList list = parent.getElementsByTagName(tagName);
        if (list.getLength() > 0) {
            return list.item(0).getTextContent();
        }
        return null;
    }

    // Helper: Extract lyric text from a <lyric> element if present.
    private static String getLyric(Element noteElement) {
        NodeList lyricList = noteElement.getElementsByTagName("lyric");
        if (lyricList.getLength() > 0) {
            Element lyricElement = (Element) lyricList.item(0);
            return getElementText(lyricElement, "text");
        }
        return "";
    }

    // Helper: Determine the voice number for a note element (default "1" if not present).
    private static String getVoiceNumber(Element noteElement) {
        NodeList voiceList = noteElement.getElementsByTagName("voice");
        if (voiceList.getLength() > 0) {
            return voiceList.item(0).getTextContent().trim();
        }
        return "1";
    }

    // Helper: Check if a note's notations contain a <tuplet> element with type "start".
    private static boolean hasTupletStart(Element noteElement) {
        Element notations = getChildElement(noteElement, "notations");
        if (notations != null) {
            NodeList tupletList = notations.getElementsByTagName("tuplet");
            for (int i = 0; i < tupletList.getLength(); i++) {
                Element tuplet = (Element) tupletList.item(i);
                String type = tuplet.getAttribute("type");
                if ("start".equalsIgnoreCase(type)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Helper: Check if a note's notations contain a <tuplet> element with type "stop".
    private static boolean hasTupletStop(Element noteElement) {
        Element notations = getChildElement(noteElement, "notations");
        if (notations != null) {
            NodeList tupletList = notations.getElementsByTagName("tuplet");
            for (int i = 0; i < tupletList.getLength(); i++) {
                Element tuplet = (Element) tupletList.item(i);
                String type = tuplet.getAttribute("type");
                if ("stop".equalsIgnoreCase(type)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Helper: Get the first child element with the given tag name.
    private static Element getChildElement(Element parent, String tagName) {
        NodeList list = parent.getElementsByTagName(tagName);
        if (list.getLength() > 0) {
            return (Element) list.item(0);
        }
        return null;
    }

    // Helper: Map a note type string to its numeric duration value.
    // Mappings: quarter → 0.25, half → 0.5, whole → 1, eighth → 0.125, 16th → 0.0625, 32nd → 0.03125, 64th → 0.015625.
    private static double getNoteDurationValue(String noteType) {
        if (noteType == null) return 0.25;
        switch (noteType.toLowerCase()) {
            case "whole":
                return 1.0;
            case "half":
                return 0.5;
            case "quarter":
                return 0.25;
            case "eighth":
                return 0.125;
            case "16th":
                return 0.0625;
            case "32nd":
                return 0.03125;
            case "64th":
                return 0.015625;
            default:
                return 0.25;
        }
    }

    // Helper method to determine the duration character for a full-measure rest based on the time signature.
    private static String getFullMeasureRestDurationChar(int numerator, int denominator) {
        // For 4/4 time, a full measure is a whole note rest.
        if (denominator == 4) {
            return "w";
        }
        // For 6/8, use a dotted half rest ("h.") if the measure contains 6 eighth notes.
        else if (denominator == 8) {
            if (numerator == 6) {
                return "h.";
            } else {
                // Otherwise, construct a string of "i" repeated 'numerator' times.
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < numerator; i++) {
                    sb.append("i");
                }
                return sb.toString();
            }
        }
        // For 16th-based time signatures.
        else if (denominator == 16) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < numerator; i++) {
                sb.append("s");
            }
            return sb.toString();
        }
        // Default: fallback to quarter note rest.
        else {
            return "q";
        }
    }

}
