package com.tbot.ruler.controller.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.tbot.ruler.appliances.state.State;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
@JsonRootName(value = "appliance")
public class ApplianceEntity {

	@JsonProperty(value = "_links")
	private Map<String, String> links;
	private String id;
	private String name;
	private String description;
	private State stateValue;
}
