package main;
import main.sound.BellNote;
import main.sound.Note;
import main.sound.NoteLength;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SongReader {
    public final static String FILE_DIRECTORY = "data/";

    public List<BellNote> readFile(String fileName) {
        final List<BellNote> bellNotes = new ArrayList<>();

        if (fileName == null || fileName.isBlank()) {
            System.err.println("File name is null or empty");
            return bellNotes;
        }

        int lineCounter = 0;
        final String filePath = FILE_DIRECTORY + fileName;

        try (final BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();

            // Read each line in file
            while (line != null) {
                final String[] tokens = line.split(" ");

                boolean valid = true;
                BellNote bellNote = null;

                // Need 2 tokens for note, a note and the length of it
                if (tokens.length == 2) {
                    final Note note = parseNote(tokens[0]);

                    final NoteLength noteLength = parseNoteLength(tokens[1]);

                    // initialize note if valid note and note length
                    if (note != Note.INVALID && noteLength != NoteLength.INVALID) {
                        bellNote = new BellNote(note, noteLength);
                    }else{
                        valid = false;
                    }
                }else{
                    valid = false;
                }

                // if valid bell note and data, add it to list
                if (valid) {
                    bellNotes.add(bellNote);
                }else{
                    System.err.println("Invalid line: '" + line + "' in file: " + fileName);
                }

                // Keep track of how many lines have been read, used for validation later
                lineCounter++;
                line = br.readLine();
            }

        } catch (IOException e) {
            System.err.println("File not found at " + filePath);
        }

        // Ensure all lines contained valid notes, if not, return empty list
        if (lineCounter != -1 && lineCounter != bellNotes.size()) {
            System.err.println("Error: Number of valid notes ("+bellNotes.size()+") given doesn't match number of lines "+"("+lineCounter+") in file "+fileName);
//            return new ArrayList<>();
        }

        return bellNotes;
    }

    public boolean validateNotes(List<BellNote> notes) {
        boolean success = true;

        // Make sure there is at least one note
        if (notes == null || notes.isEmpty()) {
            System.err.println("No valid notes given");
            success = false;
            return success;
        }

        //Check each bell note to ensure note and length is valid
        for (BellNote note : notes) {
            // Check note
            if (note.getNote() == Note.INVALID) {
                System.err.println("At least one BellNote has an invalid Note!");
                success = false;
            }

            // Check note length
            if (note.getLength() == NoteLength.INVALID) {
                System.err.println("At least one BellNote has an invalid NoteLength!");
                success = false;
            }
        }

        return success;
    }

    public Note parseNote(String note) {
        if (note == null){
            return Note.INVALID;
        }
        try {
            //Returns the enum constant of the note with the specified name.
            return Note.valueOf(note);
        } catch (IllegalArgumentException ignored) {
        }

        return Note.INVALID;
    }

    public NoteLength parseNoteLength(String noteLength) {
        if (noteLength == null){
            return NoteLength.INVALID;
        }
        try {
            final int noteInt = Integer.parseInt(noteLength);
            //Convert to float
            final float noteLen = (float) 1 / noteInt;

            //Make sure given length is valid
            for (NoteLength note : NoteLength.values()) {
                if (note.getLength() == noteLen) {
                    return note;
                }
            }

            return NoteLength.INVALID;
        } catch (NumberFormatException ignored) {
        }

        return NoteLength.INVALID;
    }

    public static void main(String[] args) {
        final String file = "InvalidMusic.txt";
//        final String file = "MaryLamb.txt";
        final SongReader songReader = new SongReader();
        final List<BellNote> notes = songReader.readFile(file);
        final boolean validData = songReader.validateNotes(notes);

        if (!validData)
            System.out.println("Data not valid!");
    }
}