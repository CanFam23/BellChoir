package main.sound;

public enum NoteLength {
    WHOLE(1.0f),
    HALF(0.5f),
    QUARTER(0.25f),
    EIGHTH(0.125f),
    INVALID(0.0f);

    private final int timeMs;
    private final float length;

    private NoteLength(float length) {
        this.length = length;
        timeMs = (int)(length * Note.MEASURE_LENGTH_SEC * 1000);
    }

    public int getTimeMs() {
        return timeMs;
    }

    public float getLength() {
        return length;
    }
}
