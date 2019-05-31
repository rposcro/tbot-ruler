package com.tbot.ruler.broker;

import com.tbot.ruler.signals.EmitterSignal;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class SignalEmissionBrokerProxy implements Consumer<EmitterSignal> {

    @Autowired
    private BeanFactory beanFactory;

    private SignalEmissionBroker emissionBroker;


    @Override
    public void accept(EmitterSignal signal) {
        emissionBroker.receiveSignalFromEmitter(signal);
    }

    @Override
    public Consumer<EmitterSignal> andThen(Consumer<? super EmitterSignal> after) {
        return null;
    }

    @EventListener
    public void finishInitialization(ApplicationStartedEvent event) {
        this.emissionBroker = beanFactory.getBean(SignalEmissionBroker.class);
    }

}
