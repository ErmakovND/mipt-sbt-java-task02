package edu.phystech.sbt.java.main;

import org.junit.Test;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

public class ScalableThreadPoolTest {

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
        ThreadPool pool = new ScalableThreadPool(2, 4);
        pool.start();
        try {
            sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 3; i++) {
            pool.execute(test);
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            sleep(1100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(3, test.getX());
    }
}
