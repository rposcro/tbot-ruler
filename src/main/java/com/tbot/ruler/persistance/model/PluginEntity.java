package com.tbot.ruler.persistance.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Table("plugins")
@Getter
@Builder
@AllArgsConstructor
public class PluginEntity {

    @Id
    @Column("plugin_id")
    private long pluginId;

    @NonNull
    @JsonProperty(required = true)
    @Column("plugin_uuid")
    private String pluginUuid;

    @NonNull
    @JsonProperty(required = true)
    @Column("builder_class")
    private String builderClass;

    @NonNull
    @JsonProperty(required = true)
    @Column("name")
    private String name;

    @Column("configuration")
    private JsonNode configuration;

    @Transient
    private List<ThingEntity> things;
}
