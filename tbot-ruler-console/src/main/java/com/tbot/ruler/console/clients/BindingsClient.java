package com.tbot.ruler.console.clients;

import com.tbot.ruler.controller.admin.payload.BindingCreateRequest;
import com.tbot.ruler.controller.admin.payload.BindingDeleteRequest;
import com.tbot.ruler.controller.admin.payload.BindingResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BindingsClient extends AbstractApiClient {

    @Autowired
    private BindingsAdminApi bindingsAdminApi;

    public List<BindingResponse> getAllBindings() {
        return executeApiFunction(() -> bindingsAdminApi.getBindings().execute());
    }

    public void createBinding(String senderUuid, String receiverUuid) {
        executeApiFunction(() -> bindingsAdminApi.createThing(BindingCreateRequest.builder()
                        .senderUuid(senderUuid)
                        .receiverUuid(receiverUuid)
                        .build())
                .execute());
    }

    public void deleteBinding(String senderUuid, String receiverUuid) {
        executeApiFunction(() -> bindingsAdminApi.deleteThing(BindingDeleteRequest.builder()
                        .senderUuid(senderUuid)
                        .receiverUuid(receiverUuid)
                        .build())
                .execute());
    }
}
