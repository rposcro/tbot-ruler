package com.tbot.ruler.service.things;

import com.tbot.ruler.service.lifecycle.SubjectLifecycleService;
import com.tbot.ruler.subjects.actuator.Actuator;
import com.tbot.ruler.subjects.actuator.ActuatorState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActuatorsService {

    @Autowired
    private SubjectLifecycleService subjectLifecycleService;

    public List<Actuator> findAllActuators() {
        return subjectLifecycleService.getAllActuators();
    }

    public ActuatorState getActuatorState(String actuatorUuid) {
        Actuator actuator = subjectLifecycleService.getActuatorByUuid(actuatorUuid);
        return actuator != null ? actuator.getState() : null;
    }
}
