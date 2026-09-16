package com.tbot.ruler.console.views;

import com.tbot.ruler.console.views.routes.actuators.ActuatorsDashboard;
import com.tbot.ruler.console.views.routes.bindings.BindingsDashboard;
import com.tbot.ruler.console.views.routes.plugins.PluginsDashboard;
import com.tbot.ruler.console.views.routes.dumps.DumpsDashboard;
import com.tbot.ruler.console.views.routes.stencils.StencilsDashboard;
import com.tbot.ruler.console.views.routes.things.ThingsDashboard;
import com.tbot.ruler.console.views.routes.webhooks.WebhooksDashboard;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
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

    private Component setUpDrawer() {
        SideNav subjectsNav = new SideNav("Subjects");
        subjectsNav.setWidthFull();
        subjectsNav.setCollapsible(true);
        subjectsNav.setExpanded(true);
        subjectsNav.addItem(new SideNavItem("Plugins", PluginsDashboard.class, VaadinIcon.PLUG.create()));
        subjectsNav.addItem(new SideNavItem("Things", ThingsDashboard.class, VaadinIcon.CUBES.create()));
        subjectsNav.addItem(new SideNavItem("Actuators", ActuatorsDashboard.class, VaadinIcon.PUZZLE_PIECE.create()));
        subjectsNav.addItem(new SideNavItem("Webhooks", WebhooksDashboard.class, VaadinIcon.CONNECT_O.create()));
        subjectsNav.addItem(new SideNavItem("Bindings", BindingsDashboard.class, VaadinIcon.PAPERCLIP.create()));
        subjectsNav.addItem(new SideNavItem("Stencils", StencilsDashboard.class, VaadinIcon.LAYOUT.create()));

        SideNav serviceNav = new SideNav("Service");
        serviceNav.setWidthFull();
        serviceNav.setCollapsible(true);
        serviceNav.setExpanded(true);
        serviceNav.addItem(new SideNavItem("Configuration Dump", DumpsDashboard.class, VaadinIcon.FILE_PROCESS.create()));

        VerticalLayout drawer = new VerticalLayout(
            subjectsNav,
            serviceNav
        );
        drawer.setWidthFull();
        return drawer;
    }
}
