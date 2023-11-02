package com.tbot.ruler.persistance.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("bindings")
public class BindingEntity {

    @Column("sender_uuid")
    private String senderUuid;

    @Column("receiver_uuid")
    private String receiverUuid;
}
