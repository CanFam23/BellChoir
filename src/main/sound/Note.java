package main.sound;

/**
 * A note is part of a standard 13-note scale, where the note
 * <ul>
 *     <li>'middle A' (440Hz) is identified by "A4", and an A an octave higher would be identified as "A5".</li>
 *     <li>Sharp notes are indicated by appending S to the standard note, so middle A # would be indicated by 'A4S'.</li>
 *     <li>Flat notes are equivalent to a sharp of the lower note, so to identify B flat, use 'A4F'.</li>
 *     <li>A REST note (nothing is played) is identified by 'REST'.</li>
 * </ul>
 */
public enum Note {
    /** Constant for Rest. */
    REST(0),
    /** Constant for A4. */
    A4(1),
    /** Constant for A4S. */
    A4S(2),
    /** Constant for B4F - equal to A4S in terms of sound frequency. */
    B4F(A4S),
    /** Constant for B4. */
    B4(3),
    /** Constant for C4. */
    C4(4),
    /** Constant for C4S. */
    C4S(5),
    /** Constant for D4F - equal to C4S in terms of sound frequency. */
    D4F(C4S),
    /** Constant for D4. */
    D4(6),
    /** Constant for D4S. */
    D4S(7),
    /** Constant for E4F - equal to D4S in terms of sound frequency. */
    E4F(D4S),
    /** Constant for E4. */
    E4(8),
    /** Constant for F4. */
    F4(9),
    /** Constant for F4S. */
    F4S(10),
    /** Constant for G4F - equal to F4S in terms of sound frequency. */
    G4F(F4S),
    /** Constant for G4. */
    G4(11),
    /** Constant for G4S. */
    G4S(12),
    /** Constant for A5F - equal to G4S in terms of sound frequency. */
    A5F(G4S),
    /** Constant for A5. */
    A5(13),
    /** Constant for any note not shown above (invalid). */
    INVALID(0);

    /** The number of audio samples taken per second during audio playback (KHz) */
    public static final int SAMPLE_RATE = 48 * 1024; // ~48KHz

    /** Fixed measurement length in seconds for notes */
    public static final int MEASURE_LENGTH_SEC = 1;

    // Circumference of a circle divided by # of samples
    /** The angular step size per sample, used for waveform generation. */
    private static final double step_alpha = (2.0d * Math.PI) / SAMPLE_RATE;

    /** The standard pitch frequency for musical note A4. */
    private final double FREQUENCY_A_HZ = 440.0d;

    /** Max volume a note can be. */
    private final double MAX_VOLUME = 127.0d;

    /** Reference to harmonic equal note */
    private final Note harmonicEqual;

    /** The semitone value relative to A4 (where A4=1) */
    private final int semitone;

    /** A byte array representing a single measure of a sine wave sample. */
    private final byte[] sinSample = new byte[MEASURE_LENGTH_SEC * SAMPLE_RATE];

    /**
     * Private constructor for the Note enum that creates the sine wave sample for each note.
     * This constructor calculates the frequency of the note relative to A4 (440 Hz) using the
     * twelve-tone equal temperament formula. It then generates a sin waveform for the
     * frequency and stores it in the {@code sinSample} array.
     *
     * @param semitone The semitone value relative to A4 (where A4=1)
     */
    Note(int semitone) {
        this.harmonicEqual = this;
        this.semitone = semitone;

        if (semitone > 0) {
            // Calculate the frequency!
            final double halfStepUpFromA = semitone - 1;
            final double exp = halfStepUpFromA / 12.0d;
            final double freq = FREQUENCY_A_HZ * Math.pow(2.0d, exp);

            // Create sinusoidal data sample for the desired frequency
            final double sinStep = freq * step_alpha;
            for (int i = 0; i < sinSample.length; i++) {
                sinSample[i] = (byte) (Math.sin(i * sinStep) * MAX_VOLUME);
            }
        }
    }

    /**
     * Constructor for flat notes that references their harmonic sharp equivalent
     *
     * @param harmonicEqual The notes harmonic sharp equivalent
     */
    Note(Note harmonicEqual) {
        this.harmonicEqual = harmonicEqual;
        this.semitone = harmonicEqual.semitone;
    }

    /**
     * Gets the {@link #sinSample} array of bytes, which represents
     * single measure of a sine wave sample.
     * <br>For flat notes, returns the sample of the harmonic equivalent.<br>
     *
     * @return An array of bytes representing the single measure.
     */
    public byte[] sample() {
        return harmonicEqual.sinSample;
    }
}
