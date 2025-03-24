package sound;

import java.util.Objects;

public class BellNote {
    private final Note note;
    private final NoteLength length;

    public BellNote(Note note, NoteLength length) {
        this.note = note;
        this.length = length;
    }

    public Note getNote() {
        return note;
    }

    public NoteLength getLength() {
        return length;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BellNote{");
        sb.append("note=").append(note);
        sb.append(", length=").append(length);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BellNote bellNote = (BellNote) o;
        return note == bellNote.note && length == bellNote.length;
    }

    @Override
    public int hashCode() {
        return Objects.hash(note, length);
    }
}
