package main;

import java.util.HashMap;

public class MusicPlayer {

    private final Controller playMediaInstance = new Controller();

    public void playSymbols(String input, HashMap<String, String> symbolToNote) throws InterruptedException {
        // Split by whitespaces
        // And whenever is a line break, it will append \n in the beginning of the next line
        String[] sequences = input.split("(?=\\n)|\\s+");
        for (int i = 0; i < sequences.length; ++i) {
            String seq = sequences[i];
            // Rule 10
            if(seq.contains("\n")) {
                Thread.sleep(3000);
                seq = seq.replaceAll("\\s+", "");
            }
            if (seq.startsWith("[") && seq.endsWith("]")) {
                // Remove brackets
                String group = seq.substring(1, seq.length() - 1);
                if (group.contains("/")) {
                    // Rule 2
                    playFastNotes(group, symbolToNote);
                } else {
                    // Rule 1
                    playSimultaneous(group, symbolToNote);
                    // Rule 5
                    if(i + 1 < sequences.length && sequences[i + 1].startsWith("[") &&
                            sequences[i + 1].endsWith("]")) {
                        Thread.sleep(430);
                    }
                }
            } else if (seq.contains("|")) {
                if(seq.contains("/")) {
                    // Rule 9
                    if(seq.indexOf("|") != seq.lastIndexOf("|")) {
                        handlePause(seq.substring(0, seq.indexOf("|")), symbolToNote);
                        Thread.sleep(2100);
                        handlePause(seq.substring(seq.lastIndexOf("|") + 1), symbolToNote);
                    } else {
                        // Rule 8
                        handlePause(seq.substring(0, seq.indexOf("/")), symbolToNote);
                        Thread.sleep(1500);
                        handlePause(seq.substring(seq.lastIndexOf("/") + 1), symbolToNote);
                    }
                } else {
                    // Rules 6, 7
                    handlePause(seq, symbolToNote);
                }
            } else {
                for (char note : seq.toCharArray()) {
                    playMediaInstance.playMedia(symbolToNote.get("" + note), "#");
                    if(seq.length() > 1) {
                        Thread.sleep(200); // Rule 3
                    } else {
                        Thread.sleep(430); // Rule 4
                    }
                }
            }
        }
    }

    private void playFastNotes(String group, HashMap<String, String> symbolToNote) throws InterruptedException {
        // Rule 2
        String[] notes = group.split("/");
        for (String note : notes) {
            playMediaInstance.playMedia(symbolToNote.get(note), "#");
            Thread.sleep(150);
        }
    }

    private void playSimultaneous(String group, HashMap<String, String> symbolToNote) {
        // Rule 1
        String[] notes = group.split("");
        for (String note : notes) {
            playMediaInstance.playMedia(symbolToNote.get(note), "#");
        }
    }

    private void handlePause(String sequence, HashMap<String, String> symbolToNote) throws InterruptedException {
        // Rules 6 to 9
        String[] parts = sequence.split("\\|");
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            if (!part.isEmpty()) {
                for (char note : part.toCharArray()) {
                    playMediaInstance.playMedia(symbolToNote.get("" + note), "#");
                    if(part.length() > 1) {
                        Thread.sleep(200); // Rule 3
                    } else {
                        Thread.sleep(430); // Rule 4
                    }
                }
            }
            if (i < parts.length - 1) {
                long pauseDuration = calculatePauseDuration(sequence);
                Thread.sleep(pauseDuration);
            }
        }
    }

    private long calculatePauseDuration(String sequence) {
        long basePause = 1000;
        long extraPause = 700;
        // On testing, spaceCount gives always 0, but it looks nice
        int spaceCount = sequence.split("\\|")[0].length() - sequence.replaceAll(" ", "").split("\\|")[0].length();
        return basePause + (extraPause * spaceCount);
    }

}
