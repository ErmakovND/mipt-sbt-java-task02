package edu.phystech.sbt.java.main;

import org.junit.Test;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

public class ExecutionManagerImplTest {

    @Test
    public void execute() {
        ExecutionManager executionManager = new ExecutionManagerImpl(2);
        Runnable sleepTask = () -> {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        final int[] c = {0};
        Context context = executionManager.execute(() -> {
            c[0]++;}, sleepTask, sleepTask, sleepTask);
        assertEquals(0, context.getCompletedTaskCount());
        try {
            sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        context.interrupt();
        while (!context.isFinished()) {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        assertEquals(2, context.getCompletedTaskCount());
        assertEquals(1, context.getInterruptedTaskCount());
        assertEquals(1, c[0]);
    }
}