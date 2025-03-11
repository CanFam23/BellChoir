package sound;

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
}
