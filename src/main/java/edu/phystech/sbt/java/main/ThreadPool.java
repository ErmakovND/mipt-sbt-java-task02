package edu.phystech.sbt.java.main;

public interface ThreadPool {
    void start();
    void execute(Runnable runnable);
}
