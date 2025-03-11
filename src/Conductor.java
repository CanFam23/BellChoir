import sound.BellNote;
import sound.Note;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.List;

public class Conductor {
    private final AudioFormat af;

    public Conductor(AudioFormat af) {
        this.af = af;
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            System.out.println(args[0]);
        }
        final AudioFormat af =
                new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, false);
        Conductor t = new Conductor(af);
        SongReader sr = new SongReader();
        List<BellNote> song = sr.readFile(args[0]);

        t.playSong(song);
    }

    public void playSong(List<BellNote> song) throws LineUnavailableException {
        try (final SourceDataLine line = AudioSystem.getSourceDataLine(af)) {
            line.open();
            line.start();

            for (BellNote bn: song) {
                playNote(line, bn);
            }
            line.drain();
        }
    }

    private void playNote(SourceDataLine line, BellNote bn) {
        final int ms = Math.min(bn.getLength().getTimeMs(), Note.MEASURE_LENGTH_SEC * 1000);
        final int length = Note.SAMPLE_RATE * ms / 1000;
        line.write(bn.getNote().sample(), 0, length);
        line.write(Note.REST.sample(), 0, 50);
    }
}
