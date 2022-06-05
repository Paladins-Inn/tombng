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

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.PollEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.quarkus.QuarkusVaadinServlet;
import com.vaadin.quarkus.QuarkusVaadinServletService;
import io.quarkus.security.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.inject.spi.BeanManager;

/**
 * QuarkusVaadinSecurityServletService --
 *
 * @author klenkes74 {@literal <rlichti@kaiserpfalz-edv.de>}
 * @since 2.0.0  2022-06-04
 */
@Slf4j
public class QuarkusVaadinSecurityServletService
        extends QuarkusVaadinServletService
        implements BeforeEnterListener, BeforeLeaveListener, AfterNavigationListener,
        ComponentEventListener<PollEvent> {

    private BeanManager beanManager;

    private String applicationLoginPage;

    private QuarkusSecurityRouter router;

    public QuarkusVaadinSecurityServletService(
            QuarkusVaadinServlet servlet,
            DeploymentConfiguration configuration,
            BeanManager beanManager,
            String applicationLoginPage) {
        super(servlet, configuration, beanManager);

        this.beanManager = beanManager;
        this.applicationLoginPage = applicationLoginPage;

        log.debug("Using KesUI based UIService. beanManager='{}'", System.identityHashCode(this.beanManager));
    }

    @Override
    public void init() throws ServiceException {
        this.router = new QuarkusSecurityRouter(getRouteRegistry());

        super.init();
    }

    public void handleRequest(VaadinRequest request, VaadinResponse response)
            throws ServiceException {
        try {
            super.handleRequest(request, response);
        } catch (UnauthorizedException cause) {
            log.info("User not logged in, redirecting to '{}': {}", applicationLoginPage, cause.getMessage());

            response.setStatus(302);
            response.setContentType("text/html");
            response.setHeader("Location", applicationLoginPage);
        }
    }

    @Override
    public Router getRouter() {
        return router;
    }


    @Override
    public void fireUIInitListeners(UI ui) {
        addUIListeners(ui);
        super.fireUIInitListeners(ui);
    }

    private void addUIListeners(UI ui) {
        log.trace("Adding this service as navigation listeners. session='{}', service='{}', main-div='{}'",
                ui.getSession().getSession().getId(),
                System.identityHashCode(this),
                getMainDivId(ui.getSession(), getCurrentRequest())
        );
        ui.addAfterNavigationListener(this);
        ui.addBeforeLeaveListener(this);
        ui.addBeforeEnterListener(this);
        ui.addPollListener(this);
    }

    @Override
    public void onComponentEvent(PollEvent event) {
        log.trace("poll event. session='{}', ui='{}', element='{}'",
                getCurrentServletRequest().getSession().getId(),
                event.getSource().getId(),
                event.getSource().getElement().getClass().getSimpleName()
        );
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        log.trace("after navigation. session='{}', ui='{}', element='{}'",
                getCurrentServletRequest().getSession().getId(),
                event.getLocation().getPathWithQueryParameters(),
                event.getSource().getClass().getSimpleName()
        );
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        log.trace("before enter. session='{}', ui='{}', element='{}'",
                getCurrentServletRequest().getSession().getId(),
                event.getLocation().getPathWithQueryParameters(),
                event.getSource().getClass().getSimpleName()
        );
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        log.trace("before leave. session='{}', ui='{}', element='{}'",
                getCurrentServletRequest().getSession().getId(),
                event.getLocation().getPathWithQueryParameters(),
                event.getSource().getClass().getSimpleName()
        );
    }
}
