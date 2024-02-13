package com.tbot.ruler.console.views.routes.stencils;

import com.tbot.ruler.console.accessors.StencilsAccessor;
import com.tbot.ruler.console.utils.FormUtils;
import com.tbot.ruler.console.views.AbstractActionsSupport;
import com.tbot.ruler.console.views.components.handlers.EditDialogSubmittedHandler;
import com.tbot.ruler.controller.admin.payload.StencilCreateRequest;
import com.tbot.ruler.controller.admin.payload.StencilResponse;
import com.tbot.ruler.controller.admin.payload.StencilUpdateRequest;
import com.vaadin.flow.spring.annotation.RouteScope;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.tbot.ruler.console.views.PopupNotifier.notifyInfo;

@RouteScope
@SpringComponent
public class StencilsActionsSupport extends AbstractActionsSupport {

    @Autowired
    private StencilsAccessor stencilsAccessor;

    public List<StencilResponse> getAllStencils() {
        return stencilsAccessor.getAllStencils();
    }

    public void launchStencilCreate(EditDialogSubmittedHandler<StencilEditDialog> submitHandler) {
        StencilEditDialog.builder()
                .updateMode(false)
                .submitHandler(submitHandler)
                .build()
                .open();
    }

    public void launchStencilEdit(StencilResponse stencil, EditDialogSubmittedHandler<StencilEditDialog> submitHandler) {
        StencilEditDialog.builder()
                .updateMode(true)
                .submitHandler(submitHandler)
                .originalStencil(stencil)
                .build()
                .open();
    }

    public void launchStencilPayloadEdit(StencilResponse stencil, EditDialogSubmittedHandler<StencilPayloadEditDialog> submitHandler) {
        StencilPayloadEditDialog.builder()
                .updateMode(true)
                .submitHandler(submitHandler)
                .originalStencil(stencil)
                .build()
                .open();
    }

    public boolean createStencil(StencilEditDialog dialog) {
        return handlingExceptions(() -> {
            StencilCreateRequest request = StencilCreateRequest.builder()
                    .owner(dialog.getOwner())
                    .type(dialog.getType())
                    .payload(FormUtils.asJsonNode("{}"))
                    .build();
            stencilsAccessor.createStencil(request);
            notifyInfo("Stencil %s of %s updated", request.getType(), request.getOwner());
        });
    }

    public boolean updateStencil(StencilEditDialog dialog) {
        return handlingExceptions(() -> {
            StencilResponse original = dialog.getOriginalStencil();
            StencilUpdateRequest request = StencilUpdateRequest.builder()
                    .owner(dialog.getOwner())
                    .type(dialog.getType())
                    .payload(original.getPayload())
                    .build();
            stencilsAccessor.updateStencil(original.getStencilUuid(), request);
            notifyInfo("Stencil %s of %s updated", original.getType(), original.getOwner());
        });
    }

    public boolean updateStencilPayload(StencilPayloadEditDialog dialog) {
        return handlingExceptions(() -> {
            StencilResponse original = dialog.getOriginalStencil();
            StencilUpdateRequest request = StencilUpdateRequest.builder()
                    .owner(original.getOwner())
                    .type(original.getType())
                    .payload(dialog.getPayload())
                    .build();
            stencilsAccessor.updateStencil(original.getStencilUuid(), request);
            notifyInfo("Stencil Payload %s of %s updated", original.getType(), original.getOwner());
        });
    }
}
