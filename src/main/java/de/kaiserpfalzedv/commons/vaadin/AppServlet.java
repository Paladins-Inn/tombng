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
import com.vaadin.quarkus.QuarkusVaadinServlet;
import io.quarkus.arc.Unremovable;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

/**
 * AppServlet --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-05-28
 */
@Slf4j
@ApplicationScoped
@Unremovable
@WebServlet(
        urlPatterns = {"/*", "/frontend/*"},
        name = "Vaadin UI",
        asyncSupported = true
)
public class AppServlet extends QuarkusVaadinServlet implements SessionInitListener, SessionDestroyListener {

    public AppServlet() {
        log.info("Created AppServlet for Vaadin");
    }

    @Override
    public void servletInitialized() throws ServletException {
        super.servletInitialized();

        getService().addSessionInitListener(this);
        getService().addSessionDestroyListener(this);
    }


    @Override
    public void sessionDestroy(SessionDestroyEvent event) {
        log.trace("session destroy event. session='{}', duration='{}'",
                event.getSession().getSession().getId(),
                event.getSession().getCumulativeRequestDuration());
    }

    @Override
    public void sessionInit(SessionInitEvent event) throws ServiceException {
        log.trace("session init event. session='{}', principal='{}'",
                event.getSession().getSession().getId(),
                event.getRequest().getUserPrincipal().getName()
        );
    }
}
