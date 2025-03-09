package sound;

public class BellNote {
    final Note note;
    final NoteLength length;

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
