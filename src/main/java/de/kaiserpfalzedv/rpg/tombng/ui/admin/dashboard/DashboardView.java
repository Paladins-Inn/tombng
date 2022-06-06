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

package de.kaiserpfalzedv.rpg.tombng.ui.admin.dashboard;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import de.kaiserpfalzedv.rpg.tombng.ui.AdminLayout;
import de.kaiserpfalzedv.rpg.tombng.ui.I18nKeys;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.Dependent;

/**
 * Dashboard --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-06-05
 */
@PageTitle(I18nKeys.VIEWS_ADMIN_DASHBOARD)
@Route(value = "dashboard", layout = AdminLayout.class)
@RouteAlias(value = "", layout = AdminLayout.class)
@RolesAllowed({"admin"})
@Dependent
@Slf4j
public class DashboardView extends FormLayout {

    @PostConstruct
    public void init() {
        add(new Label("Hier kommt ein geiles Admin Dashboard hin."));
    }
}
