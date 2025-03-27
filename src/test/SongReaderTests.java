package test;

import main.SongReader;
import main.sound.BellNote;
import main.sound.Note;
import main.sound.NoteLength;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
TODO:
 - Add/Remove display names to/from all
 - readFile
    - curr have 8 tests
 - validateNotes
    - curr have 5 tests
 - parseNote
    - curr have 6 tests
 - parseNoteLength
    - curr have 8 tests
 */

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SongReaderTests {

    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;

    private SongReader songReader;

    @BeforeAll
    public void setup(){
        songReader = new SongReader();

        System.setErr(new PrintStream(errContent));
    }

    @AfterAll
    public void tearDown(){
        System.setErr(originalErr);
        errContent.reset();
    }

    @BeforeEach
    void beforeEach(TestInfo testInfo) {
        System.out.println("Testing: " + testInfo.getDisplayName());
    }

    // readFile tests

    @Test
    @DisplayName("readFile with null file")
    public void testReadFileNullFile(){
        assertNotNull(songReader);
        final List<BellNote> notes = songReader.readFile(null);
        assertTrue(notes.isEmpty(), "readFile function should return empty list when null file name is passed!");
    }

    @Test
    @DisplayName("readFile with empty file")
    public void testReadFileEmptyFileName(){
        assertNotNull(songReader);
        final List<BellNote> notes = songReader.readFile(" ");
        assertTrue(notes.isEmpty(), "readFile function should return empty list when empty file name is passed!");
    }

    @Test
    @DisplayName("readFile with a file that doesn't exist")
    public void testReadFileFileDoesntExist(){
        assertNotNull(songReader);
        final List<BellNote> notes = songReader.readFile("NeverGonnaGiveYouUp.txt");
        assertTrue(notes.isEmpty(), "readFile function should return empty list when unknown file name is passed!");
    }

    @Test
    @DisplayName("readFile with empty file")
    public void testReadFileEmptyFile(){
        assertNotNull(songReader);
        final List<BellNote> notes = songReader.readFile("EmptyFile.txt");
        assertTrue(notes.isEmpty(), "readFile function should return empty list when empty file is passed!");
    }

    @Test
    @DisplayName("readRile with a file that contains invalid notes")
    public void testReadFileFileWithInvalidNotes(){
        assertNotNull(songReader);
        final List<BellNote> notes = songReader.readFile("InvalidMusic.txt");
        // There is only one valid note in the given file
        assertEquals(1, notes.size(), "readFile function should not add invalid notes to list of notes to return!");
    }

    @Test
    @DisplayName("readRile with a file that contains no notes")
    public void testReadFileFileWithRandomTextFile(){
        assertNotNull(songReader);
        final List<BellNote> notes = songReader.readFile("Text.txt");
        assertTrue(notes.isEmpty(), "readFile function should not return any notes when no notes are in the given file!");
    }

    @Test
    @DisplayName("readRile with a file that has a incorrect file path")
    public void testReadFileFileWithIncorrectFilePath(){
        assertNotNull(songReader);
        final List<BellNote> notes = songReader.readFile("data/MaryLamb.txt");
        assertTrue(notes.isEmpty(), "readFile function should not return any notes when no file is found with the given path!");
    }

    @Test
    @DisplayName("readRile with a file that contains all valid notes")
    public void testReadFileFileWithValidFileWithValidNotes(){
        assertNotNull(songReader);
        final List<BellNote> notes = songReader.readFile("MaryLamb.txt");
        assertFalse(notes.isEmpty(), "readFile function should return all notes when all notes are valid in the given file!");

        final boolean validNotes = songReader.validateNotes(notes);
        assertTrue(validNotes, "readFile function should not return any invalid notes!");
    }

    // validateNote tests

    @Test
    public void testValidateNoteWithNullPassed(){
        assertNotNull(songReader);
        final boolean validNotes = songReader.validateNotes(null);
        assertFalse(validNotes, "validateNotes function should return false when null is passed!");
    }

    @Test
    public void testValidateNoteWithEmptyListPassed(){
        assertNotNull(songReader);
        final boolean validNotes = songReader.validateNotes(new ArrayList<>());
        assertFalse(validNotes, "validateNotes function should return false when an empty list is passed!");
    }

    @Test
    public void testValidateNoteWithListOfInvalidNotesPassed(){
        assertNotNull(songReader);
        final List<BellNote> notes = new ArrayList<>();
        notes.add(new BellNote(Note.B4, NoteLength.INVALID));
        notes.add(new BellNote(Note.INVALID, NoteLength.HALF));
        notes.add(new BellNote(Note.INVALID, NoteLength.INVALID));

        final boolean validNotes = songReader.validateNotes(notes);
        assertFalse(validNotes, "validateNotes function should return false if any invalid notes are passed!");
    }

    @Test
    public void testValidateNoteWithListOfInvalidAndValidNotesPassed(){
        assertNotNull(songReader);
        final List<BellNote> notes = new ArrayList<>();
        notes.add(new BellNote(Note.B4, NoteLength.INVALID));
        notes.add(new BellNote(Note.INVALID, NoteLength.HALF));
        notes.add(new BellNote(Note.INVALID, NoteLength.INVALID));
        notes.add(new BellNote(Note.B4, NoteLength.EIGHTH));
        notes.add(new BellNote(Note.A5, NoteLength.HALF));
        notes.add(new BellNote(Note.C4, NoteLength.QUARTER));

        final boolean validNotes = songReader.validateNotes(notes);
        assertFalse(validNotes, "validateNotes function should return false if any invalid notes are found in the given list!");
    }

    @Test
    public void testValidateNoteWithListOfValidNotesPassed(){
        assertNotNull(songReader);
        final List<BellNote> notes = new ArrayList<>();
        notes.add(new BellNote(Note.B4, NoteLength.EIGHTH));
        notes.add(new BellNote(Note.A5, NoteLength.HALF));
        notes.add(new BellNote(Note.C4, NoteLength.QUARTER));

        final boolean validNotes = songReader.validateNotes(notes);
        assertTrue(validNotes, "validateNotes function should return true if all notes in the given list are valid!");
    }

    // parseNote tests

    @Test
    public void testParseNoteWithNullPassed(){
        assertNotNull(songReader);
        final String note = null;

        final Note parsedNote = songReader.parseNote(note);
        assertEquals(parsedNote, Note.INVALID, "parseNote function should return invalid note when null is passed!");
    }

    @Test
    public void testParseNoteWithEmptyStringPassed(){
        assertNotNull(songReader);
        final String note = "";

        final Note parsedNote = songReader.parseNote(note);
        assertEquals(parsedNote, Note.INVALID, "parseNote function should return invalid note when empty string is passed!");
    }

    @Test
    public void testParseNoteWithBlankStringPassed(){
        assertNotNull(songReader);
        final String note = " ";

        final Note parsedNote = songReader.parseNote(note);
        assertEquals(parsedNote, Note.INVALID, "parseNote function should return invalid note when blank string is passed!");
    }

    @Test
    public void testParseNoteWithInvalidStringPassed(){
        assertNotNull(songReader);
        final String note = "note";

        final Note parsedNote = songReader.parseNote(note);
        assertEquals(parsedNote, Note.INVALID, "parseNote function should return invalid note when an invalid string is passed!");
    }

    @Test
    public void testParseNoteWithNumberStringPassed(){
        assertNotNull(songReader);
        final String note = "12";

        final Note parsedNote = songReader.parseNote(note);
        assertEquals(parsedNote, Note.INVALID, "parseNote function should return invalid note when string of numbers is passed!");
    }

    @Test
    public void testParseNoteWithValidStringPassed(){
        assertNotNull(songReader);
        final Note actualNote = Note.REST;
        final String note = String.valueOf(actualNote);

        final Note parsedNote = songReader.parseNote(note);
        assertEquals(parsedNote, actualNote, "parseNote function should return valid note when valid string is passed!");
    }

    // parseLength tests

    @Test
    public void testParseLengthWithNullPassed(){
        assertNotNull(songReader);

        final NoteLength noteLen = songReader.parseNoteLength(null);
        assertEquals(noteLen,NoteLength.INVALID, "parseNoteLength function should return invalid noteLength when null is passed!");
    }

    @Test
    public void testParseLengthWithEmptyStringPassed(){
        assertNotNull(songReader);

        final String noteLength = "";

        final NoteLength noteLen = songReader.parseNoteLength(noteLength);
        assertEquals(noteLen,NoteLength.INVALID, "parseNoteLength function should return invalid noteLength when an empty string is passed!");
    }

    @Test
    public void testParseLengthWithBlankStringPassed(){
        assertNotNull(songReader);

        final String noteLength = " ";

        final NoteLength noteLen = songReader.parseNoteLength(noteLength);
        assertEquals(noteLen,NoteLength.INVALID, "parseNoteLength function should return invalid noteLength when an blank string is passed!");
    }

    @Test
    public void testParseLengthWithInvalidStringPassed(){
        assertNotNull(songReader);

        final String noteLength = "len";

        final NoteLength noteLen = songReader.parseNoteLength(noteLength);
        assertEquals(noteLen,NoteLength.INVALID, "parseNoteLength function should return invalid noteLength when an invalid string is passed!");
    }

    @Test
    public void testParseLengthWithStringNumberZeroPassed(){
        assertNotNull(songReader);

        final String noteLength = "0";

        final NoteLength noteLen = songReader.parseNoteLength(noteLength);
        assertEquals(noteLen,NoteLength.INVALID, "parseNoteLength function should return invalid noteLength when an invalid string is passed!");
    }

    @Test
    public void testParseLengthWithStringInvalidNumberPassed(){
        assertNotNull(songReader);

        final String noteLength = "12";

        final NoteLength noteLen = songReader.parseNoteLength(noteLength);
        assertEquals(noteLen,NoteLength.INVALID, "parseNoteLength function should return invalid noteLength when an invalid string is passed!");
    }

    @Test
    public void testParseLengthWithStringNegativeNumberPassed(){
        assertNotNull(songReader);

        final String noteLength = "-1";

        final NoteLength noteLen = songReader.parseNoteLength(noteLength);
        assertEquals(noteLen,NoteLength.INVALID, "parseNoteLength function should return invalid noteLength when an invalid string is passed!");
    }

    @Test
    public void testParseLengthWithStringValidNumberPassed(){
        assertNotNull(songReader);

        final NoteLength actualNoteLength = NoteLength.HALF;
        final float actualNoteLengthVal = actualNoteLength.getLength();

        final String noteLength = String.valueOf((int) (actualNoteLengthVal * 4));

        final NoteLength noteLen = songReader.parseNoteLength(noteLength);
        assertEquals(noteLen,actualNoteLength, "parseNoteLength function should return valid noteLength when an valid string is passed!");
    }
}
