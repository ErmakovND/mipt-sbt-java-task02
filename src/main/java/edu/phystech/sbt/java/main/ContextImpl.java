package edu.phystech.sbt.java.main;

public class ContextImpl implements Context {

    private ContextService contextService;

    public ContextImpl(ContextService contextService) {
        this.contextService = contextService;
    }

    @Override
    public int getCompletedTaskCount() {
        return contextService.getCompletedTaskCount();
    }

    @Override
    public int getFailedTaskCount() {
        return contextService.getFailedTaskCount();
    }

    @Override
    public int getInterruptedTaskCount() {
        return contextService.getInterruptedTaskCount();
    }

    @Override
    public void interrupt() {
        contextService.interrupt();
    }

    @Override
    public boolean isFinished() {
        return contextService.isFinished();
    }
}
