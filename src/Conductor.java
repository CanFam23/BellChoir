import sound.BellNote;
import sound.Note;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
TODO:
 - Conductor controls tempo of program
 - Have reentrant lock, make members wait with (Lock) until its their time to play
    - Fairness lock so members are granted lock in order them came
 - Member will play when they have the lock, and then unlock the lock once they are done
 - Keep track of what note we are on, give lock to member that plays note
 - Conductor can check if Memeber is currently playing. If so, it waits to reassign it
 - Could have a blockingQueue, pass to each member
 - member will peak into queue, if note is theirs, take it and play it
 */

public class Conductor implements Runnable{
    private final Map<BellNote, Member> members = new HashMap<>();

    private final BlockingQueue<BellNote> songOrder = new LinkedBlockingQueue<>();

    private int numMembers = 0;

    private final ReentrantLock lock = new ReentrantLock(true);

    private final CountDownLatch latch = new CountDownLatch(1);

    private final SourceDataLine line;

    private final Thread thread;

    public Conductor(AudioFormat af) {
        try {
            this.line = AudioSystem.getSourceDataLine(af);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        this.thread = new Thread(this, "Conductor");
    }

    public static void main(String[] args) {
        // Validate at least one argument was passed and the first one is not empty/null
        if (args.length == 0 || Objects.equals(args[0], "")) {
            System.err.println("Error: No file provided to read song from.");
            System.exit(1);
        }

        // Validate only one argument was passed
        if (args.length > 1) {
            System.err.println("Error: More than one argument provided to program, only 1 argument is accepted (The name of the file to read from).");
            System.exit(1);
        }

        final SongReader sr = new SongReader();

        final List<BellNote> song = sr.readFile(args[0]);

        // Validate song data
        if (!sr.validateData(song,args[0])) {
            System.err.println("Error: No valid notes found in file: " + args[0]);
            System.exit(1);
        }

        final AudioFormat af =
                new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, false);
        Conductor conductor = new Conductor(af);

        conductor.createChoir(song);

        conductor.start();

        conductor.stop();
    }

    public void start(){
        try {
            line.open();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        line.start();
        thread.start();

        for (Member member : members.values()) {
            member.start();
        }
        latch.countDown();
    }

    /**
     * Runs this operation.
     */
    @Override
    public void run() {
        while(!songOrder.isEmpty()) {
//            for (Member member : members.values()) {
//                System.out.println(member.getState());
//            }
//            System.out.println();
        }
    }

    public void stop(){
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        stopMembers();
        line.drain();
        line.close();
    }

    public void createChoir(List<BellNote> song) {
        songOrder.addAll(song);

        for (BellNote b : song) {
            addMember(b);
        }
    }

    public void addMember(BellNote b) {
        if (!members.containsKey(b)) {
            members.put(b,new Member(b,numMembers++,lock,songOrder,line,latch));
        }
    }

    public void stopMembers() {
        for (Member member : members.values()) {
            member.stop();
        }
    }
}
