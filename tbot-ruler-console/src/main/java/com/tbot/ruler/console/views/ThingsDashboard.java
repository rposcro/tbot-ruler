package com.tbot.ruler.console.views;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "things", layout = TBotRulerConsoleView.class)
@PageTitle("TBot Ruler Console | Things Dashboard")
public class ThingsDashboard extends VerticalLayout {

    public ThingsDashboard() {
        setSizeFull();

        add(setUpToolbar());
        add(setUpPanel());
    }

    private HorizontalLayout setUpToolbar() {
        HorizontalLayout toolbar = new HorizontalLayout();
        return toolbar;
    }

    private VerticalLayout setUpPanel() {
        VerticalLayout listPanel = new VerticalLayout();
        return listPanel;
    }
}
