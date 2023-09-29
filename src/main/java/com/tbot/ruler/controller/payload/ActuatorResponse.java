package com.tbot.ruler.controller.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonRootName(value = "actuator")
public class ActuatorResponse {

	@JsonProperty(required = true)
	private String uuid;

	@JsonProperty(required = true)
	private String name;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String description;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Object state;
}
