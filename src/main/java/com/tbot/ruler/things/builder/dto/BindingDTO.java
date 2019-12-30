package com.tbot.ruler.things.builder.dto;

import com.tbot.ruler.things.*;
import lombok.Data;

import java.util.List;

@Data
public class BindingDTO {

    private String note;
    private ItemId senderId;
    private List<ItemId> consumerIds;
}
