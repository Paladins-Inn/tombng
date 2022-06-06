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

package de.kaiserpfalzedv.rpg.tombng.ui.app.about;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.kaiserpfalzedv.rpg.tombng.ui.I18nKeys;
import de.kaiserpfalzedv.rpg.tombng.ui.UserLayout;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.Dependent;

/**
 * AboutView --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-06-01
 */
@PageTitle(I18nKeys.VIEWS_APP_ABOUT)
@Route(value = "about", layout = UserLayout.class)
@Dependent
@RolesAllowed({"user", "admin", "operator", "dev"})
@Slf4j
public class AboutView extends VerticalLayout {
    @ConfigProperty(name = "application.name")
    String appName;

    @PostConstruct
    @RolesAllowed({"user", "admin", "editor"})
    public void init() {
        add(new Label(appName));
    }
}
