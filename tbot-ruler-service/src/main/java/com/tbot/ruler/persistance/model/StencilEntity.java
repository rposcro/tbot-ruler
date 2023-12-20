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
@Table("stencils")
public class StencilEntity {

    @Id
    @Column("stencil_id")
    private long stencilId;

    @Column("stencil_uuid")
    private String stencilUuid;

    @Column("owner")
    private String owner;

    @Column("type")
    private String type;

    @Column("payload")
    private JsonNode payload;

    @Version
    @Column("version")
    private int version;
}
