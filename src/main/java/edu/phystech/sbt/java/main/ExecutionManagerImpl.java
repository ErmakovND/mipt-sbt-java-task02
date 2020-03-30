package edu.phystech.sbt.java.main;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class ExecutionManagerImpl implements ExecutionManager {

    private final Queue<Runnable> tasks;
    private final List<Thread> threads;

    private static class TaskContext implements Runnable {
        private final Runnable task;
        private final ContextService contextService;

        public TaskContext(Runnable task, ContextService contextService) {
            this.task = task;
            this.contextService = contextService;
        }

        @Override
        public void run() {
            if (contextService.isInterrupted()) {
                contextService.addInterruptedTask();
                return;
            }
            try {
                task.run();
                contextService.addCompletedTask();
            } catch (Exception e) {
                contextService.addFailedTask();
            }
        }
    }

    private static class CallbackContext implements Runnable {
        private final Runnable callback;
        private final ContextService contextService;

        public CallbackContext(Runnable callback, ContextService contextService) {
            this.callback = callback;
            this.contextService = contextService;
        }

        @Override
        public void run() {
            synchronized (contextService) {
                while (!contextService.isFinished()) {
                    try {
                        contextService.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            callback.run();
        }
    }

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

    public ExecutionManagerImpl(int threadCount) {
        threads = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            threads.add(new Worker());
        }
        tasks = new ArrayDeque<>();
        for (Thread thread : threads) {
            thread.start();
        }
    }

    @Override
    public Context execute(Runnable callback, Runnable... tasks) {
        int count = tasks.length;
        ContextService contextService = new ContextServiceImpl(count);
        Context context = new ContextImpl(contextService);
        for (Runnable task : tasks) {
            synchronized (this.tasks) {
                this.tasks.add(new TaskContext(task, contextService));
                this.tasks.notify();
            }
        }
        synchronized (this.tasks) {
            this.tasks.add(new CallbackContext(callback, contextService));
            this.tasks.notify();
        }
        return context;
    }
}