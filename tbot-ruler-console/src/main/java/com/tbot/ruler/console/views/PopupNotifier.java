package com.tbot.ruler.console.views;

import com.tbot.ruler.console.views.components.PromptDialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

import static java.lang.String.format;

public class PopupNotifier {

    public static void promptError(String... promptLines) {
        PromptDialog.builder()
                .title("Error")
                .prompt(promptLines)
                .action("Ok", PromptDialog::close)
                .build()
                .open();
    }

    public static void notifyError(String message) {
        notify(message, NotificationVariant.LUMO_ERROR);
    }

    public static void notifyWarning(String message) {
        notify(message, NotificationVariant.LUMO_WARNING);
    }

    public static void notifyInfo(String message) {
        notify(message, NotificationVariant.LUMO_PRIMARY);
    }

    public static void notifyInfo(String message, Object... arguments) {
        notify(format(message, arguments), NotificationVariant.LUMO_PRIMARY);
    }

    public static void notify(String message, NotificationVariant variant) {
        Notification notification = new Notification(message);
        notification.addThemeVariants(variant);
        notification.setDuration(3000);
        notification.open();
    }
}
