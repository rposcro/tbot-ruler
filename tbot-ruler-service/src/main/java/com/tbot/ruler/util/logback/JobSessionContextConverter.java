package com.tbot.ruler.util.logback;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.tbot.ruler.jobs.Job;
import com.tbot.ruler.jobs.JobSessionManager;

public class JobSessionContextConverter extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent iLoggingEvent) {
        Job job = JobSessionManager.getContext();

        if (job != null) {
            StringBuilder builder = new StringBuilder()
                .append("Job: ")
                .append(job.getJobName())
                .append(" - ");
            return builder.toString();
        }

        return "";
    }
}
