package com.tbot.ruler.console.views.routes.stencils;

import com.tbot.ruler.console.views.components.AbstractEditDialog;
import com.tbot.ruler.console.views.components.handlers.EditDialogSubmittedHandler;
import com.tbot.ruler.console.views.validation.FormValidator;
import com.tbot.ruler.controller.admin.payload.StencilResponse;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import static com.tbot.ruler.console.utils.FormUtils.orEmpty;

public class StencilEditDialog extends AbstractEditDialog<StencilEditDialog> {

    private final TextField txtUuid = new TextField();
    private final TextField txtOwner = new TextField();
    private final TextField txtType = new TextField();

    @Getter
    private final StencilResponse originalStencil;

    @Builder
    public StencilEditDialog(
            @NonNull Boolean updateMode,
            @NonNull EditDialogSubmittedHandler<StencilEditDialog> submitHandler,
            StencilResponse originalStencil
    ) {
        super(updateMode, submitHandler);

        if (updateMode && originalStencil == null) {
            throw new NullPointerException("Original entity cannot be null in update mode!");
        }

        this.originalStencil = originalStencil;

        setHeaderTitle(updateMode ? "Edit Stencil" : "Create Stencil");
        setModal(true);
        setWidth("60%");

        setUpFormFields();
        add(constructForm());

        this.setResizable(true);
        this.setDraggable(true);
    }

    public String getOwner() {
        return txtOwner.getValue();
    }

    public String getType() {
        return txtType.getValue();
    }

    @Override
    protected void setUpFormFields() {
        txtUuid.setLabel("Uuid");
        txtUuid.setEnabled(false);
        txtOwner.setLabel("Owner");
        txtType.setLabel("Type");

        if (originalStencil != null) {
            txtUuid.setValue(orEmpty(originalStencil.getStencilUuid()));
            txtOwner.setValue(orEmpty(originalStencil.getOwner()));
            txtType.setValue(orEmpty(originalStencil.getType()));
        } else {
            txtUuid.setValue("");
            txtOwner.setValue("");
            txtType.setValue("");
        }
    }

    @Override
    protected FormValidator constructFormValidator() {
        return new FormValidator()
                .notEmpty(txtOwner)
                .notEmpty(txtType);
    }

    private Component constructForm() {
        FormLayout form = new FormLayout();
        if (updateMode) {
            form.add(txtUuid, txtOwner, txtType);
        } else {
            form.add(txtOwner, txtType);
        }
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        return form;
    }
}
