package com.tbot.ruler.persistance;

import com.tbot.ruler.persistance.model.ApplianceEntity;

import java.util.List;
import java.util.Optional;

public interface AppliancesRepository {

    List<ApplianceEntity> findAll();

    Optional<ApplianceEntity> findByUuid(String applianceUuid);
}
