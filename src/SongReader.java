import sound.BellNote;
import sound.Note;
import sound.NoteLength;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SongReader {
    final static String FILE_DIRECTORY = "data/";

    public SongReader() {
    }

    public List<BellNote> readFile(String fileName) {
        final String filePath = FILE_DIRECTORY + fileName;
        final List<BellNote> bellNotes = new ArrayList<>();

        try (final BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();

            while (line != null) {
                String[] tokens = line.split(" ");

                final Note note = parseNote(tokens[0]);
//                System.out.println(note);

                final NoteLength noteLength = parseNoteLength(tokens[1]);
//                System.out.println(noteLength);

                if (note != Note.INVALID && noteLength != NoteLength.INVALID) {
                    final BellNote bellNote = new BellNote(note, noteLength);
                    bellNotes.add(bellNote);
                }

                line = br.readLine();
            }

        } catch (IOException e) {
            System.err.println("File not found at " + filePath);
        }

        return bellNotes;
    }

    public boolean validateData(List<BellNote> notes, String fileName) {
        boolean success = true;

        if (notes.isEmpty()) {
            System.err.println("No valid notes found");
            success = false;
        }

        //Check each bell note to ensure note and length is valid
        for (BellNote note : notes) {
            if (note.getNote() == Note.INVALID) {
                System.err.println("At least one BellNote has an invalid Note!");
                success = false;
            }

            if (note.getLength() == NoteLength.INVALID) {
                System.err.println("At least one BellNote has an invalid NoteLength!");
                success = false;
            }
        }

        return success;
    }

    public Note parseNote(String note) {
        try {
            //Returns the enum constant of the note with the specified name.
            return Note.valueOf(note);
        } catch (IllegalArgumentException ignored) {
        }

        return Note.INVALID;
    }

    public NoteLength parseNoteLength(String noteLength) {
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
        final String file = "MaryLamb.txt";
        final SongReader songReader = new SongReader();
        final List<BellNote> notes = songReader.readFile(file);
        final boolean validData = songReader.validateData(notes, file);

        if (!validData)
            System.out.println("Data not valid!");
    }
}