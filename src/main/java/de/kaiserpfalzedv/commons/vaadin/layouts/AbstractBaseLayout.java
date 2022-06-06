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

package de.kaiserpfalzedv.commons.vaadin.layouts;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarGroup;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.*;
import de.kaiserpfalzedv.commons.vaadin.EventBusRegistered;
import de.kaiserpfalzedv.commons.vaadin.profile.UserDetails;
import de.kaiserpfalzedv.commons.vaadin.security.LogoutView;
import de.kaiserpfalzedv.commons.vaadin.security.PermissionChecker;
import de.kaiserpfalzedv.commons.vaadin.security.PermissionHolding;
import de.kaiserpfalzedv.rpg.tombng.ui.app.profile.ProfileView;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import static de.kaiserpfalzedv.commons.vaadin.i18n.DefaultComponentsI18nKeys.*;

/**
 * AbstractBaseLayout -- Default base layout for a default Vaadin application.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-06-01
 */
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
@Slf4j
public abstract class AbstractBaseLayout extends AppLayout implements EventBusRegistered, PermissionHolding,
        BeforeEnterObserver, BeforeLeaveObserver {
    @ConfigProperty(name = "application.name")
    @ToString.Include
    String appName;
    @ConfigProperty(name = "application.version")
    @ToString.Include
    String appVersion;
    @ConfigProperty(name = "quarkus.oidc.logout.path", defaultValue = "/logout")
    String quarkusLogoutPage;


    @Inject
    @ToString.Include
    protected UserDetails userInfo;

    @Inject
    protected PermissionChecker permissionChecker;


    protected final HorizontalLayout header = new HorizontalLayout();
    protected final Label viewTitle = new Label(appName);

    protected final Avatar avatar = new Avatar();
    protected final MenuBar menuBar = new MenuBar();
    protected final MenuItem avatarMenuItem = menuBar.addItem(avatar);
    protected final SubMenu avatarContextMenu = avatarMenuItem.getSubMenu();
    protected final AvatarGroup loggedInUsers = new AvatarGroup();
    protected final Tabs menu = new Tabs();

    protected final RouterLink logoutLink = new RouterLink(
            getTranslation(ACTIONS_LOGOUT),
            LogoutView.class
    );


    @PostConstruct
    public void init() {
        registerToBus();

        log.trace("Creating Layout. object={}", this);

        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeader());

        createMenu();

        addToDrawer(createDrawer(menu));

    }

    @PreDestroy
    public void autoUnregisterFromBus() {
        unregisterFromBus();
    }

    protected Component createHeader() {
        header.setId("header");
        header.getThemeList().set("dark", true);
        header.setWidthFull();
        header.setSpacing(false);
        header.setAlignItems(FlexComponent.Alignment.STRETCH);

        header.add(new DrawerToggle());

        viewTitle.setWidth(70f, Unit.PERCENTAGE);
        viewTitle.setHeightFull();
        header.add(viewTitle);

        header.add(loggedInUsers);


        header.add(menuBar);
        createAvatarContextMenu();


        return header;
    }

    private void createAvatarContextMenu() {
        if (userInfo != null) {
            avatar.setName(userInfo.getName());
            avatar.setImage(userInfo.getImage());
        }
        avatarContextMenu.removeAll();
        avatarContextMenu.addItem(new RouterLink(getTranslation(VIEWS_PERSON_INFO), ProfileView.class));
        avatarContextMenu.addItem(getTranslation(ACTIONS_SETTINGS));
        avatarContextMenu.addItem(getTranslation(ACTIONS_HELP));
        avatarContextMenu.addItem(new RouterLink(getTranslation(ACTIONS_LOGOUT), LogoutView.class));
    }

    protected Component createDrawer(final Tabs menu) {
        log.debug("Creating drawer. session='{}'", UI.getCurrent().getSession().getSession().getId());

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.getThemeList().set("spacing-s", true);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);

        VerticalLayout logoLayout = new VerticalLayout();
        logoLayout.setId("logo");
        logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        logoLayout.add(new Label("Hier seht Ihr ein geiles Logo"));

        layout.add(logoLayout, menu);

        return layout;
    }

    protected void createMenu() {
        menu.setOrientation(Tabs.Orientation.VERTICAL);
        menu.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        menu.setId("tabs");

        RouteConfiguration.forSessionScope()
                .getAvailableRoutes().stream()
                .filter(d -> getClass().getAnnotatedSuperclass().getType().equals(d.getParentLayout()))
                .forEach(r -> menu.add(createTab(r)));

        addMenu();

        if (userInfo != null) {
            menu.add(new Tab(logoutLink));
        }
    }

    public abstract void addMenu();

    protected Tab createTab(RouteData entry) {
        final Tab result = new Tab();

        RouterLink link = new RouterLink(i18nReadPageTitle(entry.getNavigationTarget()), entry.getNavigationTarget());

        result.add(link);
        ComponentUtil.setData(result, Class.class, entry.getNavigationTarget());

        return result;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        log.trace("before enter. session='{}', ui='{}', layout='{}', router='{}', target='{}', allowed='{}'",
                event.getUI().getSession().getSession().getId(),
                System.identityHashCode(event.getUI()),
                System.identityHashCode(this),
                System.identityHashCode(event.getSource()),
                event.getNavigationTarget().getSimpleName(),
                permissionChecker.hasPermission(event.getNavigationTarget(), userInfo)
        );

        String title = i18nReadPageTitle(event.getNavigationTarget());

        viewTitle.setText(title);
    }

    private String i18nReadPageTitle(final Class<?> target) {
        String result;

        if (target.isAnnotationPresent(PageTitle.class)) {
            result = getTranslation(UI.getCurrent().getLocale(), target.getAnnotation(PageTitle.class).value());
        } else {
            result = target.getSimpleName().replace("View", "");
        }

        return result;
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        if (LogoutView.class.isAssignableFrom(event.getNavigationTarget())) {
            if (userInfo != null) {
                log.info("Closing session. session='{}'", event.getUI().getSession().getSession().getId());

                event.getUI().getPage().setLocation(quarkusLogoutPage);
                event.getUI().close();
                event.getUI().getSession().close();
            } else {
                event.getUI().getPage().getHistory().go(0);
                event.getUI().getPage().reload();
            }
        }

        permissionChecker.denyEntryToProtectedPagesWithoutPermission(event.getNavigationTarget(), event.getUI(), userInfo);
    }
}
