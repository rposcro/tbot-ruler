package com.tbot.ruler.things;

import com.tbot.ruler.signals.ApplianceSignal;

public interface Collector {

    public CollectorId getId();
    public CollectorMetadata getMetadata();
    public void collectSignal(ApplianceSignal signal) throws CollectorException;
}
