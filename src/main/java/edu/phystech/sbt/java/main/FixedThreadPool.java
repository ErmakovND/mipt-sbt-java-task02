package edu.phystech.sbt.java.main;

import java.util.*;

public class FixedThreadPool implements ThreadPool {
    private final Queue<Runnable> tasks;
    private final List<Thread> threads;

    private class Worker extends Thread {
        @Override
        public void run() {
            while (true) {
                Runnable task;
                synchronized (tasks) {
                    while (tasks.isEmpty()) {
                        try {
                            tasks.wait();
                        } catch (InterruptedException ignored) {
                        }
                    }
                    task = tasks.poll();
                }
                task.run();
            }
        }
    }

    public FixedThreadPool(int threadCount) {
        tasks = new ArrayDeque<>();
        threads = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            threads.add(new Worker());
        }
    }

    @Override
    public void start() {
        for (Thread thread : threads) {
            thread.start();
        }
    }

    @Override
    public void execute(Runnable runnable) {
        synchronized (tasks) {
            tasks.add(runnable);
            tasks.notify();
        }
    }
}
