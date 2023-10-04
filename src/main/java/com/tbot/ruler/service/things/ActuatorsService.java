package com.tbot.ruler.service.things;

import com.tbot.ruler.subjects.Actuator;
import com.tbot.ruler.subjects.ActuatorState;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ActuatorsService implements InitializingBean {

    @Autowired
    private SubjectLifetimeService subjectLifetimeService;

    private Map<String, Actuator> actuatorsMap;

    @Override
    public void afterPropertiesSet() {
        this.actuatorsMap = subjectLifetimeService.getAllActuators().stream()
                .collect(Collectors.toMap(Actuator::getUuid, Function.identity()));
    }

    public List<Actuator> findAllActuators() {
        return subjectLifetimeService.getAllActuators();
    }

    public ActuatorState getActuatorState(String actuatorUuid) {
        Actuator actuator = actuatorsMap.get(actuatorUuid);
        return actuator != null ? actuator.getState() : null;
    }
}
