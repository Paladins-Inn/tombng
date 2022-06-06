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

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.internal.BeforeEnterHandler;
import de.kaiserpfalzedv.commons.vaadin.i18n.DefaultComponentsI18nKeys;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.Dependent;

/**
 * LogoutView --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-06-01
 */
@PageTitle(DefaultComponentsI18nKeys.ACTIONS_LOGOUT)
@Route(value = "logout")
@Dependent
@PermitAll
@Slf4j
public class LogoutView extends Div implements BeforeEnterHandler {
    @ConfigProperty(name = "quarkus.oidc.logout.path", defaultValue = "/logout")
    String quarkusLogoutPage;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        log.info("Closing session. session='{}'", event.getUI().getSession().getSession().getId());
        event.getUI().getSession().close();
        event.getUI().getSession().getSession().invalidate();
        event.getUI().getPage().setLocation(quarkusLogoutPage);
        event.getUI().close();
    }
}
