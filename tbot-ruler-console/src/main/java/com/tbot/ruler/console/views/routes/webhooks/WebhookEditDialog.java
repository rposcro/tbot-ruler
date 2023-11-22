package com.tbot.ruler.console.views.routes.webhooks;

import com.tbot.ruler.console.views.components.AbstractEditDialog;
import com.tbot.ruler.console.views.validation.FormValidator;
import com.tbot.ruler.controller.admin.payload.WebhookResponse;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Builder;
import lombok.NonNull;

import java.util.List;
import java.util.function.Consumer;

import static com.tbot.ruler.console.utils.FormUtils.orEmpty;

public class WebhookEditDialog extends AbstractEditDialog<WebhookEditDialog> {

    private final TextField txtUuid = new TextField();
    private final TextField txtName = new TextField();
    private final TextArea txtDescription = new TextArea();
    private final Select<String> selOwner = new Select<>();

    private final List<String> owners;
    private final WebhookResponse original;

    @Builder
    public WebhookEditDialog(
            @NonNull Boolean updateMode,
            @NonNull List<String> owners,
            @NonNull Consumer<WebhookEditDialog> submitHandler,
            WebhookResponse original
    ) {
        super(updateMode, submitHandler);

        if (updateMode && original == null) {
            throw new NullPointerException("Original entity cannot be null in update mode!");
        }

        this.owners = owners;
        this.original = original;

        setHeaderTitle(updateMode ? "Edit Webhook" : "Create Webhook");
        setModal(true);
        setWidth("60%");

        setUpFormFields();
        add(constructForm());
    }

    public boolean isUpdateMode() {
        return updateMode;
    }

    public WebhookResponse getOriginal() {
        return this.original;
    }

    public String getWebhookOwner() {
        return selOwner.getValue();
    }

    public String getWebhookName() {
        return txtName.getValue().trim();
    }

    public String getWebhookDescription() {
        return txtDescription.getValue();
    }

    @Override
    protected void setUpFormFields() {
        txtUuid.setLabel("Uuid");
        txtUuid.setEnabled(false);
        txtName.setLabel("Name");
        txtDescription.setLabel("Description");

        selOwner.setLabel("Owner");
        selOwner.setItems(owners);
        selOwner.setEnabled(!updateMode);

        if (updateMode) {
            txtUuid.setValue(orEmpty(original.getWebhookUuid()));
            txtName.setValue(orEmpty(original.getName()));
            txtDescription.setValue(orEmpty(original.getDescription()));
            selOwner.setValue(original.getOwner());
        } else {
            txtUuid.setValue("");
            txtName.setValue("");
            txtDescription.setValue("");
            selOwner.setValue(null);
        }
    }

    @Override
    protected FormValidator constructFormValidator() {
        return new FormValidator()
                .notEmpty(txtName)
                .notEmpty(selOwner);
    }

    private FormLayout constructForm() {
        FormLayout form = new FormLayout();
        if (updateMode) {
            form.add(txtUuid, txtName, selOwner, txtDescription);
        } else {
            form.add(txtName, selOwner, txtDescription);
        }
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));
        return form;
    }
}
