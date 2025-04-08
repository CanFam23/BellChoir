package main;

import main.sound.BellNote;
import main.sound.Note;
import main.sound.NoteLength;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.*;

/**
 * The {@code Conductor} class is responsible for coordinating the playback of a song
 * by managing multiple {@link main.Member} threads. It assigns each {@link main.sound.BellNote}
 * to a corresponding {@link main.Member} and ensures proper synchronization to maintain
 * the song's timing.
 *
 * <p>The conductor uses a {@link SourceDataLine} for audio output and uses a dedicated
 * thread to control the playback sequence. It also handles the initialization of members,
 * playback of notes, and cleanup of resources once the song is complete.</p>
 */
public class Conductor implements Runnable {
    /**
     * Buffer used when checking if a song is taking too long. A song
     * is allotted its sum of note length times this multiplier.
     */
    private final int SONG_ALLOTTED_TIME_BUFFER_MULTIPLIER = 2;

    /** Map that keeps track of what {@link main.Member} plays what {@link main.sound.BellNote}. */
    private final Map<Note, Member> members = new HashMap<>();

    /** List of {@link main.sound.Note notes} in the song that should be played. */
    private final List<BellNote> song;

    /** The {@code SourceDataLine} to write audio bytes to. */
    private SourceDataLine line;

    /** The thread that will control the tempo of the song. */
    private final Thread thread;

    /** Keep track of how many members are in the choir. */
    private int numMembers = 0;

    /**
     * Constructs a new Conductor object.
     *
     * @param af   The {@link AudioFormat} to use.
     * @param song The list of {@link main.sound.BellNote BellNotes} to play.
     */
    public Conductor(AudioFormat af, List<BellNote> song) {
        this.thread = new Thread(this, "Conductor");
        this.song = song;

        setLine(af);

        for (BellNote b : this.song) {
            addMember(b);
        }
    }

    /**
     * The main method reads the given command line argument (If there is one), and attempts to read
     * the file with the name given and convert it into a list of {@link main.sound.BellNote BellNots}
     * to play. If successful, it will create a new {@link Conductor} object and use it to play the song.
     *
     * @param args Arguments passed, should only be one, the name of the file to read.
     */
    public static void main(String[] args) {
        // Validate at least one argument was passed and the first one is not empty/null
        if (args.length == 0 || Objects.equals(args[0], "")) {
            System.err.println("Conductor.main Error: No file provided to read song from.");
            System.exit(1);
        }

        // Validate only one argument was passed
        if (args.length > 1) {
            System.err.println("Conductor.main Error: More than one argument provided to program, only 1 argument is accepted (The name of the file to read from).");
            System.exit(1);
        }

        final SongReader sr = new SongReader();

        final List<BellNote> song = sr.readFile(args[0]);

        // Validate song data
        if (song.isEmpty() || !sr.validateNotes(song)) {
            System.err.println("Conductor.main Error: No notes or at least one invalid note found in file: " + args[0]);
            System.exit(1);
        }

        System.out.println("Successfully loaded " + args[0]);

        // Create the audio format
        final AudioFormat af =
                new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, false);

        final Conductor conductor = new Conductor(af, song);

        // Play the song
        conductor.playSong();

        // Stop the conductor thread (will stop all members as well once song is over)
        conductor.stop();
    }

    /**
     * Opens and starts the {@code SourceDataLine}, if it's unavailable, it waits three seconds and tries again. If
     * it's still unavailable, the programs is aborted with status of 1. If it's available, the conductor thread is started
     * and the song is played.
     */
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
            } catch (InterruptedException ignored) {
            }

            // If line is still unavailable after 3 seconds, terminate program
            try {
                line.open();
                line.start();
            } catch (LineUnavailableException lue) {
                System.err.println("playSong: Line still unavailable when trying to open/start it after waiting 3 seconds, aborting");
                System.exit(1);
            }

        }

        thread.start();
    }

    /**
     * Starts the threads of all {@link main.Member Members} in the {@link #members} hashmap.
     */
    private void startMembers() {
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

        /*
         * Get the amount of time I think the song is expected to take to play, which
         * is the sum of all the note lengths time in milliseconds
         */
        int songTime = 0;
        for (BellNote b : song) {
            songTime += b.getLength().getTimeMs();
        }
        // Start time is when the song starts playing (about)
        final long startTime = System.currentTimeMillis();

        // Total time I'm giving the program to play the song
        final int allottedTime = songTime * SONG_ALLOTTED_TIME_BUFFER_MULTIPLIER;

        System.out.println("Playing song...");

        // Loop through all the notes in the song and have the member that plays the given note play it.
        for (BellNote b : song) {
            final Note noteToPlay = b.getNote();
            Member member = members.get(noteToPlay);

            // If no member exists in the hashMap, add it then assign it to member so it plays the note
            if (member == null) {
                addMember(b);
                member = members.get(noteToPlay);
            }

            // If the elapsed time is greater than the allotted time, something could have gone wrong, so the program
            // automatically ends
            float elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime > allottedTime) {
                System.err.println("Conductor.run(): SONG EXCEEDED ALLOTTED TIME, ending program.");
                System.exit(1);
            }

            member.giveTurn();
        }

        System.out.println("Song over");
    }

    /**
     * Waits for the {@link #thread} to finish its task (Playing the song) before stopping all
     * {@link main.Member Members} in the {@link #members} hashmap and draining/closing the {@code SourceDataLine}.
     */
    public void stop() {
        try {
            thread.join();
        } catch (InterruptedException e) {
            System.err.println("Thread was interrupted while waiting for conductor thread to finish.");
        }
        stopMembers();
        line.drain();
        line.close();
    }

    /**
     * Adds a {@link main.Member} to the {@link #members} hashmap.
     *
     * @param b {@link main.sound.BellNote} the member will play.
     */
    private void addMember(BellNote b) {
        // If the note is invalid, terminate the program
        if (b.getNote() == Note.INVALID || b.getLength() == NoteLength.INVALID) {
            System.err.println("addMember: Invalid note: " + b.getNote() + " found in song, terminating program.");
            System.exit(1);
        }

        // If the note is not in the members map, add it
        if (!members.containsKey(b.getNote())) {
            final Queue<NoteLength> noteLengthQueue = new ArrayDeque<>();
            noteLengthQueue.add(b.getLength());
            members.put(b.getNote(), new Member(1 + numMembers++, b.getNote(), line, noteLengthQueue));
        } else {
            // If the note is already in the members map, add the length to the member's queue
            members.get(b.getNote()).addLength(b.getLength());
        }
    }

    /**
     * Calls {@link Member#stop()} on all {@link main.Member Members} in the {@link #members} hashmap.
     */
    private void stopMembers() {
        for (Member member : members.values()) {
            member.stop();
        }
    }

    /**
     * Sets the {@link SourceDataLine}, using the provided {@link AudioFormat}.
     * IF the line is unavailable, it waits three seconds and tries again. If
     * it's still unavailable, the programs is aborted with status of 1.
     *
     * @param af The {@link AudioFormat} to use.
     */
    private void setLine(AudioFormat af) {
        try {
            this.line = AudioSystem.getSourceDataLine(af);
            // If line is not available, wait 3 seconds and try again
        } catch (LineUnavailableException e) {
            System.err.println("Line was unavailable, waiting 3 seconds and trying again...");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignored) {
            }

            // If line is still unavailable after 3 seconds, terminate program
            try {
                this.line = AudioSystem.getSourceDataLine(af);
            } catch (LineUnavailableException lue) {
                System.err.println("setLine: Line still unavailable after waiting 3 seconds, aborting");
                System.exit(1);
            }
        }
    }
}
