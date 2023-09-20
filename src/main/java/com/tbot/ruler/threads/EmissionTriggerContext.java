package com.tbot.ruler.threads;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class EmissionTriggerContext {

    private Date lastScheduledExecutionTime;
}
