/*
 * Copyright (c) 2022. Kaiserpfalz EDV-Service, Roland T. Lichti
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.kaiserpfalzedv.commons.vaadin.mainlayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.RouterLink;
import com.youthlin.avatar.Gravatar;
import de.kaiserpfalzedv.commons.security.PersonList;
import de.kaiserpfalzedv.commons.vaadin.TraceNavigation;
import de.kaiserpfalzedv.commons.vaadin.about.AboutView;
import de.kaiserpfalzedv.commons.vaadin.about.PolicyView;
import de.kaiserpfalzedv.commons.vaadin.about.TermsOfServiceView;
import io.quarkus.oidc.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.LoggerFactory;

import java.security.Principal;

/**
 * The main view contains a button and a click listener.
 */
@Slf4j
public class MainLayout extends AppLayout implements TraceNavigation {

    private final Tabs menu;
    private H1 viewTitle;

    private Avatar avatar = new Avatar("anonymous");


    public MainLayout() {
        log.trace("Creating Main Layout. object={}", this);

        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeader());

        menu = createMenu();
        addToDrawer(createDrawer(menu));
    }


    private Component createHeader() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setId("header");
        layout.getThemeList().set("dark", true);
        layout.setWidthFull();
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        layout.add(new DrawerToggle());

        viewTitle = new H1();
        layout.add(viewTitle);

        layout.add(avatar);

        return layout;
    }

    private Component createDrawer(final Tabs menu) {
        log.debug("Creating drawer. session='{}'", UI.getCurrent().getSession().getSession().getId());

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.getThemeList().set("spacing-s", true);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);

        HorizontalLayout logoLayout = new HorizontalLayout();
        logoLayout.setId("logo");
        logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        logoLayout.add(new Label("Logo"));
        logoLayout.add(new H1("Anwendung"));

        layout.add(logoLayout, menu);

        return layout;
    }

    private Tabs createMenu() {
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        tabs.setId("tabs");
        tabs.add(createMenuItems());

        return tabs;
    }

    private Component[] createMenuItems() {
        return new Tab[]{
                createTab("Dash Board", DashboardView.class),
                createTab("About", AboutView.class),
                createTab("Personen", PersonList.class),
                createTab("Terms of Service", TermsOfServiceView.class),
                createTab("Data Protection Policy", PolicyView.class),
                createTab("Logout", "/logout")
        };
    }

    private static Tab createTab(String text, String url) {
        final Tab tab = new Tab();

        tab.add(new Anchor(url, text));

        return tab;
    }

    private static Tab createTab(String text, Class<? extends Component> navigationTarget) {
        final Tab tab = new Tab();

        tab.add(new RouterLink(text, navigationTarget));
        ComponentUtil.setData(tab, Class.class, navigationTarget);

        return tab;
    }

    private Principal user = null;

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        UI ui = getUI().orElse(null);
        LoggerFactory.getLogger(getClass()).trace("Navigation finished. session='{}', ui='{}', component='{}', principal={}, user={}",
                ui.getSession().getSession().getId(),
                ui.getId(),
                this.getClass().getSimpleName(),
                ((JsonWebToken) ui.getSession().getAttribute(Principal.class)).getSubject(),
                ((UserInfo) ui.getSession().getSession().getAttribute("userInfo")).get("sub")
        );

        setUser(ui.getSession().getAttribute(Principal.class));

    }

    private void setUser(Principal user) {
        this.user = user;

        if (user != null) {
            avatar.setName(user.getName());
            avatar.setImage(Gravatar.withEmail(user.getName()).size(80).getUrl());
        } else {
            avatar.setName("anonymous");
        }
    }
}
