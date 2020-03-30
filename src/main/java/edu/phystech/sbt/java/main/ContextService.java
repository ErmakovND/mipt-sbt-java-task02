package edu.phystech.sbt.java.main;

public interface ContextService extends Context {
    void addCompletedTask();

    void addFailedTask();

    void addInterruptedTask();

    boolean isInterrupted();
}
