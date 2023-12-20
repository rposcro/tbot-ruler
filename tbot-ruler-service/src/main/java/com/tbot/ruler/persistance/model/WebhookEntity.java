package com.tbot.ruler.persistance.model;

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
@Table("webhooks")
public class WebhookEntity {

    @Id
    @Column("webhook_id")
    private long webhookId;

    @Column("webhook_uuid")
    private String webhookUuid;

    @Column("owner")
    private String owner;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Version
    @Column("version")
    private int version;
}
