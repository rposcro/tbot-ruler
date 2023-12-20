package com.tbot.ruler.console.views.routes.bindings;

import com.tbot.ruler.console.views.components.EntityFilterableGrid;
import com.tbot.ruler.console.views.components.handlers.DialogActionHandler;
import com.tbot.ruler.controller.admin.payload.ActuatorResponse;
import com.tbot.ruler.controller.admin.payload.BindingResponse;
import com.tbot.ruler.controller.admin.payload.WebhookResponse;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.Builder;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

public class BindingsCreateDialog extends Dialog {

    private final EntityFilterableGrid<BindingParticipantModel> gridSenders;
    private final EntityFilterableGrid<BindingParticipantModel> gridReceivers;
    private final Button assignButton;

    private final List<BindingParticipantModel> senders;
    private final List<BindingParticipantModel> receivers;
    private final Map<String, Set<String>> sendersToReceiversMap;
    private final Map<String, Set<String>> receiversToSendersMap;

    @Builder
    public BindingsCreateDialog(
            @NonNull List<ActuatorResponse> actuators,
            @NonNull List<WebhookResponse> webhooks,
            @NonNull List<BindingResponse> existingBindings,
            @NonNull DialogActionHandler<BindingsCreateDialog> bindHandler,
            @NonNull DialogActionHandler<BindingsCreateDialog> finishHandler) {
        this.senders = gatherSendersList(actuators, webhooks);
        this.receivers = gatherRecipientsList(actuators);
        this.sendersToReceiversMap = existingBindings.stream().collect(
                Collectors.groupingBy(BindingResponse::getSenderUuid, mapping(BindingResponse::getReceiverUuid, toSet())));
        this.receiversToSendersMap = existingBindings.stream().collect(
                Collectors.groupingBy(BindingResponse::getReceiverUuid, mapping(BindingResponse::getSenderUuid, toSet())));

        this.gridSenders = constructSendersGrid(senders);
        this.gridReceivers = constructReceiversGrid(receivers);
        this.assignButton = constructAssignButton(bindHandler);

        this.addDialogCloseActionListener(event -> finishHandler.execute(this));

        setHeaderTitle("Create Binding");
        setModal(true);
        setResizable(true);
        setDraggable(true);
        setWidth("80%");
        setHeight("90%");

        add(constructContent());
        getFooter().add(constructFinishButton(finishHandler));
    }

    public void addBinding(String senderUuid, String receiverUuid) {
        sendersToReceiversMap.computeIfAbsent(senderUuid, uuid -> new HashSet<>()).add(receiverUuid);
        receiversToSendersMap.computeIfAbsent(receiverUuid, uuid -> new HashSet<>()).add(senderUuid);
    }

    public BindingParticipantModel getSelectedSender() {
        return gridSenders.asSingleSelect().getValue();
    }

    public BindingParticipantModel getSelectedReceiver() {
        return gridReceivers.asSingleSelect().getValue();
    }

    private List<BindingParticipantModel> gatherSendersList(List<ActuatorResponse> actuators, List<WebhookResponse> webhooks) {
        List<BindingParticipantModel> list = new ArrayList<>(actuators.size() + webhooks.size());
        actuators.stream()
                .map(actuator -> new BindingParticipantModel(actuator.getName(), actuator.getActuatorUuid(), "actuator"))
                .forEach(list::add);
        webhooks.stream()
                .map(webhook -> new BindingParticipantModel(webhook.getName(), webhook.getWebhookUuid(), "webhook"))
                .forEach(list::add);
        return list;
    }

    private List<BindingParticipantModel> gatherRecipientsList(List<ActuatorResponse> actuators) {
        return actuators.stream()
                .map(actuator -> new BindingParticipantModel(actuator.getName(), actuator.getActuatorUuid(), "actuator"))
                .toList();
    }

    private Button constructAssignButton(DialogActionHandler<BindingsCreateDialog> bindHandler) {
        return new Button(VaadinIcon.ANGLE_DOUBLE_DOWN.create(), event -> {
            bindHandler.execute(this);
            gridSenders.asSingleSelect().clear();
            gridReceivers.asSingleSelect().clear();
        });
    }

    private Button constructFinishButton(DialogActionHandler<BindingsCreateDialog> finishHandler) {
        Button btnFinish = new Button("Finish", event -> finishHandler.execute(this));
        btnFinish.getStyle().set("margin-left", "auto");
        btnFinish.getStyle().set("margin-right", "auto");
        return btnFinish;
    }

    private Component constructContent() {
        VerticalLayout layout = new VerticalLayout(
                gridSenders,
                new Div(assignButton),
                gridReceivers);
        layout.setSizeFull();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        return layout;
    }

    private EntityFilterableGrid<BindingParticipantModel> constructSendersGrid(List<BindingParticipantModel> senders) {
        EntityFilterableGrid<BindingParticipantModel> grid = new EntityFilterableGrid<>(
                BindingParticipantModel.class, new String[] { "name", "uuid", "type" });
        grid.setItems(senders);
        grid.asSingleSelect().addValueChangeListener(item -> handleSelectionChange());
        return grid;
    }

    private EntityFilterableGrid<BindingParticipantModel> constructReceiversGrid(List<BindingParticipantModel> receivers) {
        EntityFilterableGrid<BindingParticipantModel> grid = new EntityFilterableGrid<>(
                BindingParticipantModel.class, new String[] { "name", "uuid" });
        grid.setItems(receivers);
        grid.asSingleSelect().addValueChangeListener(item -> handleSelectionChange());
        return grid;
    }

    private void handleSelectionChange() {
        BindingParticipantModel selectedSender = gridSenders.asSingleSelect().getValue();
        BindingParticipantModel selectedReceiver = gridReceivers.asSingleSelect().getValue();
        assignButton.setEnabled(false);

        if (selectedSender != null && selectedReceiver != null) {
            assignButton.setEnabled(true);
        } else if (selectedSender != null) {
            Set<String> binded = sendersToReceiversMap.getOrDefault(selectedSender.getUuid(), Collections.emptySet());
            gridReceivers.setItems(receivers.stream()
                    .filter(receiver -> !binded.contains(receiver.getUuid()) && !receiver.getUuid().equals(selectedSender.getUuid()))
                    .toList());
        } else if (selectedReceiver != null) {
            Set<String> binded = receiversToSendersMap.getOrDefault(selectedReceiver.getUuid(), Collections.emptySet());
            gridSenders.setItems(senders.stream()
                    .filter(sender -> !binded.contains(sender.getUuid()) && !sender.getUuid().equals(selectedReceiver.getUuid()))
                    .toList());
        } else {
            gridSenders.setItems(senders);
            gridReceivers.setItems(receivers);
        }
    }
}
