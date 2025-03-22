import sound.BellNote;
import sound.Note;

import javax.sound.sampled.SourceDataLine;

public class Member implements Runnable{
    private final BellNote bn;
    private final Thread t;

    public Member(BellNote bn, int threadNum) {
        this.bn = bn;

        this.t = new Thread(this, "Member " + threadNum + " BellNote: " + bn.toString());
        t.start();
    }

    public void playNote(SourceDataLine line) {
        System.out.println(t.getName());
        final int ms = Math.min(bn.getLength().getTimeMs(), Note.MEASURE_LENGTH_SEC * 1000);
        final int length = Note.SAMPLE_RATE * ms / 1000;
        line.write(bn.getNote().sample(), 0, length);
        line.write(Note.REST.sample(), 0, 50);
    }

    @Override
    public void run() {

    }

    public void stop(){
        waitToStop();
    }

    public void waitToStop(){
        try {
            t.join();
        } catch (InterruptedException e) {
            System.err.println(t.getName() + " stop malfunction");
        }
    }
}
