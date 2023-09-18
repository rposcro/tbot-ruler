package com.tbot.ruler.persistance.json.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BindingDTO {

    private String note;
    private String senderId;
    private List<String> consumerIds;
}
