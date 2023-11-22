package com.tbot.ruler.console.accessors;

import com.tbot.ruler.console.clients.BindingsClient;
import com.tbot.ruler.controller.admin.payload.BindingResponse;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RouteScope
@SpringComponent
public class RouteBindingsAccessor {

    @Autowired
    private BindingsClient bindingsClient;

    public List<BindingResponse> getAllBindings() {
        return bindingsClient.getAllBindings();
    }

    public void createBinding(String senderUuid, String receiverUuid) {
        bindingsClient.createBinding(senderUuid, receiverUuid);
    }

    public void deleteBinding(String senderUuid, String receiverUuid) {
        bindingsClient.deleteBinding(senderUuid, receiverUuid);
    }
}
