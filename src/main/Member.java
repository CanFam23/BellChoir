package main;

import main.sound.BellNote;
import main.sound.Note;

import javax.sound.sampled.SourceDataLine;

public class Member implements Runnable{
    private final BellNote bn;

    private final Thread t;

    private volatile boolean running;

    private final SourceDataLine line;

    private boolean myTurn = false;

    public Member(int threadNum, BellNote bn, SourceDataLine sourceDataLine) {
        this.bn = bn;

        this.t = new Thread(this, "Member " + threadNum + " plays: " + bn.toString());

        this.line = sourceDataLine;
    }

    public void start(){
        t.start();
        running = true;
    }

    public void playNote() {
        System.out.println(t.getName());
        final int ms = Math.min(bn.getLength().getTimeMs(), Note.MEASURE_LENGTH_SEC * 1000);
        final int length = Note.SAMPLE_RATE * ms / 1000;
        line.write(bn.getNote().sample(), 0, length);
        line.write(Note.REST.sample(), 0, 50);
    }

    public void giveTurn() {
        synchronized (this) {
            if (myTurn) {
                throw new IllegalStateException("Attempt to give a turn to a member who's hasn't completed the current turn");
            }
            myTurn = true;
            notify();
            while (myTurn) {
                try {
                    wait();
                } catch (InterruptedException ignored) {}
            }
        }
    }

    @Override
    public void run() {
        synchronized (this) {
            while(running){
                while(!myTurn){
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        t.interrupt(); // Set the interrupt flag back to true //TODO remove?
                        return;
                    }
                }
                playNote();
                myTurn = false;
                notify();
            }
        }
    }

    public void stop(){
        System.out.println("Stopping " + t.getName());
        running = false; // Don't really need this line, but kept it for sanity
        t.interrupt(); // Causes run method to return, terminated the thread
    }
}
