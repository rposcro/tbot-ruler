package com.tbot.ruler.service.lifecycle;

import com.tbot.ruler.persistance.ThingsRepository;
import com.tbot.ruler.subjects.thing.RulerThing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Scope("singleton")
public class ThingsLifecycleService {

    @Autowired
    private ThingsRepository thingsRepository;

    @Autowired
    private ThingFactoryComponent thingFactoryComponent;

    @Autowired
    private JobsLifecycleService jobsLifecycleService;

    private List<RulerThing> things;
    private Map<Long, RulerThing> thingsIdMap;
    private Map<String, RulerThing> thingsUuidMap;

    public List<RulerThing> getAllThings() {
        return Collections.unmodifiableList(things);
    }

    public RulerThing getThingById(long id) {
        return thingsIdMap.get(id);
    }

    public RulerThing getThingByUuid(String uuid) {
        return thingsUuidMap.get(uuid);
    }

    public void activateAllThings() {
        things = new LinkedList<>();
        thingsIdMap = new HashMap<>();
        thingsUuidMap = new HashMap<>();
        thingsRepository.findAll().forEach(thingEntity -> {
            RulerThing thing = thingFactoryComponent.buildThing(thingEntity);
            things.add(thing);
            thingsIdMap.put(thingEntity.getThingId(), thing);
            thingsUuidMap.put(thingEntity.getThingUuid(), thing);

            if (thing.hasJobs()) {
                jobsLifecycleService.startSubjectJobs(thing);
            }
        });
    }
}
