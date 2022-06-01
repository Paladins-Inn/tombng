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

import com.vaadin.flow.server.*;
import com.vaadin.quarkus.annotation.VaadinServiceEnabled;
import com.vaadin.quarkus.annotation.VaadinServiceScoped;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * VaadinSessionListener --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-05-29
 */
@VaadinServiceEnabled
@VaadinServiceScoped
@Slf4j
public class VaadinSessionListener implements SessionInitListener, SessionDestroyListener {

    @Inject
    VaadinErrorHandler errorHandler;

    @Produces
    public VaadinSession session() {
        return VaadinSession.getCurrent();
    }

    @Override
    public void sessionDestroy(SessionDestroyEvent event) {
        log.debug("Session destroyed. session='{}'", event.getSession().getSession().getId());
        MDC.remove("session");
    }

    @Override
    public void sessionInit(SessionInitEvent event) throws ServiceException {
        MDC.put("session", event.getSession().getSession().getId());

        log.debug("Session initialized. session='{}'", event.getSession().getSession().getId());
        event.getSession().getSession().getAttributeNames().stream()
                .forEach(a -> log.trace("Session attribute. key='{}', value={}", a, event.getSession().getSession().getAttribute(a)));
        event.getService().getContext().getContextParameterNames().asIterator().forEachRemaining(
                a -> log.trace("Context parameter. key='{}', value={}", a, event.getService().getContext().getContextParameter(a))
        );

        event.getSession().setErrorHandler(errorHandler);
    }
}
