package com.tbot.ruler.plugins.jwavez;

import com.tbot.ruler.things.Collector;
import com.tbot.ruler.things.builder.dto.CollectorDTO;
import com.tbot.ruler.things.exceptions.PluginException;

public interface CollectorBuilder {

    String getReference();
    Collector buildCollector(CollectorDTO collectorDTO) throws PluginException;
}
