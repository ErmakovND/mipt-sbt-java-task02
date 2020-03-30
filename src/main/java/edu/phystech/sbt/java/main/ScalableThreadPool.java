package edu.phystech.sbt.java.main;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class ScalableThreadPool implements ThreadPool {
    private final int minThreadCount;
    private final int maxThreadCount;
    private int runThreadCount;
    private int workThreadCount;
    private final Queue<Runnable> tasks;
    private final List<Thread> threads;

    private class Worker extends Thread {
        @Override
        public void run() {
            while (true) {
                Runnable task;
                synchronized (tasks) {
                    workThreadCount--;
                    while (tasks.isEmpty()) {
                        if (runThreadCount > minThreadCount) {
                            runThreadCount--;
                            return;
                        }
                        try {
                            tasks.wait();
                        } catch (InterruptedException ignored) {
                        }
                    }
                    task = tasks.poll();
                    workThreadCount++;
                }
                task.run();
            }
        }
    }

    public ScalableThreadPool(int minThreadCount, int maxThreadCount) {
        this.minThreadCount = minThreadCount;
        this.maxThreadCount = maxThreadCount;
        runThreadCount = 0;
        threads = new ArrayList<>();
        tasks = new ArrayDeque<>();
        for (int i = 0; i < minThreadCount; i++) {
            threads.add(new Worker());
        }
    }

    @Override
    public void start() {
        synchronized (tasks) {
            runThreadCount = minThreadCount;
            workThreadCount = runThreadCount;
        }
        for (Thread thread : threads) {
            thread.start();
        }
    }

    @Override
    public void execute(Runnable runnable) {
        synchronized (tasks) {
            if (workThreadCount == runThreadCount && runThreadCount < maxThreadCount) {
                Worker worker = new Worker();
                threads.add(worker);
                worker.start();
                runThreadCount++;
                workThreadCount++;
            }
            tasks.add(runnable);
            tasks.notify();
        }
    }
}
