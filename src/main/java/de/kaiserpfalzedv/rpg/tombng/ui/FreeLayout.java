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

package de.kaiserpfalzedv.rpg.tombng.ui;

import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.quarkus.annotation.UIScoped;
import de.kaiserpfalzedv.commons.vaadin.layouts.AbstractBaseLayout;
import de.kaiserpfalzedv.commons.vaadin.security.PermissionHolding;
import io.quarkus.arc.Unremovable;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.security.PermitAll;

/**
 * FreeLayout --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-06-01
 */
@Unremovable
@UIScoped
@RoutePrefix("free")
@PermitAll
@Slf4j
public class FreeLayout extends AbstractBaseLayout implements PermissionHolding {
    @Override
    public void addMenu() {
        RouteConfiguration.forSessionScope()
                .getAvailableRoutes().stream()
                .filter(d -> UserLayout.class.equals(d.getParentLayout()))
                .forEach(r -> menu.add(createTab(r)));

        if (userInfo != null) {
            RouteConfiguration.forSessionScope()
                    .getAvailableRoutes().stream()
                    .filter(d -> AdminLayout.class.equals(d.getParentLayout()))
                    .forEach(r -> menu.add(createTab(r)));
        }
    }
}
