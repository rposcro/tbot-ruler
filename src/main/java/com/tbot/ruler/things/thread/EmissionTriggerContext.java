package com.tbot.ruler.things.thread;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class EmissionTriggerContext {
    private Date lastScheduledExecutionTime;
}
