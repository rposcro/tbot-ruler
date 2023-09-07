package com.tbot.ruler.controller.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
@JsonRootName(value = "appliance")
public class ApplianceResponse {

	@JsonProperty(value = "_links")
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private Map<String, String> links;

	@JsonProperty(required = true)
	private String id;

	@JsonProperty(required = true)
	private String name;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String description;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Object stateValue;
}
