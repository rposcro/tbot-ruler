package com.tbot.ruler.console.views.routes.actuators;

import com.fasterxml.jackson.databind.JsonNode;
import com.tbot.ruler.console.views.components.AbstractEditDialog;
import com.tbot.ruler.console.views.components.handlers.EditDialogSubmittedHandler;
import com.tbot.ruler.console.views.validation.FormValidator;
import com.tbot.ruler.controller.admin.payload.ActuatorResponse;
import com.tbot.ruler.controller.admin.payload.PluginResponse;
import com.tbot.ruler.controller.admin.payload.ThingResponse;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Builder;
import lombok.NonNull;

import java.util.Collections;
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
    private final Select<String> selReference = new Select<>();
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
        return selReference.getValue().trim();
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
        txtDescription.setLabel("Description");
        txtConfiguration.setLabel("Configuration");
        txtConfiguration.setClassName("content-code");

        selReference.setLabel("Reference");

        selPlugin.setLabel("Plugin");
        selPlugin.setItems(plugins);
        selPlugin.setEnabled(!updateMode);
        selPlugin.setItemLabelGenerator(plugin -> String.format("%s (%s)", plugin.getName(), plugin.getPluginUuid()));
        selPlugin.addValueChangeListener(this::pluginSelectionChanged);

        selThing.setLabel("Thing");
        selThing.setItems(things);
        selThing.setEnabled(true);
        selThing.setItemLabelGenerator(thing -> String.format("%s (%s)", thing.getName(), thing.getThingUuid()));

        if (updateMode) {
            txtUuid.setValue(orEmpty(original.getActuatorUuid()));
            txtName.setValue(orEmpty(original.getName()));
            txtDescription.setValue(orEmpty(original.getDescription()));
            txtConfiguration.setValue(asString(original.getConfiguration()));
            PluginResponse selectedPlugin = anyMatching(plugins, plugin -> plugin.getPluginUuid().equals(original.getPluginUuid()));
            selPlugin.setValue(selectedPlugin);
            selThing.setValue(anyMatching(things, thing -> thing.getThingUuid().equals(original.getThingUuid())));
            setReferenceItems(selectedPlugin);
            selReference.setValue(orEmpty(original.getReference()));
        } else {
            txtUuid.setValue("");
            txtName.setValue("");
            txtDescription.setValue("");
            txtConfiguration.setValue("");
            selPlugin.setValue(null);
            selThing.setValue(null);
            setReferenceItems(null);
        }
    }

    @Override
    protected FormValidator constructFormValidator() {
        return new FormValidator()
                .notEmpty(txtName)
                .notEmpty(selReference)
                .notEmpty(selPlugin)
                .notEmpty(selThing)
                .validJson(txtConfiguration);
    }

    private FormLayout constructForm() {
        FormLayout form = new FormLayout();
        if (updateMode) {
            form.add(txtUuid, txtName, selPlugin, selReference, selThing, txtDescription, txtConfiguration);
        } else {
            form.add(txtName, selPlugin, selReference, selThing, txtDescription, txtConfiguration);
        }
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        return form;
    }

    private void pluginSelectionChanged(HasValue.ValueChangeEvent<PluginResponse> event) {
        PluginResponse selectedPlugin = event.getValue();
        setReferenceItems(selectedPlugin);
    }

    private void setReferenceItems(PluginResponse plugin) {
        if (plugin == null) {
            selReference.setItems(Collections.emptyList());
            selReference.setEnabled(false);
        } else {
            selReference.setItems(plugin.getSupportedActuatorReferences());
            selReference.setEnabled(true);
        }
    }
}
