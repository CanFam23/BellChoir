package main;
import main.sound.BellNote;
import main.sound.Note;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.*;

/*
TODO:
 - Conductor controls tempo of program
TODO:
 - Song Reader Tests
 - More validation
 - Starvation checks, make sure no member is waiting too long
 - Dead lock checks?
 */

public class Conductor implements Runnable{
    private final Map<BellNote, Member> members = new HashMap<>();

    private final List<BellNote> song;

    private SourceDataLine line;

    private final Thread thread;

    private int numMembers = 0;

    public Conductor(AudioFormat af,List<BellNote> song) {
        this.thread = new Thread(this, "Conductor");
        this.song = song;

        setLine(af);

        for (BellNote b : this.song) {
            addMember(b);
        }
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
        if (!sr.validateNotes(song)) {
            System.err.println("Error: No valid notes found in file: " + args[0]);
            System.exit(1);
        }

        // Create the audio format
        final AudioFormat af =
                new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, false);

        final Conductor conductor = new Conductor(af,song);

        // Play the song
        conductor.playSong();

        // Stop the conductor thread (will stop all members as well once song is over)
        conductor.stop();
    }

    public void playSong() {
        // Open and start the line, if unavailable, wait 3 seconds then try again
        try {
            line.open();
            line.start();
            // If line is not available, wait 3 seconds and try again
        } catch (LineUnavailableException e) {
            System.err.println("Line was unavailable when trying to open/start it, waiting 3 seconds and trying again...");

            // Wait 3 seconds
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignored) {}

            // If line is still unavailable after 3 seconds, terminate program
            try {
                line.open();
                line.start();
            } catch (LineUnavailableException lue) {
                System.err.println("Line still unavailable when trying to open/start it after waiting 3 seconds, aborting");
                System.exit(1);
            }

        }

        thread.start();
    }

    public void startMembers(){
        for (Member member : members.values()) {
            member.start();
        }
    }

    /**
     * Opens and starts the line, starts the member threads, and plays the song.
     */
    @Override
    public void run() {
        startMembers();

        // Loop through all the notes in the song and have the member that plays the given note play it.
        for(BellNote b : song) {
            Member member = members.get(b);

            // If no member exists in the hashMap, add it then assign it to member so it plays the note
            if (member == null) {
                addMember(b);
                member = members.get(b);
            }

            member.giveTurn();
        }
    }

    public void stop(){
        try{
            thread.join();
        } catch (InterruptedException e) {
            System.err.println("Thread was interrupted while waiting for conductor thread to finish.");
        }
        stopMembers();
        line.drain();
        line.close();
    }

    public void addMember(BellNote b) {
        if (!members.containsKey(b)) {
            members.put(b,new Member(numMembers++,b,line));
        }
    }

    public void stopMembers() {
        for (Member member : members.values()) {
            member.stop();
        }
    }

    private void setLine(AudioFormat af) {
        try {
            this.line = AudioSystem.getSourceDataLine(af);
            // If line is not available, wait 3 seconds and try again
        } catch (LineUnavailableException e) {
            System.err.println("Line was unavailable, waiting 3 seconds and trying again...");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignored) {}

            // If line is still unavailable after 3 seconds, terminate program
            try {
                this.line = AudioSystem.getSourceDataLine(af);
            } catch (LineUnavailableException lue) {
                System.err.println("Line still unavailable after waiting 3 seconds, aborting");
                System.exit(1);
            }
        }
    }
}
