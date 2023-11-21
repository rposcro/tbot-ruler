package com.tbot.ruler.console.views;

import com.tbot.ruler.console.views.validation.FormValidator;
import com.tbot.ruler.console.views.validation.FormValidationResult;
import com.tbot.ruler.console.views.validation.FieldValidator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;

import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class AbstractEditDialog<TDialog extends AbstractEditDialog> extends Dialog {

    protected final PopupNotifier popupNotifier = new PopupNotifier();
    protected final boolean updateMode;

    private FormValidator formValidator;

    private final Button btnSubmit;
    private final Button btnClose;
    private final Button btnReset;

    protected AbstractEditDialog(boolean updateMode, Consumer<TDialog> submitHandler) {
        this.updateMode = updateMode;
        this.btnSubmit = constructSubmitButton(updateMode, submitHandler);
        this.btnClose = constructCloseButton();
        this.btnReset = constructResetButton();
        setUpFooter();
    }

    protected abstract void setUpFormFields();
    protected FormValidator constructFormValidator() {
        return new FormValidator();
    }

    protected boolean validateForm() {
        formValidator = formValidator == null ? constructFormValidator() : formValidator;
        FormValidationResult result = formValidator.validate();

        if (!result.validationPassed()) {
            popupNotifier.notifyWarning("Please address form issues");
        }
        return result.validationPassed();
    }

    private void setUpFooter() {
        getFooter().add(btnSubmit, btnClose, btnReset);
    }

    private Button constructSubmitButton(boolean updateMode, Consumer<TDialog> clickHandler) {
        Button button = new Button(updateMode ? "Update" : "Create");
        button.getStyle().set("margin-left", "auto");
        button.addClickListener(event -> {
            if (validateForm()) {
                clickHandler.accept((TDialog) this);
            }
        });
        return button;
    }

    private Button constructCloseButton() {
        Button button = new Button("Close");
        button.addClickListener(event -> this.close());
        return button;
    }

    private Button constructResetButton() {
        Button button = new Button("Reset");
        button.getStyle().set("margin-right", "auto");
        button.addClickListener(event -> setUpFormFields());
        return button;
    }
}
