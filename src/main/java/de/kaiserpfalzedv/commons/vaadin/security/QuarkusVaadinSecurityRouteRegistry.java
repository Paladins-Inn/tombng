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

package de.kaiserpfalzedv.commons.vaadin.security;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.RouteData;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RoutesChangedListener;
import com.vaadin.flow.router.internal.NavigationRouteTarget;
import com.vaadin.flow.router.internal.RouteTarget;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.server.RouteRegistry;
import com.vaadin.flow.server.VaadinContext;
import com.vaadin.flow.shared.Registration;
import de.kaiserpfalzedv.commons.vaadin.profile.ProfileProducer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * QuarkusVaadinSecurityRouteRegistry --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-06-07
 */
@AllArgsConstructor
@Slf4j
public class QuarkusVaadinSecurityRouteRegistry implements RouteRegistry {

    private RouteRegistry registry;
    private PermissionChecker permissionChecker;

    private ProfileProducer profileProducer;


    @Override
    public void setRoute(String path, Class<? extends Component> navigationTarget, List<Class<? extends RouterLayout>> parentChain) {
        registry.setRoute(path, navigationTarget, parentChain);
    }

    @Override
    public void removeRoute(Class<? extends Component> navigationTarget) {
        registry.removeRoute(navigationTarget);
    }

    @Override
    public void removeRoute(String path) {
        registry.removeRoute(path);
    }

    @Override
    public void removeRoute(String path, Class<? extends Component> navigationTarget) {
        registry.removeRoute(path, navigationTarget);
    }

    @Override
    public List<RouteData> getRegisteredRoutes() {
        return registry.getRegisteredRoutes().stream()
                .filter(d -> permissionChecker.hasPermission(d.getNavigationTarget(), profileProducer.profile()))
                .collect(Collectors.toList());
    }

    @Override
    public NavigationRouteTarget getNavigationRouteTarget(String url) {
        return registry.getNavigationRouteTarget(url);
    }

    @Override
    public RouteTarget getRouteTarget(Class<? extends Component> target, RouteParameters parameters) {
        return registry.getRouteTarget(target, parameters);
    }

    @Override
    public Optional<Class<? extends Component>> getNavigationTarget(String url) {
        return registry.getNavigationTarget(url);
    }

    @Override
    public Optional<Class<? extends Component>> getNavigationTarget(String url, List<String> segments) {
        return registry.getNavigationTarget(url, segments);
    }

    @Override
    public Optional<String> getTargetUrl(Class<? extends Component> navigationTarget) {
        return registry.getTargetUrl(navigationTarget);
    }

    @Override
    public Optional<String> getTargetUrl(Class<? extends Component> navigationTarget, RouteParameters parameters) {
        return registry.getTargetUrl(navigationTarget, parameters);
    }

    @Override
    public Optional<String> getTemplate(Class<? extends Component> navigationTarget) {
        return registry.getTemplate(navigationTarget);
    }

    @Override
    @Deprecated
    public List<Class<? extends RouterLayout>> getRouteLayouts(String path, Class<? extends Component> navigationTarget) {
        return registry.getRouteLayouts(path, navigationTarget);
    }

    @Override
    public void update(Command command) {
        registry.update(command);
    }

    @Override
    public Registration addRoutesChangeListener(RoutesChangedListener listener) {
        return registry.addRoutesChangeListener(listener);
    }

    @Override
    public void clean() {
        registry.clean();
    }

    @Override
    public VaadinContext getContext() {
        return registry.getContext();
    }

    @Override
    public boolean hasMandatoryParameter(Class<? extends Component> navigationTarget) {
        return registry.hasMandatoryParameter(navigationTarget);
    }
}
