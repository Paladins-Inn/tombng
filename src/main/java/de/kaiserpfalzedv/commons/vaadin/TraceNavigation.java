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

package de.kaiserpfalzedv.commons.vaadin;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.*;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.LoggerFactory;

import java.security.Principal;

/**
 * AbstractBaseForm -- Base Form with tracing.
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-05-29
 */
public interface TraceNavigation extends BeforeEnterObserver, BeforeLeaveObserver, AfterNavigationObserver {

    @Override
    default void afterNavigation(AfterNavigationEvent event) {
        UI ui = UI.getCurrent();
        LoggerFactory.getLogger(getClass()).trace("Navigation finished. session='{}', ui='{}', component='{}'",
                ui.getSession().getSession().getId(),
                ui.getId(),
                this.getClass().getSimpleName()
        );
    }

    @Override
    default void beforeEnter(BeforeEnterEvent event) {
        LoggerFactory.getLogger(getClass()).trace("Entering. session='{}', ui='{}', component='{}'",
                event.getUI().getSession().getSession().getId(),
                event.getUI().getId(),
                this.getClass().getSimpleName()
        );

        event.getUI().getSession().setAttribute(
                Principal.class,
                (JsonWebToken) event.getUI().getSession().getSession().getAttribute("idToken"));
    }

    @Override
    default void beforeLeave(BeforeLeaveEvent event) {
        LoggerFactory.getLogger(getClass()).trace("Leaving. session='{}', ui='{}', component='{}'",
                event.getUI().getSession().getSession().getId(),
                event.getUI().getId(),
                this.getClass().getSimpleName()
        );
    }
}
