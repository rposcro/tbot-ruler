package com.tbot.ruler.things.builder.dto;

import com.tbot.ruler.appliances.ApplianceId;
import com.tbot.ruler.things.ItemId;
import lombok.Data;

import java.util.List;

@Data
public class BindingDTO {

    private ApplianceId applianceId;
    private List<ItemId> emitterIds;
    private List<ItemId> collectorIds;
}
