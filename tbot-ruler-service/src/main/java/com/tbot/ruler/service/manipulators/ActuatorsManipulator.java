package com.tbot.ruler.service.manipulators;

import com.tbot.ruler.exceptions.LifecycleException;
import com.tbot.ruler.persistance.ActuatorsRepository;
import com.tbot.ruler.persistance.BindingsRepository;
import com.tbot.ruler.persistance.model.ActuatorEntity;
import com.tbot.ruler.service.lifecycle.ActuatorsLifecycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActuatorsManipulator {

    @Autowired
    private ActuatorsRepository actuatorsRepository;

    @Autowired
    private BindingsRepository bindingsRepository;

    @Autowired
    private ActuatorsLifecycleService actuatorsLifecycleService;

    public ActuatorEntity createActuator(ActuatorEntity actuatorEntity) {
        actuatorEntity = actuatorsRepository.save(actuatorEntity);
        actuatorsLifecycleService.activateActuator(actuatorEntity);
        return actuatorEntity;
    }

    public ActuatorEntity updateActuator(ActuatorEntity actuatorEntity) {
        actuatorsLifecycleService.deactivateActuator(actuatorEntity);
        actuatorEntity = actuatorsRepository.save(actuatorEntity);
        actuatorsLifecycleService.activateActuator(actuatorEntity);
        return actuatorEntity;
    }

    public void removeActuator(ActuatorEntity actuatorEntity) {
        assertConsistency(actuatorEntity.getActuatorUuid());
        actuatorsRepository.findByUuid(actuatorEntity.getActuatorUuid()).ifPresent(entity -> {
            actuatorsLifecycleService.deactivateActuator(entity);
            actuatorsRepository.delete(entity);
        });
    }

    private void assertConsistency(String actuatorUuid) {
        if (bindingsRepository.bindingWithUuidExists(actuatorUuid)) {
            throw new LifecycleException("Cannot remove actuator %s, binding(s) exist(s)!", actuatorUuid);
        }
    }
}
