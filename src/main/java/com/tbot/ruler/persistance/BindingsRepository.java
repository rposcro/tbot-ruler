package com.tbot.ruler.persistance;

import com.tbot.ruler.persistance.model.BindingEntity;

import java.util.List;

public interface BindingsRepository {

    List<BindingEntity> findBySenderUuid(String senderUuid);
}
