package com.tbot.ruler.service.things;

import com.tbot.ruler.appliances.Appliance;
import com.tbot.ruler.persistance.AppliancesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Scope("singleton")
public class AppliancesLifetimeService {

    @Autowired
    private AppliancesRepository appliancesRepository;
    @Autowired
    private ApplianceFactory applianceFactory;

    private List<Appliance> appliances;
    private Map<String, Appliance> appliancesMap;

    @PostConstruct
    public void init() {
        appliances = applianceFactory.buildAppliances(appliancesRepository.findAll());
        appliancesMap = appliances.stream()
                .collect(Collectors.toMap(Appliance::getUuid, Function.identity()));
    }

    public List<Appliance> getAllAppliances() {
        return appliances;
    }

    public Appliance getApplianceByUuid(String uuid) {
        return appliancesMap.get(uuid);
    }
}
