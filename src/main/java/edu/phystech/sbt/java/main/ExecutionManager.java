package edu.phystech.sbt.java.main;

public interface ExecutionManager {
    Context execute(Runnable callback, Runnable... tasks);
}