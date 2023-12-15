package com.tbot.ruler.console.views.routes.actuators;

import com.fasterxml.jackson.databind.JsonNode;
import com.tbot.ruler.console.views.components.AbstractEditDialog;
import com.tbot.ruler.console.views.components.handlers.EditDialogSubmittedHandler;
import com.tbot.ruler.console.views.validation.FormValidator;
import com.tbot.ruler.controller.admin.payload.ActuatorResponse;
import com.tbot.ruler.controller.admin.payload.PluginResponse;
import com.tbot.ruler.controller.admin.payload.ThingResponse;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Builder;
import lombok.NonNull;

import java.util.List;

import static com.tbot.ruler.console.utils.FormUtils.asJsonNode;
import static com.tbot.ruler.console.utils.FormUtils.asString;
import static com.tbot.ruler.console.utils.FormUtils.orEmpty;
import static com.tbot.ruler.console.utils.StreamUtils.anyMatching;

public class ActuatorEditDialog extends AbstractEditDialog<ActuatorEditDialog> {

    private final TextField txtUuid = new TextField();
    private final TextField txtName = new TextField();
    private final Select<PluginResponse> selPlugin = new Select<>();
    private final Select<ThingResponse> selThing = new Select<>();
    private final TextField txtReference = new TextField();
    private final TextField txtDescription = new TextField();
    private final TextArea txtConfiguration = new TextArea();

    private final boolean updateMode;
    private final List<PluginResponse> plugins;
    private final List<ThingResponse> things;
    private final ActuatorResponse original;

    @Builder
    public ActuatorEditDialog(
            @NonNull Boolean updateMode,
            @NonNull List<PluginResponse> plugins,
            @NonNull List<ThingResponse> things,
            @NonNull EditDialogSubmittedHandler<ActuatorEditDialog> submitHandler,
            ActuatorResponse original) {
        super(updateMode, submitHandler);

        if (updateMode && original == null) {
            throw new NullPointerException("Original entity cannot be null in update mode!");
        }

        this.updateMode = updateMode;
        this.plugins = plugins;
        this.things = things;
        this.original = original;

        setHeaderTitle(updateMode ? "Edit Actuator" : "Create Actuator");
        setModal(true);
        setWidth("60%");

        setUpFormFields();
        add(constructForm());
    }

    public boolean isUpdateMode() {
        return updateMode;
    }

    public ActuatorResponse getOriginal() {
        return original;
    }

    public String getName() {
        return txtName.getValue().trim();
    }

    public String getDescription() {
        return txtDescription.getValue().trim();
    }

    public String getReference() {
        return txtReference.getValue().trim();
    }

    public JsonNode getConfiguration() {
        return asJsonNode(txtConfiguration.getValue());
    }

    public PluginResponse getPlugin() {
        return selPlugin.getValue();
    }

    public ThingResponse getThing() {
        return selThing.getValue();
    }

    @Override
    protected void setUpFormFields() {
        txtUuid.setLabel("Uuid");
        txtUuid.setEnabled(false);
        txtName.setLabel("Name");
        txtReference.setLabel("Reference");
        txtDescription.setLabel("Description");
        txtConfiguration.setLabel("Configuration");
        txtConfiguration.setClassName("content-code");

        selPlugin.setLabel("Plugin");
        selPlugin.setItems(plugins);
        selPlugin.setEnabled(!updateMode);
        selPlugin.setItemLabelGenerator(plugin -> String.format("%s (%s)", plugin.getName(), plugin.getPluginUuid()));

        selThing.setLabel("Thing");
        selThing.setItems(things);
        selThing.setEnabled(true);
        selThing.setItemLabelGenerator(thing -> String.format("%s (%s)", thing.getName(), thing.getThingUuid()));

        if (updateMode) {
            txtUuid.setValue(orEmpty(original.getActuatorUuid()));
            txtName.setValue(orEmpty(original.getName()));
            txtReference.setValue(orEmpty(original.getReference()));
            txtDescription.setValue(orEmpty(original.getDescription()));
            txtConfiguration.setValue(asString(original.getConfiguration()));
            selPlugin.setValue(anyMatching(plugins, plugin -> plugin.getPluginUuid().equals(original.getPluginUuid())));
            selThing.setValue(anyMatching(things, thing -> thing.getThingUuid().equals(original.getThingUuid())));
        } else {
            txtUuid.setValue("");
            txtName.setValue("");
            txtReference.setValue("");
            txtDescription.setValue("");
            txtConfiguration.setValue("");
            selPlugin.setValue(null);
            selThing.setValue(null);
        }
    }

    @Override
    protected FormValidator constructFormValidator() {
        return new FormValidator()
                .notEmpty(txtName)
                .notEmpty(txtReference)
                .notEmpty(selPlugin)
                .notEmpty(selThing)
                .validJson(txtConfiguration);
    }

    private FormLayout constructForm() {
        FormLayout form = new FormLayout();
        if (updateMode) {
            form.add(txtUuid, txtName, txtReference, selPlugin, selThing, txtDescription, txtConfiguration);
        } else {
            form.add(txtName, txtReference, selPlugin, selThing, txtDescription, txtConfiguration);
        }
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        return form;
    }
}
