package main;

import main.sound.BellNote;
import main.sound.Note;

import javax.sound.sampled.SourceDataLine;

/**
 * The {@code Member} class represents a musical member that plays a {@link main.sound.BellNote}
 * in a synchronized multithreaded environment. Each member runs on its own thread and waits for its turn to play.
 * The class ensures synchronization between multiple members using the {@link #giveTurn()} method.
 */
public class Member implements Runnable {
    /** The {@link main.sound.BellNote} that this member will play. */
    private final BellNote bn;

    /** The thread associated with this member, which executes the {@link #run()} method. */
    private final Thread t;

    /** A flag indicating whether the thread should keep running. It is volatile to ensure visibility across threads. */
    private volatile boolean running;

    /** The {@link SourceDataLine} used to output audio for this member's note. */
    private final SourceDataLine line;

    /** A flag indicating whether it is this member's turn to play. */
    private boolean myTurn = false;

    /**
     * Constructs a new {@code Member} object.
     *
     * @param threadNum      Number of member.
     * @param bn             {@link main.sound.BellNote} the member will play.
     * @param sourceDataLine The {@link SourceDataLine} to write the audio bytes to.
     */
    public Member(int threadNum, BellNote bn, SourceDataLine sourceDataLine) {
        this.bn = bn;

        this.t = new Thread(this, "Member " + threadNum + " plays: " + bn.toString());

        this.line = sourceDataLine;
    }

    /**
     * Sets {@link #running} to {@code true} and starts the {@link #t thread}.
     */
    public void start() {
        t.start();
        running = true;
    }

    /**
     * Plays the members {@link main.sound.BellNote} using the given {@link SourceDataLine}.
     */
    public void playNote() {
        System.out.println(t.getName());
        final int ms = Math.min(bn.getLength().getTimeMs(), Note.MEASURE_LENGTH_SEC * 1000);
        final int length = Note.SAMPLE_RATE * ms / 1000;
        line.write(bn.getNote().sample(), 0, length);
        line.write(Note.REST.sample(), 0, 50);
    }


    /**
     * Gives the turn to the current member by setting {@link #myTurn} to {@code true}
     * and notifying a waiting thread. If the current member has not completed
     * their previous turn, an {@link IllegalStateException} is thrown.
     * <p>The method uses synchronization to ensure proper coordination between threads.
     * It waits until the turn is completed before returning.</p>
     *
     * @throws IllegalStateException if an attempt is made to give a turn to a member
     *                               who hasn't completed their current turn.
     */
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
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    /**
     * While {@link #running}, the thread will wait until it is its turn to play its note.
     * Once the thread has played its note, it will notify another thread waiting on the thread.
     *
     * <p>The method uses synchronization to ensure proper coordination between threads.</p>
     */
    @Override
    public void run() {
        synchronized (this) {
            while (running) {
                while (!myTurn) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        // Set the interrupt flag back to true (Catching the error sets it to false)
                        t.interrupt(); // Not really needed but good practice (I think)
                        return;
                    }
                }
                playNote();
                myTurn = false;
                notify();
            }
        }
    }

    /**
     * Sets {@link #running} to {@code false} and interrupts the {@link #t thread} in case it's waiting.
     */
    public void stop() {
        running = false; // Don't really need this line, but kept it for sanity
        t.interrupt(); // Causes run method to return, terminated the thread
    }
}
