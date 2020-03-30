package edu.phystech.sbt.java.main;

import org.junit.Test;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

public class FixedThreadPoolTest {

    static class TestExecute implements Runnable {
        private int x;
        private final Object lock = new Object();

        public int getX() {
            return x;
        }

        @Override
        public void run() {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (lock) {
                x++;
            }
        }
    }

    @Test
    public void execute() {
        TestExecute test = new TestExecute();
        ThreadPool pool = new FixedThreadPool(4);
        pool.start();
        for (int i = 0; i < 10; i++) {
            pool.execute(test);
        }
        assertEquals(0, test.getX());
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(10, test.getX());
    }
}