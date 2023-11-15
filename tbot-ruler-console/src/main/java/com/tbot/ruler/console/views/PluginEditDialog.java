package com.tbot.ruler.console.views;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Builder;
import lombok.NonNull;

import java.util.List;
import java.util.function.Consumer;

public class PluginEditDialog extends Dialog {

    private ObjectMapper objectMapper = new ObjectMapper();

    private final TextField txtUuid = new TextField();
    private final TextField txtName = new TextField();
    private final Select<String> selFactory = new Select<>();
    private final TextArea txtConfiguration = new TextArea();

    private final Button btnSubmit;
    private final Button btnClose;
    private final Button btnReset;

    private final boolean updateMode;
    private final List<String> factories;
    private final String originalUuid;
    private final String originalFactory;
    private final String originalName;
    private final String originalConfiguration;

    @Builder
    public PluginEditDialog(
            @NonNull Boolean updateMode,
            @NonNull List<String> factories,
            @NonNull String uuid,
            @NonNull Consumer<PluginEditDialog> submitHandler,
            String factory,
            String name,
            String configuration) {
        this.updateMode = updateMode;
        this.factories = factories;
        this.originalUuid = uuid;
        this.originalName = name;
        this.originalFactory = factory;
        this.originalConfiguration = configuration;

        this.btnSubmit = constructSubmitButton(submitHandler);
        this.btnClose = constructCloseButton();
        this.btnReset = constructResetButton();

        setHeaderTitle(updateMode ? "Edit Plugin" : "Create Plugin");
        setModal(true);
        setWidth("60%");

        setUpFooter();
        setUpFormFields();

        add(constructForm());
    }

    public String getPluginName() {
        return txtName.getValue().trim();
    }

    public String getPluginConfiguration() {
        return txtConfiguration.getValue().trim().replaceAll("\n", " ");
    }

    private FormLayout constructForm() {
        FormLayout form = new FormLayout();
        form.add(txtUuid, txtName, selFactory, txtConfiguration);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        return form;
    }

    private Button constructSubmitButton(Consumer<PluginEditDialog> clickHandler) {
        Button button = new Button(updateMode ? "Update" : "Create");
        button.getStyle().set("margin-left", "auto");
        button.addClickListener(event -> {
            if (validateFields()) {
                clickHandler.accept(this);
            }
        });
        return button;
    }

    private Button constructCloseButton() {
        Button button = new Button("Close");
        button.addClickListener(event -> PluginEditDialog.this.close());
        return button;
    }

    private Button constructResetButton() {
        Button button = new Button("Reset");
        button.getStyle().set("margin-right", "auto");
        button.addClickListener(event -> setUpFormFields());
        return button;
    }

    private void setUpFormFields() {
        txtUuid.setLabel("Uuid");
        txtUuid.setValue(originalUuid);
        txtUuid.setEnabled(false);

        txtName.setLabel("Name");
        txtName.setValue(originalName);

        selFactory.setLabel("Factory Class");
        selFactory.setItems(factories);
        selFactory.setValue(originalFactory);
        selFactory.setEnabled(!updateMode);

        txtConfiguration.setLabel("Configuration");
        txtConfiguration.setValue(originalConfiguration);
    }

    private void setUpFooter() {
        getFooter().add(btnSubmit, btnClose, btnReset);
    }

    private boolean validateFields() {
        try {
            objectMapper.readTree(txtConfiguration.getValue());
        } catch(Exception e) {
            return false;
        }
        return !txtName.getValue().trim().isEmpty()
                && !selFactory.getValue().isEmpty();
    }
}
