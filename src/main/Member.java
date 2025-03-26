package main;

import main.sound.BellNote;
import main.sound.Note;

import javax.sound.sampled.SourceDataLine;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;

public class Member implements Runnable{
    private final BellNote bn;

    private final Thread t;

    private boolean running;

    private final Lock lock;

    private final BlockingQueue<BellNote> songOrder;

    private final SourceDataLine line;

    private final CountDownLatch latch;

    public Member(BellNote bn, int threadNum, Lock lock, BlockingQueue<BellNote> songOrder, SourceDataLine sourceDataLine, CountDownLatch latch) {
        this.bn = bn;

        this.t = new Thread(this, "Member " + threadNum + " plays: " + bn.toString());

        this.lock = lock;
        this.songOrder = songOrder;
        this.line = sourceDataLine;
        this.latch = latch;
        running = true;
    }

    public void start(){
        t.start();
    }

    public void playNote() {
        System.out.println(t.getName());
        final int ms = Math.min(bn.getLength().getTimeMs(), Note.MEASURE_LENGTH_SEC * 1000);
        final int length = Note.SAMPLE_RATE * ms / 1000;
        line.write(bn.getNote().sample(), 0, length);
        line.write(Note.REST.sample(), 0, 50);
    }

    @Override
    public void run() {
        while (running) {
//            System.out.println(t.getName() + " attempting to get lock.");
            lock.lock();
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
//            System.out.println(t.getName() + " got the lock!");

            try {
                if(songOrder.peek() != null && songOrder.peek().equals(bn)) {
//                    System.out.println(t.getName() + " should play their note.");
                    try {
                        BellNote note = songOrder.take();
//                        System.out.println(songOrder.size() + " " + songOrder.peek());

                        playNote();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            } finally {
                lock.unlock();
//                System.out.println(t.getName() + " released the lock!");
            }
        }
    }

    public void stop(){
        running = false;
        waitToStop();
    }

    public void waitToStop(){
        try {
            t.join();
        } catch (InterruptedException e) {
            System.err.println(t.getName() + " stop malfunction");
        }
    }

    public Thread.State getState(){
        return t.getState();
    }
}
