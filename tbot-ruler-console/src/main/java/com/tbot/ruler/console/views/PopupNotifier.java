package com.tbot.ruler.console.views;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.spring.annotation.SpringComponent;

@SpringComponent
public class PopupNotifier {

    public void notifyError(String message) {
        notify(message, NotificationVariant.LUMO_ERROR);
    }

    public void notifyWarning(String message) {
        notify(message, NotificationVariant.LUMO_WARNING);
    }

    public void notifyInfo(String message) {
        notify(message, NotificationVariant.LUMO_PRIMARY);
    }

    public void notify(String message, NotificationVariant variant) {
        Notification notification = new Notification(message);
        notification.addThemeVariants(variant);
        notification.setDuration(2500);
        notification.open();
    }
}
