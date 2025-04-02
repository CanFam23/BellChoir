package main.sound;

/**
 * In music, common lengths are whole, half, quarter, and eighth notes.
 * These are represented by the numbers 1, 2, 4, and 8 respectively in the song file.
 * The program assumes each song is written in 4/4 time, and a whole note takes up an entire measure.
 *
 * <p>This enum represents different note durations and provides their corresponding time values in milliseconds.
 * The time is calculated based on {@link Note#MEASURE_LENGTH_SEC}, assuming a fixed measure length.</p>
 */
public enum NoteLength {
    /** A whole note. */
    WHOLE(1.0f),
    /** A half note. */
    HALF(0.5f),
    /** A quarter note. */
    QUARTER(0.25f),
    /** A eighth note. */
    EIGHTH(0.125f),
    /** A invalid note. */
    INVALID(0.0f);

    /** The duration of the note in milliseconds, computed based on the measure length. */
    private final int timeMs;

    /** The fraction of a full measure that this note occupies. */
    private final float length;

    /**
     * Constructs a {@code NoteLength} with the specified fraction of a measure.
     *
     * @param length The fraction of a full measure that this note represents.
     */
    NoteLength(float length) {
        this.length = length;
        timeMs = (int) (length * Note.MEASURE_LENGTH_SEC * 1000);
    }

    /**
     * Returns the duration of the note in milliseconds.
     *
     * @return The note duration in milliseconds.
     */
    public int getTimeMs() {
        return timeMs;
    }

    /**
     * Returns the length of the note as a fraction of a measure.
     *
     * @return The fractional length of the note.
     */
    public float getLength() {
        return length;
    }
}

