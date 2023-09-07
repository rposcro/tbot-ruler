package com.tbot.ruler.persistance;

import java.util.Optional;

public interface ApplianceStateRepository {

    ApplianceState save(ApplianceState applianceState);

    Optional<ApplianceState> findByKey(String applianceKey);
}
