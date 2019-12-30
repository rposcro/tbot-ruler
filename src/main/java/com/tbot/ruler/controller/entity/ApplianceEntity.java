package com.tbot.ruler.controller.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.tbot.ruler.appliances.Appliance;
import com.tbot.ruler.appliances.state.State;

import java.util.Map;

@JsonRootName(value = "appliance")
public class ApplianceEntity {

	private Map<String, String> links;
	private Appliance appliance;

	public ApplianceEntity(Appliance appliance, Map<String, String> links) {
	    this.appliance = appliance;
	    this.links = links;
	}

	@JsonProperty(value = "id")
	public String getId() {
		return appliance.getId().getValue();
	}

	public String getName() {
		return appliance.getName();
	}

	public String getDescription() {
		return appliance.getDescription();
	}

	public State getStateValue() {
		return (State) appliance.getState().orElse(null);
	}

	@JsonProperty(value = "_links")
	public Map<String, String> getLinks() {
		return this.links;
	}
}
