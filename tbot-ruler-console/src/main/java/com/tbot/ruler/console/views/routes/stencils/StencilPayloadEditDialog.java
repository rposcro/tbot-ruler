package com.tbot.ruler.console.views.routes.stencils;

import com.fasterxml.jackson.databind.JsonNode;
import com.tbot.ruler.console.views.components.AbstractEditDialog;
import com.tbot.ruler.console.views.components.JsonEditor;
import com.tbot.ruler.console.views.components.handlers.EditDialogSubmittedHandler;
import com.tbot.ruler.console.views.validation.FormValidator;
import com.tbot.ruler.controller.admin.payload.StencilResponse;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

public class StencilPayloadEditDialog extends AbstractEditDialog<StencilPayloadEditDialog> {

    private final JsonEditor txtJson;

    @Getter
    private final StencilResponse originalStencil;

    @Builder
    public StencilPayloadEditDialog(
            @NonNull Boolean updateMode,
            @NonNull EditDialogSubmittedHandler<StencilPayloadEditDialog> submitHandler,
            @NonNull StencilResponse originalStencil
    ) {
        super(updateMode, submitHandler);

        this.originalStencil = originalStencil;
        this.txtJson = new JsonEditor("Payload");

        setHeaderTitle(updateMode ? "Edit Stencil Payload" : "Create Stencil Payload");
        setModal(true);
        setWidth("90%");
        setHeight("90%");

        setUpFormFields();
        add(constructForm());

        this.setResizable(true);
        this.setDraggable(true);
    }

    public JsonNode getPayload() {
        return this.txtJson.getJson();
    }

    @Override
    protected void setUpFormFields() {
        txtJson.setJson(originalStencil.getPayload());
    }

    @Override
    protected FormValidator constructFormValidator() {
        return new FormValidator()
                .validJson(txtJson);
    }

    private Component constructForm() {
        VerticalLayout form = new VerticalLayout();
        form.add(txtJson);
        form.setWidthFull();
        form.setHeightFull();
        return form;
    }
}
