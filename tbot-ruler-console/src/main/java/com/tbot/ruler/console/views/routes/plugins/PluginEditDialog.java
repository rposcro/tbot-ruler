package com.tbot.ruler.console.views.routes.plugins;

import com.tbot.ruler.console.utils.FormUtils;
import com.tbot.ruler.console.views.components.AbstractEditDialog;
import com.tbot.ruler.console.views.components.handlers.EditDialogSubmittedHandler;
import com.tbot.ruler.console.views.validation.FormValidator;
import com.tbot.ruler.controller.admin.payload.PluginResponse;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Builder;
import lombok.NonNull;

import java.util.List;

import static com.tbot.ruler.console.utils.FormUtils.orEmpty;

public class PluginEditDialog extends AbstractEditDialog<PluginEditDialog> {

    private final TextField txtUuid = new TextField();
    private final TextField txtName = new TextField();
    private final Select<String> selFactory = new Select<>();
    private final TextArea txtConfiguration = new TextArea();

    private final List<String> factories;
    private final PluginResponse original;

    @Builder
    public PluginEditDialog(
            @NonNull Boolean updateMode,
            @NonNull List<String> factories,
            @NonNull EditDialogSubmittedHandler<PluginEditDialog> submitHandler,
            PluginResponse original
    ) {
        super(updateMode, submitHandler);

        if (updateMode && original == null) {
            throw new NullPointerException("Original entity cannot be null in update mode!");
        }

        this.factories = factories;
        this.original = original;

        setHeaderTitle(updateMode ? "Edit Plugin" : "Create Plugin");
        setModal(true);
        setWidth("60%");

        setUpFormFields();
        add(constructForm());
    }

    public boolean isUpdateMode() {
        return updateMode;
    }

    public String getPluginName() {
        return txtName.getValue().trim();
    }

    public String getFactory() {
        return selFactory.getValue();
    }

    public String getPluginConfiguration() {
        return txtConfiguration.getValue().trim().replaceAll("\n", " ");
    }

    @Override
    protected void setUpFormFields() {
        txtUuid.setLabel("Uuid");
        txtUuid.setEnabled(false);

        txtName.setLabel("Name");

        selFactory.setLabel("Factory Class");
        selFactory.setItems(factories);
        selFactory.setEnabled(!updateMode);

        txtConfiguration.setLabel("Configuration");
        txtConfiguration.setClassName("content-code");

        if (original != null) {
            txtUuid.setValue(orEmpty(original.getPluginUuid()));
            txtName.setValue(orEmpty(original.getName()));
            selFactory.setValue(original.getFactoryClass());
            txtConfiguration.setValue(FormUtils.asString(original.getConfiguration()));
        } else {
            txtUuid.setValue("");
            txtName.setValue("");
            selFactory.setValue(null);
            txtConfiguration.setValue("");
        }
    }

    @Override
    protected FormValidator constructFormValidator() {
        return new FormValidator()
                .notEmpty(txtName)
                .notEmpty(selFactory)
                .validJson(txtConfiguration);
    }

    private FormLayout constructForm() {
        FormLayout form = new FormLayout();
        if (updateMode) {
            form.add(txtUuid, txtName, selFactory, txtConfiguration);
        } else {
            form.add(txtName, selFactory, txtConfiguration);
        }
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        return form;
    }
}
