package com.tbot.ruler.persistance;

import com.tbot.ruler.persistance.model.ApplianceState;

import java.util.Optional;

public interface ApplianceStateRepository {

    ApplianceState save(ApplianceState applianceState);

    Optional<ApplianceState> findByKey(String applianceKey);
}
