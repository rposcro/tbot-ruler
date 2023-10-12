package com.tbot.ruler.persistance.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
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
@Table("plugins")
public class PluginEntity {

    @Id
    @Column("plugin_id")
    private long pluginId;

    @NonNull
    @Column("plugin_uuid")
    private String pluginUuid;

    @NonNull
    @Column("factory_class")
    private String factoryClass;

    @NonNull
    @Column("name")
    private String name;

    @Column("configuration")
    private JsonNode configuration;

    @Version
    @Column("version")
    private int version;

    @Transient
    @Builder.Default
    private List<ThingEntity> things = Collections.emptyList();
}
