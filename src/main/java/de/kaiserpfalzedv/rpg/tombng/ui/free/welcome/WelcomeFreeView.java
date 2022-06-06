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

package de.kaiserpfalzedv.rpg.tombng.ui.free.welcome;

import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import de.kaiserpfalzedv.rpg.tombng.ui.FreeLayout;
import de.kaiserpfalzedv.rpg.tombng.ui.I18nKeys;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.annotation.security.PermitAll;
import javax.enterprise.context.Dependent;

/**
 * WelcomeFreeView --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-06-01
 */
@PageTitle(I18nKeys.VIEWS_FREE_WELCOME)
@Route(value = "welcome", layout = FreeLayout.class)
@RouteAlias(value = "", layout = FreeLayout.class)
@Dependent
@PermitAll
@Slf4j
public class WelcomeFreeView extends VerticalLayout {
    @ConfigProperty(name = "application.name")
    String appName;

    @PostConstruct
    @PermitAll
    public void init() {
        add(new Label(appName));
    }

}
