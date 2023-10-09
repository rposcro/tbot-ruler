package com.tbot.ruler.service.things;

import com.tbot.ruler.service.lifetime.SubjectLifetimeService;
import com.tbot.ruler.subjects.Actuator;
import com.tbot.ruler.subjects.ActuatorState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActuatorsService {

    @Autowired
    private SubjectLifetimeService subjectLifetimeService;

    public List<Actuator> findAllActuators() {
        return subjectLifetimeService.getAllActuators();
    }

    public ActuatorState getActuatorState(String actuatorUuid) {
        Actuator actuator = subjectLifetimeService.getActuatorByUuid(actuatorUuid);
        return actuator != null ? actuator.getState() : null;
    }
}
