package main;

import main.sound.Note;
import main.sound.NoteLength;

import javax.sound.sampled.SourceDataLine;
import java.util.Queue;

/**
 * The {@code Member} class represents a musical member that plays a {@link main.sound.BellNote}
 * in a synchronized multithreaded environment. Each member runs on its own thread and waits for its turn to play.
 * The class ensures synchronization between multiple members using the {@link #giveTurn()} method.
 */
public class Member implements Runnable {
    /** The {@link main.sound.BellNote} that this member will play. */
    private final Note note;

    /** The thread associated with this member, which executes the {@link #run()} method. */
    private final Thread t;

    /** A flag indicating whether the thread should keep running. It is volatile to ensure visibility across threads. */
    private volatile boolean running;

    /** The {@link SourceDataLine} used to output audio for this member's note. */
    private final SourceDataLine line;

    /** A flag indicating whether it is this member's turn to play. */
    private boolean myTurn = false;

    /** Keeps track of what {@link main.sound.NoteLength} to play. Will play them in the order of the queue. */
    private final Queue<NoteLength> noteLengths;

    /**
     * Constructs a new {@code Member} object.
     *
     * @param threadNum      Number of member.
     * @param note           {@link main.sound.Note} the member will play.
     * @param sourceDataLine The {@link SourceDataLine} to write the audio bytes to.
     * @param noteLengths    The {@code Queue} of {@link main.sound.NoteLength NoteLengths}, helps to keep track of
     *                       how long to play the note each time.
     */
    public Member(int threadNum, Note note, SourceDataLine sourceDataLine, Queue<NoteLength> noteLengths) {
        this.note = note;

        this.t = new Thread(this, "Member " + threadNum + " plays: " + note.toString());

        this.line = sourceDataLine;

        this.noteLengths = noteLengths;
    }

    /**
     * Sets {@link #running} to {@code true} and starts the {@link #t thread}.
     */
    public void start() {
        t.start();
        running = true;
    }

    /**
     * Plays the members {@link main.sound.BellNote} using the given {@link SourceDataLine}. The method
     * takes the head of the {@link #noteLengths} queue and plays the {@code Members} note for that amount of time.
     */
    public void playNote() {
        if (noteLengths.isEmpty()) {
            System.out.println(t.getName() + " No more notes left for me to play.");
            return;
        }

        final NoteLength noteLength = noteLengths.poll();
        // Uncomment this line to see what threads play what note and when
//        System.out.println(t.getName() + " Length: " + noteLength);
        final int ms = Math.min(noteLength.getTimeMs(), Note.MEASURE_LENGTH_SEC * 1000);
        final int length = Note.SAMPLE_RATE * ms / 1000;
        line.write(note.sample(), 0, length);
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
                        if (!running) {
                            return;
                        }
                    }
                }
                playNote();
                myTurn = false;
                notify();
            }
        }
    }

    /**
     * Adds the given {@link main.sound.NoteLength} to the {@link #noteLengths queue} of {@code NoteLengths} to play.
     *
     * @param noteLength {@link main.sound.NoteLength} to add.
     */
    public void addLength(NoteLength noteLength) {
        noteLengths.add(noteLength);
    }

    /**
     * Sets {@link #running} to {@code false} and interrupts the {@link #t thread} in case it's waiting.
     */
    public void stop() {
        running = false; // Don't really need this line, but kept it for sanity
        t.interrupt(); // Causes run method to return, terminated the thread
    }
}
