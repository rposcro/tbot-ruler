package com.tbot.ruler.things;

import java.util.Date;

public interface EmissionTrigger {
    public Date nextEmissionTime(EmissionTriggerContext context);
}
