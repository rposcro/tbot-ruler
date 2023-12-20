package com.tbot.ruler.persistance.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("actuators")
public class ActuatorEntity {

    @Id
    @Column("actuator_id")
    private long actuatorId;

    @Column("actuator_uuid")
    private String actuatorUuid;

    @Column("thing_id")
    private long thingId;

    @Column("plugin_id")
    private long pluginId;

    @Column("reference")
    private String reference;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Column("configuration")
    private JsonNode configuration;

    @Version
    @Column("version")
    private int version;
}
