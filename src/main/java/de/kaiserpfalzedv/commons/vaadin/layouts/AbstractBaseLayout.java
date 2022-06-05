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

import com.google.common.eventbus.Subscribe;
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
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.*;
import de.kaiserpfalzedv.commons.vaadin.EventBusRegistered;
import de.kaiserpfalzedv.commons.vaadin.security.PermissionHolding;
import de.kaiserpfalzedv.commons.vaadin.security.servlet.UserDetails;
import de.kaiserpfalzedv.commons.vaadin.views.logout.LogoutView;
import de.kaiserpfalzedv.rpg.tombng.views.app.profile.ProfileView;
import io.quarkus.security.Authenticated;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.util.Arrays;

/**
 * AbstractBaseLayout --
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

    @ConfigProperty(name = "quarkus.oidc.authentication.redirect-path", defaultValue = "/ui/app/")
    String applicationLoginPage;

    @Inject
    @ToString.Include
    protected UserDetails userInfo;


    protected final HorizontalLayout header = new HorizontalLayout();
    protected final H1 viewTitle = new H1();

    protected final Avatar avatar = new Avatar();
    protected final MenuBar menuBar = new MenuBar();
    protected final MenuItem avatarMenuItem = menuBar.addItem(avatar);
    protected final SubMenu avatarContextMenu = avatarMenuItem.getSubMenu();
    protected final AvatarGroup loggedInUsers = new AvatarGroup();
    protected final Tabs menu = new Tabs();

    protected final RouterLink logoutLink = new RouterLink("Logout", LogoutView.class);


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
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        header.add(new DrawerToggle());

        viewTitle.setWidth(70f, Unit.PERCENTAGE);
        header.add(viewTitle);

        header.add(loggedInUsers);

        if (userInfo != null) {
            avatar.setName(userInfo.getName());
            avatar.setImage(userInfo.getImage());
        }

        header.add(menuBar);
        avatarContextMenu.addItem(new RouterLink("Profile", ProfileView.class));
        avatarContextMenu.addItem("Settings");
        avatarContextMenu.addItem("Help");
        avatarContextMenu.addItem(new RouterLink("Logout", LogoutView.class));


        return header;
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

        RouterLink link = new RouterLink(entry.getTemplate(), entry.getNavigationTarget());

        PageTitle pt = entry.getNavigationTarget().getAnnotation(PageTitle.class);
        if (pt != null) link.setText(pt.value());

        result.add(link);
        ComponentUtil.setData(result, Class.class, entry.getNavigationTarget());

        return result;
    }

    protected Tab createTab(String text, String uri) {
        final Tab result = new Tab();

        Anchor link = new Anchor(uri, text);
        result.add(link);

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
                hasPermission(event.getNavigationTarget())
        );

        viewTitle.setText(event.getNavigationTarget().getAnnotation(PageTitle.class).value());
    }

    /**
     * Checks if the target is permitted for the current logged in user.
     *
     * @param target class to check for annotations.
     * @return if the class is denied for everyone.
     */
    private boolean hasPermission(Class<?> target) {
        return !isDeniedToAll(target)
                && !needsAdditionalAuthentication(target)
                && (isPermittedForAll(target) || userHasRole(target));
    }

    /**
     * If the target is annotated with {@link DenyAll} nobody is allowed to access the target.
     *
     * @param target the class to check for annotations.
     * @return TRUE if the class is denied for everyone.
     */
    private boolean isDeniedToAll(Class<?> target) {
        boolean result = target.isAnnotationPresent(DenyAll.class);

        log.trace("checked for deny-all annotation. view='{}', deny-all='{}'",
                target.getSimpleName(),
                result
        );

        return result;
    }


    /**
     * A target is permitted for all when it either has the {@link PermitAll} annotation or has no security annotation
     * at all.
     *
     * @param target The class to check for the annotation.
     * @return TRUE if there are no access restrictions at all.
     */
    private boolean isPermittedForAll(Class<?> target) {
        boolean result = target.isAnnotationPresent(PermitAll.class)
                || (!target.isAnnotationPresent(DenyAll.class) && !target.isAnnotationPresent(RolesAllowed.class));

        log.trace("checked for permit-all annotation. view='{}', permit-all='{}'",
                target.getSimpleName(),
                result
        );

        return result;
    }

    private boolean userHasRole(Class<?> target) {
        String[] rolesAllowed = target.getAnnotation(RolesAllowed.class) != null ?
                target.getAnnotation(RolesAllowed.class).value()
                : new String[0];

        log.trace("checking for roles-allowed. view='{}', roles-allowed='{}'",
                target.getSimpleName(),
                rolesAllowed
        );

        return (userInfo != null)
                && Arrays.stream(rolesAllowed).anyMatch(r -> userInfo.isUserInRole(r));
    }

    private boolean needsAdditionalAuthentication(Class<?> target) {
        boolean result = (userInfo == null) && target.isAnnotationPresent(Authenticated.class);

        log.trace("checking for authentication required. view='{}', authenticated='{}'", target.getSimpleName(), result);

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

        denyEntryToProtectedPagesWithoutPermission(event.getNavigationTarget(), event.getUI());
    }

    private void denyEntryToProtectedPagesWithoutPermission(Class<?> target, UI ui) {
        if (!hasPermission(target)) {
            log.error("user has no permission to enter the page. session='{}', target='{}'",
                    ui.getSession().getSession().getId(),
                    target.getSimpleName()
            );

            if (userInfo != null) {
                log.info("User has no permission for the target page. Stay on this page!");
                Notification.show("Sorry, you don't have access to this page!");
                ui.getPage().getHistory().go(0);
            } else {
                log.info("User is not logged in. Will be redirected to login page for protected page access.");
                ui.getPage().setLocation(applicationLoginPage);
                ui.close();
                ui.getSession().close();
                log.trace("Redirected ...");
            }
        }
    }


    @Subscribe
    public void setUserInfo(final UserDetails userInfo) {
        this.userInfo = userInfo;

        if (userInfo != null) {
            avatar.setName(userInfo.getName());
            avatar.setImage(userInfo.getImage());
        }
    }
}
