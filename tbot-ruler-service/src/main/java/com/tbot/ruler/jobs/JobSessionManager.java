package com.tbot.ruler.jobs;

public final class JobSessionManager {

    private static ThreadLocal<Job> sessionContext = new ThreadLocal<>();

    public static Job getContext() {
        return sessionContext.get();
    }

    public static void setContext(Job job) {
        sessionContext.set(job);
    }

    public static Job removeContext() {
        Job context = sessionContext.get();
        sessionContext.remove();
        return context;
    }
}
