package com.tbot.ruler.jobs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobTriggerContext {

    private long lastScheduledExecutionTime;
    private long lastCompletionTime;

    public boolean isFirstRun() {
        return lastCompletionTime == 0;
    }
}
