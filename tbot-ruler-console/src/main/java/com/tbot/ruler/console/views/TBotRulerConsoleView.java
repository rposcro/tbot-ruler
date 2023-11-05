package com.tbot.ruler.console.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route("")
@PageTitle("TBot Ruler Console")
public class TBotRulerConsoleView extends AppLayout {

    public TBotRulerConsoleView() {
        addToNavbar(setUpHeader());
        addToDrawer(setUpDrawer());
    }

    private HorizontalLayout setUpHeader() {
        H1 title = new H1("TBot Ruler Console");
        title.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.MEDIUM);

        HorizontalLayout theHeader = new HorizontalLayout(new DrawerToggle(), title);
        theHeader.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        theHeader.setWidthFull();
        theHeader.addClassNames(
                LumoUtility.Padding.Vertical.NONE,
                LumoUtility.Padding.Horizontal.MEDIUM);

        return theHeader;
    }

    private VerticalLayout setUpDrawer() {
        return new VerticalLayout(
                new RouterLink("Plugins", PluginsDashboard.class),
                new RouterLink("Things", ThingsDashboard.class),
                new RouterLink("Actuators", ActuatorsDashboard.class)
        );
    }
}
