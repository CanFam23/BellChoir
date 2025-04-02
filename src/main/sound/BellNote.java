package main.sound;

import java.util.Objects;

/**
 * Represents a musical note to be played by a bell, consisting of a pitch ({@link Note})
 * and a duration ({@link NoteLength}).
 */
public class BellNote {
    /** The musical note to be played. */
    private final Note note;

    /** The duration of the note. */
    private final NoteLength length;

    /**
     * Constructs a {@code BellNote} with the specified pitch and duration.
     *
     * @param note   The musical note to be played.
     * @param length The duration of the note.
     */
    public BellNote(Note note, NoteLength length) {
        this.note = note;
        this.length = length;
    }

    /**
     * Returns the musical note of this {@code BellNote}.
     *
     * @return The {@link Note} associated with this bell note.
     */
    public Note getNote() {
        return note;
    }

    /**
     * Returns the duration of this {@code BellNote}.
     *
     * @return The {@link NoteLength} of this bell note.
     */
    public NoteLength getLength() {
        return length;
    }

    /**
     * Returns a string representation of this {@code BellNote}.
     *
     * @return A string containing the note and its length.
     */
    @Override
    public String toString() {
        String sb = "BellNote{" + "note=" + note +
                ", length=" + length +
                '}';
        return sb;
    }

    /**
     * Compares this {@code BellNote} to another object.
     * Two {@code BellNote} objects are equal if they have the same note and length.
     *
     * @param o The object to compare with this {@code BellNote}.
     * @return {@code true} if the objects are equal, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BellNote bellNote = (BellNote) o;
        return note == bellNote.note && length == bellNote.length;
    }

    /**
     * Computes the hash code for this {@code BellNote}.
     *
     * @return A hash code based on the note and length.
     */
    @Override
    public int hashCode() {
        return Objects.hash(note, length);
    }
}

