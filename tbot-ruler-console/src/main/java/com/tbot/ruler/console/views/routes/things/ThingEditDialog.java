package com.tbot.ruler.console.views.routes.things;

import com.fasterxml.jackson.databind.JsonNode;
import com.tbot.ruler.console.utils.FormUtils;
import com.tbot.ruler.console.views.components.AbstractEditDialog;
import com.tbot.ruler.console.views.components.handlers.EditDialogSubmittedHandler;
import com.tbot.ruler.console.views.validation.FormValidator;
import com.tbot.ruler.controller.admin.payload.ThingResponse;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Builder;
import lombok.NonNull;

import static com.tbot.ruler.console.utils.FormUtils.asJsonNode;
import static com.tbot.ruler.console.utils.FormUtils.orEmpty;

public class ThingEditDialog extends AbstractEditDialog<ThingEditDialog> {

    private final TextField txtUuid = new TextField();
    private final TextField txtName = new TextField();
    private final TextArea txtDescription = new TextArea();
    private final TextArea txtConfiguration = new TextArea();

    private final boolean updateMode;
    private final ThingResponse original;

    @Builder
    public ThingEditDialog(
            @NonNull Boolean updateMode,
            @NonNull EditDialogSubmittedHandler<ThingEditDialog> submitHandler,
            ThingResponse original
    ) {
        super(updateMode, submitHandler);

        if (updateMode && original == null) {
            throw new NullPointerException("Original entity cannot be null in update mode!");
        }

        this.updateMode = updateMode;
        this.original = original;

        setHeaderTitle(updateMode ? "Edit Thing" : "Create Thing");
        setModal(true);
        setWidth("60%");

        setUpFormFields();
        add(constructForm());
    }

    public boolean isUpdateMode() {
        return updateMode;
    }

    public ThingResponse getOriginal() {
        return original;
    }

    public String getName() {
        return txtName.getValue().trim();
    }

    public String getDescription() {
        return txtDescription.getValue();
    }

    public JsonNode getConfiguration() {
        return asJsonNode(txtConfiguration.getValue());
    }

    @Override
    protected void setUpFormFields() {
        txtUuid.setLabel("Uuid");
        txtUuid.setEnabled(false);
        txtName.setLabel("Name");
        txtDescription.setLabel("Description");
        txtConfiguration.setLabel("Configuration");
        txtConfiguration.setClassName("content-code");

        if (updateMode) {
            txtUuid.setValue(orEmpty(original.getThingUuid()));
            txtName.setValue(orEmpty(original.getName()));
            txtDescription.setValue(orEmpty(original.getDescription()));
            txtConfiguration.setValue(FormUtils.asString(original.getConfiguration()));
        } else {
            txtUuid.setValue("");
            txtName.setValue("");
            txtDescription.setValue("");
            txtConfiguration.setValue("");
        }
    }

    @Override
    protected FormValidator constructFormValidator() {
        return new FormValidator()
                .notEmpty(txtName)
                .validJson(txtConfiguration);
    }

    private FormLayout constructForm() {
        FormLayout form = new FormLayout();
        if (updateMode) {
            form.add(txtUuid, txtName, txtDescription, txtConfiguration);
        } else {
            form.add(txtName, txtDescription, txtConfiguration);
        }
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        return form;
    }
}
