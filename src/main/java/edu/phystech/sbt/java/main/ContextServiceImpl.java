package edu.phystech.sbt.java.main;

public class ContextServiceImpl implements ContextService {

    private final int count;
    private int completedCount;
    private final Object lockCompleted = new Object();
    private int failedCount;
    private final Object lockFailed = new Object();
    private int interruptedCount;
    private final Object lockInterrupted = new Object();
    private volatile boolean interrupted;

    public ContextServiceImpl(int count) {
        this.count = count;
    }

    @Override
    public void addCompletedTask() {
        synchronized (lockCompleted) {
            completedCount++;
        }
        notifyFinished();
    }

    @Override
    public void addFailedTask() {
        synchronized (lockFailed) {
            failedCount++;
        }
        notifyFinished();
    }

    @Override
    public void addInterruptedTask() {
        synchronized (lockInterrupted) {
            interruptedCount++;
        }
        notifyFinished();
    }

    @Override
    public void interrupt() {
        interrupted = true;
        notifyFinished();
    }

    @Override
    public boolean isFinished() {
        return count == completedCount + failedCount + interruptedCount;
    }

    @Override
    public boolean isInterrupted() {
        return interrupted;
    }

    @Override
    public int getCompletedTaskCount() {
        return completedCount;
    }

    @Override
    public int getFailedTaskCount() {
        return failedCount;
    }

    @Override
    public int getInterruptedTaskCount() {
        return interruptedCount;
    }

    private void notifyFinished() {
        if (isFinished()) {
            synchronized (this) {
                this.notify();
            }
        }
    }
}
