package com.tbot.ruler.console.views.routes.dumps;

import com.tbot.ruler.console.components.UrlProvider;
import com.tbot.ruler.console.views.TBotRulerConsoleView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Route(value = "service", layout = TBotRulerConsoleView.class)
@PageTitle("TBot Ruler Console | Actions Dashboard")
public class DumpsDashboard extends VerticalLayout {

    private final UrlProvider urlProvider;

    @Autowired
    public DumpsDashboard(UrlProvider urlProvider) {
        this.urlProvider = urlProvider;

        setSizeFull();
        add(constructContent());
    }

    private Component constructContent() {
        SideNavItem sniDownloadZip = new SideNavItem("Download Zip", urlProvider.getDownloadZipUrl(), VaadinIcon.CLOUD_DOWNLOAD_O.create());
        sniDownloadZip.setRouterIgnore(true);
        sniDownloadZip.setTarget("_blank");
        sniDownloadZip.setOpenInNewBrowserTab(false);

        SideNav dumpNav = new SideNav();
        dumpNav.addItem(sniDownloadZip);

        VerticalLayout content = new VerticalLayout();
        content.add(dumpNav);
        return content;
    }
}
