package main;

import main.sound.BellNote;
import main.sound.Note;
import main.sound.NoteLength;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code SongReader} class handles reading and parsing text files into {@link main.sound.BellNote} objects. The class
 * consists of the following methods:
 * <ul>
 *     <li>{@link #readFile(String)}</li>
 *     <li>{@link #parseNoteLength(String)}</li>
 *     <li>{@link #parseNote(String)}</li>
 *     <li>{@link #validateNotes(List)}</li>
 * </ul>
 */
public class SongReader {
    /** Directory all text files are stored in. */
    public final static String FILE_DIRECTORY = "data/";

    /**
     * Constructs a new SongReader object. Currently empty constructor
     */
    public SongReader() {
        //empty
    }

    /**
     * Reads the given file and converts each line into a {@link main.sound.BellNote} object,
     * and returns a list of all valid BellNotes found in the file.
     *
     * @param fileName The file to read.
     * @return A {@code List} of {@link main.sound.BellNote} objects, or an empty {@code List} if no valid
     * notes are found.
     * @see #parseNoteLength(String)
     * @see #parseNote(String)
     */
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
                line = line.strip();
                final String[] tokens = line.split(" ");

                boolean valid = true;
                BellNote bellNote = null;

                // Need 2 tokens for note, a note and the length of it
                if (tokens.length == 2) {
                    // Strip both strings to ensure no whitespace is in them
                    final Note note = parseNote(tokens[0].strip());

                    final NoteLength noteLength = parseNoteLength(tokens[1].strip());

                    // initialize note if valid note and note length
                    if (note != Note.INVALID && noteLength != NoteLength.INVALID) {
                        bellNote = new BellNote(note, noteLength);
                    } else {
                        valid = false;
                    }
                } else {
                    valid = false;
                }

                // if valid bell note and data, add it to list
                if (valid) {
                    bellNotes.add(bellNote);
                } else {
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
            System.err.println("Warning: Number of valid notes (" + bellNotes.size() + ") given doesn't match number of lines " + "(" + lineCounter + ") in file " + fileName);
            return new ArrayList<>();
        }

        return bellNotes;
    }

    /**
     * Checks if each note in the given {@code List} of {@link main.sound.BellNote Bellnotes} is valid. <br>
     * A BellNote is <b>valid</b> if:
     * <ul>
     *     <li>It's {@link main.sound.Note} is not {@link main.sound.Note#INVALID INVALID}</li>
     *     <li>It's {@link main.sound.NoteLength} is not {@link main.sound.NoteLength#INVALID INVALID}</li>
     * </ul>
     *
     * @param notes {@code List} of {@link main.sound.BellNote} objects to check.
     * @return {@code true} if all the notes in the list are valid and there is at least one note, {@code false} otherwise.
     */
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

    /**
     * Parses the given string into a Note.
     *
     * @param note String to parse.
     * @return {@link main.sound.Note} enum which represents the Note,
     * {@link main.sound.Note#INVALID} if the given string is not a valid Note.
     */
    public Note parseNote(String note) {
        if (note == null) {
            return Note.INVALID;
        }
        try {
            note = note.strip();
            //Returns the enum constant of the note with the specified name.
            return Note.valueOf(note);
        } catch (IllegalArgumentException ignored) {
        }

        return Note.INVALID;
    }

    /**
     * Parses the given string into a NoteLength.
     *
     * @param noteLength String to parse.
     * @return {@link main.sound.NoteLength} enum which represents the Note Length,
     * {@link main.sound.NoteLength#INVALID} if the given string is not a valid NoteLength.
     */
    public NoteLength parseNoteLength(String noteLength) {
        if (noteLength == null) {
            return NoteLength.INVALID;
        }
        try {
            noteLength = noteLength.strip();
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
}