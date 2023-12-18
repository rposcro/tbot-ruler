package com.tbot.ruler.service.lifecycle;

import com.tbot.ruler.exceptions.LifecycleException;
import com.tbot.ruler.jobs.JobBundle;
import com.tbot.ruler.jobs.JobRunner;
import com.tbot.ruler.subjects.Subject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Scope("singleton")
public class JobsLifecycleService {

    @Autowired
    private ConcurrentTaskExecutor jobRunnersExecutor;

    private final Map<String, List<JobRunner>> jobsPerSubjectUuid = new HashMap<>();

    public void startSubjectJobs(Subject subject) {
        Collection<JobBundle> jobBundles = subject.getJobBundles();

        if (jobsPerSubjectUuid.containsKey(subject.getUuid())) {
            throw new LifecycleException("Job Lifecycle: Jobs for subject " + subject.getUuid() + " are already running!");
        } else {
            List<JobRunner> runners = new ArrayList<>(jobBundles.size());
            this.jobsPerSubjectUuid.put(subject.getUuid(), runners);

            for (JobBundle jobBundle : jobBundles) {
                JobRunner runner = new JobRunner(jobBundle);
                runners.add(runner);
                jobRunnersExecutor.execute(runner);
                log.info("Job Lifecycle: Started job {} of subject {}",
                        jobBundle.getJob().getName(), subject.getUuid());
            }
        }
    }

    public void stopSubjectJobs(Subject subject) {
        List<JobRunner> runners = jobsPerSubjectUuid.get(subject.getUuid());

        if (runners != null) {
            runners.forEach(JobRunner::stop);
            log.info("Job Lifecycle: Jobs stopped for subject {}", subject.getUuid());
        } else {
            log.info("Job Lifecycle: No jobs running for subject {}, nothing to stop", subject.getUuid());
        }
    }
}
