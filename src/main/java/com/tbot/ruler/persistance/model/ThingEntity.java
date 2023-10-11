package com.tbot.ruler.persistance.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("things")
public class ThingEntity {

    @Id
    @Column("thing_id")
    private long thingId;

    @Column("thing_uuid")
    private String thingUuid;

    @Column("plugin_id")
    private long pluginId;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Column("configuration")
    private JsonNode configuration;

    @Version
    @Column("version")
    private int version;

    @Transient
    @Builder.Default
    private List<ActuatorEntity> actuators = Collections.emptyList();
}
