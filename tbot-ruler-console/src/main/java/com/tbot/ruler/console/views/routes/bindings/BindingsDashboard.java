package com.tbot.ruler.console.views.routes.bindings;

import com.tbot.ruler.console.accessors.BindingsModelAccessor;
import com.tbot.ruler.console.exceptions.ClientCommunicationException;
import com.tbot.ruler.console.views.TBotRulerConsoleView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "bindings", layout = TBotRulerConsoleView.class)
@PageTitle("TBot Ruler Console | Bindings Dashboard")
public class BindingsDashboard extends VerticalLayout {

    private final BindingsModelAccessor dataSupport;
    private final BindingEditSupport editSupport;

//    private final EntityPropertiesPanel<BindingModel> actuatorPanel;
    private final BindingsGrid bindingsGrid;

    @Autowired
    public BindingsDashboard(BindingsModelAccessor dataSupport, BindingEditSupport editSupport) {
        this.dataSupport = dataSupport;
        this.editSupport = editSupport;
        this.bindingsGrid = constructGrid();
//        this.actuatorPanel = EntityPropertiesPanel.<ActuatorModel>builder()
//                .beanType(ActuatorModel.class)
//                .editHandler(() -> editSupport.launchActuatorEdit(
//                        bindingsGrid.asSingleSelect().getValue(), this::handleUpdateActuator))
//                .properties(new String[] { "name", "reference", "actuatorUuid", "pluginName", "thingName", "description", "configuration"} )
//                .build();

        setSizeFull();
        add(constructToolbar());
        add(constructContent());
    }

    private HorizontalLayout constructToolbar() {
        Button createButton = new Button("New Binding");
        createButton.addClickListener(event -> {});

        HorizontalLayout toolbar = new HorizontalLayout(createButton);
        toolbar.setAlignItems(Alignment.START);
        toolbar.setWidthFull();
        return toolbar;
    }

    private HorizontalLayout constructContent() {
        HorizontalLayout content = new HorizontalLayout();

        try {
            bindingsGrid.setItems(dataSupport.getAllBindingsModels());
            content.add(bindingsGrid);
//            actuatorPanel.getStyle().set("margin-top", "0px");
//            content.add(bindingsGrid, actuatorPanel);
//            content.setFlexGrow(3, bindingsGrid);
//            content.setFlexGrow(1, actuatorPanel);
        } catch(ClientCommunicationException e) {
            content.add(new VerticalLayout(
                    new Span("Error loading bindings ..."),
                    new Span(e.getMessage())));
        }

        content.setSizeFull();
        content.setAlignItems(Alignment.START);
        return content;
    }

    private BindingsGrid constructGrid() {
        BindingsGrid grid = new BindingsGrid();
        return grid;
    }
}
